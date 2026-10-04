/*
 * A Java runtime that comes as an archive instead of being installed.
 *
 * FreeJ2ME's Java app is started with "java" (javaw on Windows) from PATH, so
 * the core works only where a Java runtime is installed and on PATH - which a
 * frontend in a sandbox, a handheld or a portable setup often does not have.
 * An archive of a Java runtime put in the system directory (as distributed:
 * Adoptium/Temurin's .tar.gz for Linux and macOS, .zip for Windows) is
 * unpacked next to it once, and the java in it is used from then on - by both
 * FreeJ2ME cores, which look in the same place. A different archive (another
 * size or date) is unpacked again.
 *
 * Unpacking is left to the system's tar, which every platform the core runs on
 * has: GNU tar on Linux, bsdtar on macOS and on Windows 10 and later, the
 * latter two reading .zip as well.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#if defined(_WIN32)
#include <windows.h>
#include <direct.h>
#else
#include <dirent.h>
#include <unistd.h>
#include <sys/wait.h>
#endif

#include "bundled_jre.h"

#if defined(_WIN32)
#define SLASH "\\"
#define JAVA_EXE "javaw.exe"
#else
#define SLASH "/"
#define JAVA_EXE "java"
#endif

static const char *archive_names[] = { "jre.tar.gz", "jre.tgz", "jre.tar.xz", "jre.zip", NULL };

static bool file_exists(const char *path)
{
	struct stat st;
	return stat(path, &st) == 0;
}

static bool is_dir(const char *path)
{
	struct stat st;
	return stat(path, &st) == 0 && (st.st_mode & S_IFDIR);
}

static void make_dirs(const char *path)
{
	char tmp[BUNDLED_JRE_PATH_MAX];
	size_t i, len;

	snprintf(tmp, sizeof(tmp), "%s", path);
	len = strlen(tmp);
	for (i = 1; i <= len; i++)
	{
		if (tmp[i] == '/' || tmp[i] == '\\' || tmp[i] == '\0')
		{
			char c = tmp[i];
			tmp[i] = '\0';
			if (!is_dir(tmp))
			{
#if defined(_WIN32)
				_mkdir(tmp);
#else
				mkdir(tmp, 0755);
#endif
			}
			tmp[i] = c;
		}
	}
}

/* Deletes a directory and everything in it; a previous unpack, or one that
 * was interrupted. Symbolic links are removed, not followed. */
static void remove_tree(const char *path)
{
#if defined(_WIN32)
	char pattern[BUNDLED_JRE_PATH_MAX];
	WIN32_FIND_DATAA fd;
	HANDLE h;

	snprintf(pattern, sizeof(pattern), "%s\\*", path);
	h = FindFirstFileA(pattern, &fd);
	if (h != INVALID_HANDLE_VALUE)
	{
		do
		{
			char child[BUNDLED_JRE_PATH_MAX];
			if (!strcmp(fd.cFileName, ".") || !strcmp(fd.cFileName, ".."))
				continue;
			snprintf(child, sizeof(child), "%s\\%s", path, fd.cFileName);
			if (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
				remove_tree(child);
			else
			{
				SetFileAttributesA(child, FILE_ATTRIBUTE_NORMAL);
				DeleteFileA(child);
			}
		} while (FindNextFileA(h, &fd));
		FindClose(h);
	}
	RemoveDirectoryA(path);
#else
	DIR *dir = opendir(path);
	struct dirent *entry;

	if (dir)
	{
		while ((entry = readdir(dir)) != NULL)
		{
			char child[BUNDLED_JRE_PATH_MAX];
			struct stat st;
			if (!strcmp(entry->d_name, ".") || !strcmp(entry->d_name, ".."))
				continue;
			snprintf(child, sizeof(child), "%s/%s", path, entry->d_name);
			if (lstat(child, &st) == 0 && S_ISDIR(st.st_mode))
				remove_tree(child);
			else
				unlink(child);
		}
		closedir(dir);
	}
	rmdir(path);
#endif
}

/* Runs the system's tar on the archive, into dest; true when it succeeded. */
static bool run_tar(const char *archive, const char *dest)
{
#if defined(_WIN32)
	char cmd[BUNDLED_JRE_PATH_MAX * 2 + 32];
	STARTUPINFOA si;
	PROCESS_INFORMATION pi;
	DWORD code = 1;

	snprintf(cmd, sizeof(cmd), "tar.exe -xf \"%s\" -C \"%s\"", archive, dest);
	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	ZeroMemory(&pi, sizeof(pi));
	if (!CreateProcessA(NULL, cmd, NULL, NULL, FALSE, CREATE_NO_WINDOW, NULL, NULL, &si, &pi))
		return false;
	WaitForSingleObject(pi.hProcess, INFINITE);
	GetExitCodeProcess(pi.hProcess, &code);
	CloseHandle(pi.hProcess);
	CloseHandle(pi.hThread);
	return code == 0;
#else
	int status = 0;
	pid_t pid = fork();

	if (pid == 0)
	{
		execlp("tar", "tar", "-xf", archive, "-C", dest, (char*)NULL);
		_exit(127);
	}
	if (pid < 0 || waitpid(pid, &status, 0) < 0)
		return false;
	return WIFEXITED(status) && WEXITSTATUS(status) == 0;
#endif
}

/* The java in an unpacked runtime: at its top, in the single directory the
 * archive puts everything in (jdk-21.0.4+7-jre/), or in that directory's
 * Contents/Home on macOS. */
static bool find_java(const char *root, char *out, size_t out_len)
{
	static const char *inside[] = { SLASH "bin" SLASH JAVA_EXE,
		SLASH "Contents" SLASH "Home" SLASH "bin" SLASH JAVA_EXE, NULL };
	int i;

	snprintf(out, out_len, "%s%s", root, inside[0]);
	if (file_exists(out))
		return true;

#if defined(_WIN32)
	{
		char pattern[BUNDLED_JRE_PATH_MAX];
		WIN32_FIND_DATAA fd;
		HANDLE h;
		bool found = false;

		snprintf(pattern, sizeof(pattern), "%s\\*", root);
		h = FindFirstFileA(pattern, &fd);
		if (h == INVALID_HANDLE_VALUE)
			return false;
		do
		{
			if (!(fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) || fd.cFileName[0] == '.')
				continue;
			for (i = 0; inside[i] && !found; i++)
			{
				snprintf(out, out_len, "%s\\%s%s", root, fd.cFileName, inside[i]);
				found = file_exists(out);
			}
		} while (!found && FindNextFileA(h, &fd));
		FindClose(h);
		return found;
	}
#else
	{
		DIR *dir = opendir(root);
		struct dirent *entry;
		bool found = false;

		if (!dir)
			return false;
		while (!found && (entry = readdir(dir)) != NULL)
		{
			if (entry->d_name[0] == '.')
				continue;
			for (i = 0; inside[i] && !found; i++)
			{
				snprintf(out, out_len, "%s/%s%s", root, entry->d_name, inside[i]);
				found = file_exists(out);
			}
		}
		closedir(dir);
		return found;
	}
#endif
}

bool bundled_jre_find(const char *archiveDir, const char *unpackDir,
	char *java, size_t java_len, retro_environment_t environ_cb, retro_log_printf_t log)
{
	char archive[BUNDLED_JRE_PATH_MAX], jreDir[BUNDLED_JRE_PATH_MAX], tmpDir[BUNDLED_JRE_PATH_MAX];
	char stampPath[BUNDLED_JRE_PATH_MAX], stamp[BUNDLED_JRE_PATH_MAX], current[BUNDLED_JRE_PATH_MAX];
	const char *name = NULL;
	struct stat st;
	FILE *f;
	int i;

	for (i = 0; archive_names[i]; i++)
	{
		snprintf(archive, sizeof(archive), "%s%s%s", archiveDir, SLASH, archive_names[i]);
		if (stat(archive, &st) == 0 && !(st.st_mode & S_IFDIR))
		{
			name = archive_names[i];
			break;
		}
	}
	if (!name)
		return false;

	snprintf(jreDir, sizeof(jreDir), "%s%sjre", unpackDir, SLASH);
	snprintf(tmpDir, sizeof(tmpDir), "%s%sjre.tmp", unpackDir, SLASH);
	snprintf(stampPath, sizeof(stampPath), "%s%s.source", jreDir, SLASH);
	snprintf(stamp, sizeof(stamp), "%s %lld %lld", name, (long long)st.st_size, (long long)st.st_mtime);

	/* Unpacked from this same archive already */
	current[0] = '\0';
	if ((f = fopen(stampPath, "r")) != NULL)
	{
		if (!fgets(current, sizeof(current), f))
			current[0] = '\0';
		fclose(f);
	}
	if (!strcmp(current, stamp) && find_java(jreDir, java, java_len))
	{
		log(RETRO_LOG_INFO, "Using the Java runtime unpacked from %s: %s\n", name, java);
		return true;
	}

	log(RETRO_LOG_INFO, "Unpacking the Java runtime from %s into %s\n", archive, jreDir);
	if (environ_cb)
	{
		struct retro_message_ext msg = { "Unpacking the Java runtime...", 3000, 1, RETRO_LOG_INFO,
			RETRO_MESSAGE_TARGET_ALL, RETRO_MESSAGE_TYPE_NOTIFICATION, -1 };
		environ_cb(RETRO_ENVIRONMENT_SET_MESSAGE_EXT, &msg);
	}

	/* Into a directory of its own first, so that an interrupted unpack never
	 * passes for a complete one */
	remove_tree(tmpDir);
	make_dirs(tmpDir);
	if (!run_tar(archive, tmpDir))
	{
		log(RETRO_LOG_ERROR, "Could not unpack %s (is tar available?)\n", archive);
		remove_tree(tmpDir);
		return false;
	}
	if (!find_java(tmpDir, java, java_len))
	{
		log(RETRO_LOG_ERROR, "%s holds no " JAVA_EXE " in bin/\n", archive);
		remove_tree(tmpDir);
		return false;
	}
	remove_tree(jreDir);
	if (rename(tmpDir, jreDir) != 0)
	{
		log(RETRO_LOG_ERROR, "Could not move the unpacked Java runtime to %s\n", jreDir);
		remove_tree(tmpDir);
		return false;
	}
	if ((f = fopen(stampPath, "w")) != NULL)
	{
		fputs(stamp, f);
		fclose(f);
	}
	if (!find_java(jreDir, java, java_len))
		return false;
	log(RETRO_LOG_INFO, "Using the Java runtime unpacked from %s: %s\n", name, java);
	return true;
}
