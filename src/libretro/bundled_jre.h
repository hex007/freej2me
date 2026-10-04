#ifndef BUNDLED_JRE_H
#define BUNDLED_JRE_H

#include <stdbool.h>
#include <stddef.h>

#include "libretro.h"

#define BUNDLED_JRE_PATH_MAX 4096

/* Looks for a Java runtime archive (jre.tar.gz, jre.tgz, jre.tar.xz or
 * jre.zip) in archiveDir, unpacks it into unpackDir/jre unless that holds this
 * archive's contents already, and writes the java executable in it (javaw.exe
 * on Windows) to java. Returns false when there is no archive or it could not
 * be used; the java on PATH is the one to start then. */
bool bundled_jre_find(const char *archiveDir, const char *unpackDir,
	char *java, size_t java_len, retro_environment_t environ_cb, retro_log_printf_t log);

#endif
