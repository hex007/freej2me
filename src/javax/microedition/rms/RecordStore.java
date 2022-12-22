/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package javax.microedition.rms;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Vector;
import java.util.Arrays;

import org.recompile.mobile.Mobile;

public class RecordStore
{

	public static final int AUTHMODE_ANY = 1;
	public static final int AUTHMODE_PRIVATE = 0;


	private String name;

	private String appname;

	private static String rmsPath;

	private String rmsFile;

	private File file;

	private int version = 0;

	private int nextid = 0;

	private Vector<byte[]> records;

	private Vector<RecordListener> listeners;

	private int lastModified = 0;

	private RecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException, RecordStoreNotFoundException
	{
		//System.out.println("> RecordStore "+recordStoreName);

		records = new Vector<byte[]>();
		listeners = new Vector<RecordListener>();

		records.add(new byte[]{}); // dummy record (record ids start at 1)

		int count;
		int offset;
		int reclen;

		name = recordStoreName;

		appname = Mobile.getPlatform().loader.suitename;

		rmsPath = Mobile.getPlatform().dataPath + "./rms/"+appname;
		rmsFile = Mobile.getPlatform().dataPath + "./rms/"+appname+"/"+recordStoreName;

		try
		{
			Files.createDirectories(Paths.get(rmsPath));
		}
		catch (Exception e)
		{
			//System.out.println("> Problem Creating Record Store Path: "+rmsPath);
			System.out.println(e.getMessage());
			throw(new RecordStoreException("Problem Creating Record Store Path "+rmsPath));
		}
		file = new File(rmsFile);
		if(!file.exists())
		{
			if(!createIfNecessary)
			{
				throw (new RecordStoreNotFoundException("Record Store Doesn't Exist: " + rmsFile));
			}

			try // Check Record Store File
			{
				//System.out.println("> Creating New Record Store "+appname+"/"+recordStoreName);
				file.createNewFile();
				version = 1;
				nextid = 1;
				count = 0;
				save();
				nextid = 1;
			}
			catch (Exception e)
			{
				//System.out.println("> Problem Opening Record Store (createIfNecessary "+createIfNecessary+"): "+rmsFile);
				System.out.println(e.getMessage());
				throw(new RecordStoreException("Problem Opening Record Store (createIfNecessary "+createIfNecessary+"): "+rmsFile));
			}
		}

		try // Read Records
		{
			Path path = Paths.get(file.getAbsolutePath());
			byte[] data = Files.readAllBytes(path);

			if(data.length>=4)
			{
				offset = 0;
				version = getUInt16(data, offset); offset+=2;
				nextid = getUInt16(data, offset); offset+=2;
				count = getUInt16(data, offset); offset+=2;
				for(int i=0; i<count; i++)
				{
					reclen = getUInt16(data, offset);
					offset+=2;

					loadRecord(data, offset, reclen);
					offset+=reclen;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Problem Reading Record Store: "+rmsFile);
			System.out.println(e.getMessage());
			throw(new RecordStoreException("Problem Reading Record Store: "+rmsFile));
		}
	}

	private void save()
	{
		byte[] temp = new byte[2];
		try
		{
			FileOutputStream fout = new FileOutputStream(rmsFile);

			// version //
			setUInt16(temp, 0, version);
			fout.write(temp);
			// next record id //
			setUInt16(temp, 0, nextid);
			fout.write(temp);
			// record count //
			setUInt16(temp, 0, records.size()-1);
			fout.write(temp);

			// records //
			for(int i=1; i<records.size(); i++)
			{
				setUInt16(temp, 0, records.get(i).length);
				fout.write(temp);
				fout.write(records.get(i));
			}

			fout.close();
		}
		catch (Exception e)
		{
			System.out.println("Problem Saving RecordStore");
			e.printStackTrace();
		}
	}

	private void loadRecord(byte[] data, int offset, int numBytes)
	{
		byte[] rec = Arrays.copyOfRange(data, offset, offset+numBytes);
		if(rec==null) { rec = new byte[]{}; }
		records.addElement(rec);
	}

	private int getUInt16(byte[] data, int offset)
	{
		int out = 0;

		out |= (((int)data[offset])   & 0xFF) << 8;
		out |= (((int)data[offset+1]) & 0xFF);

		return out | 0x00000000;
	}
	private void setUInt16(byte[] data, int offset, int val)
	{
		data[offset]   = (byte)((val>>8) & 0xFF);
		data[offset+1] = (byte)((val) & 0xFF);
	}

	public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreException
	{
		//System.out.println("> Add Record "+nextid+ " to "+name);
		try
		{
			byte[] rec = Arrays.copyOfRange(data, offset, offset+numBytes);
			records.addElement(rec);

			lastModified = nextid;
			nextid++;
			version++;

			save();

			for(int i=0; i<listeners.size(); i++)
			{
				listeners.get(i).recordAdded(this, lastModified);
			}
			return lastModified;
		}
		catch (Exception e)
		{
			//System.out.println("> Add Record Failed");
			throw(new RecordStoreException("Can't Add RMS Record"));
		}
	}

	public void addRecordListener(RecordListener listener)
	{
		listeners.add(listener);
	}

	public void closeRecordStore() { }

	public void deleteRecord(int recordId)
	{
		version++;
		//System.out.println("> Delete Record");
		records.set(recordId, new byte[]{});
		save();
		for(int i=0; i<listeners.size(); i++)
		{
			listeners.get(i).recordDeleted(this, recordId);
		}
	}

	public static void deleteRecordStore(String recordStoreName)
	{
		try
		{
			File fstore = new File(Mobile.getPlatform().dataPath + "./rms/"+Mobile.getPlatform().loader.suitename+"/"+recordStoreName);
			fstore.delete();
		}
		catch (Exception e)
		{
			System.out.println("Problem deleting RecordStore "+recordStoreName);
			e.printStackTrace();
		}
		System.gc();
	}

	public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated)
	{
		System.out.println("RecordStore.enumerateRecords");
		return new enumeration(filter, comparator, keepUpdated);
	}

	public long getLastModified() { return lastModified; }

	public String getName()
	{
		return name;
	}

	public int getNextRecordID()
	{
		//System.out.println("> getNextRecordID");
		return nextid;
	}

	public int getNumRecords()
	{
		//System.out.println("> getNumRecords");
		int count = 0;
		for(int i=1; i<records.size(); i++) // count deleted records
		{
			if(records.get(i).length==0) { count++; }
		}
		count = records.size()-(1+count);
		if(count<0) { count = 0; }
		return count;
	}

	public byte[] getRecord(int recordId) throws InvalidRecordIDException, RecordStoreException
	{
		//System.out.println("> getRecord("+recordId+")");
		if(recordId >= records.size())
		{
			//System.out.println("getRecord Invalid RecordId "+recordId);
			throw(new InvalidRecordIDException("(A) Invalid Record ID: "+recordId));
		}
		try
		{
			byte[] t = records.get(recordId);
			if(t.length==0) { return null; }
			return t;
		}
		catch (Exception e)
		{
			System.out.println("(getRecord) Record Store Exception: "+recordId);
			e.printStackTrace();
			throw(new RecordStoreException());
		}
	}

	public int getRecord(int recordId, byte[] buffer, int offset) throws InvalidRecordIDException, RecordStoreException
	{
		//System.out.println("> getRecord(id, buffer, offset)");
		byte[] temp = getRecord(recordId);

		if(temp == null)
		{
			return 0;
		}

		int len = temp.length;

		while (offset+len > buffer.length) { len--; }

		for(int i=0; i<len; i++)
		{
			buffer[offset+i] = temp[i];
		}
		return len;
	}

	public int getRecordSize(int recordId) throws InvalidRecordIDException, RecordStoreException
	{
		//System.out.println("> Get Record Size");
		return getRecord(recordId).length;
	}

	public int getSize() { return 32767; }

	public int getSizeAvailable() { return 65536; }

	public int getVersion() { return version; }

	public static String[] listRecordStores()
	{
		//System.out.println("List Record Stores");
		if(rmsPath==null)
		{
			rmsPath = Mobile.getPlatform().dataPath + "./rms/"+Mobile.getPlatform().loader.name;
			try
			{
				Files.createDirectories(Paths.get(rmsPath));
			}
			catch (Exception e) { }
		}
		try
		{
			File folder = new File(rmsPath);
			File[] files = folder.listFiles();

			String[] out = new String[files.length];

			for(int i=0; i<files.length; i++)
			{
				//System.out.println((files[i].toString()).substring(rmsPath.length()+1));
				out[i] = (files[i].toString()).substring(rmsPath.length()+1);
			}

			return out;
		}
		catch (Exception e) { }
		return null;
	}

	public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException, RecordStoreNotFoundException
	{
		//System.out.println("Open Record Store A "+createIfNecessary);
		return new RecordStore(recordStoreName, createIfNecessary);
	}

	public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary, int authmode, boolean writable) throws RecordStoreException, RecordStoreNotFoundException
	{
		//System.out.println("Open Record Store B "+createIfNecessary);
		return new RecordStore(recordStoreName, createIfNecessary);
	}

	public static RecordStore openRecordStore(String recordStoreName, String vendorName, String suiteName) throws RecordStoreException, RecordStoreNotFoundException
	{
		System.out.println("Open Record Store C");
		return new RecordStore(recordStoreName, false);
	}

	public void removeRecordListener(RecordListener listener)
	{
		listeners.remove(listener);
	}

	public void setMode(int authmode, boolean writable) {  }

	public void setRecord(int recordId, byte[] newData, int offset, int numBytes) throws RecordStoreException, InvalidRecordIDException
	{
		//System.out.println("> Set Record "+recordId+" in "+name);
		if(recordId >= records.size())
		{
			throw(new InvalidRecordIDException("(C) Invalid Record ID: "+recordId));
		}
		try
		{
			byte[] rec = Arrays.copyOfRange(newData, offset, offset+numBytes);
			records.set(recordId, rec);
		}
		catch (Exception e)
		{
			System.out.println("Problem in Set Record");
			e.printStackTrace();
		}
		lastModified = recordId;
		save();
		for(int i=0; i<listeners.size(); i++)
		{
			listeners.get(i).recordChanged(this, recordId);
		}
	}


	/* ************************************************************
				RecordEnumeration implementation
	    *********************************************************** */

	private class enumeration implements RecordEnumeration
	{
		private int index;
		private int[] elements;
		private int count;
		private boolean keepupdated;
		RecordFilter filter;
		RecordComparator comparator;

		public enumeration(RecordFilter filter, RecordComparator comparator, boolean keepUpdated)
		{
			keepupdated = keepUpdated;
			index = 0;
			count = 0;

			this.filter = filter;
			this.comparator = comparator;

			filter = filter;

			build();
		}

		private void build()
		{
			elements = new int[records.size()+1];
			for(int i=0; i<records.size()+1; i++) { elements[i] = 1; }
			count = 0;
			if(filter==null)
			{
				//System.out.println("Not Filtered");
				for(int i=1; i<records.size(); i++)
				{
					if(records.get(i).length>0) // not deleted
					{
						elements[count] = i;
						count++;
					}
				}
			}
			else
			{
				//System.out.println("Filtered");
				for(int i=1; i<records.size(); i++)
				{
					if(filter.matches(records.get(i)))
					{
						if(records.get(i).length>0) // not deleted
						{
							elements[count] = i;
							count++;
						}
					}
				}
			}

			int result = 0;
			int temp;
			if(comparator!=null)
			{
				//System.out.println("Comparator");
				for(int i=0; i<count-1; i++)
				{
					for(int j=0; j<count-(1+i); j++)
					{
						result = comparator.compare(records.get(elements[j]), records.get(elements[j+1]));
						if(result==RecordComparator.FOLLOWS)
						{
							temp = elements[j];
							elements[j] = elements[j+1];
							elements[j+1] = temp;
						}

					}
				}
			}
		}

		public void destroy() { }

		public boolean hasNextElement()
		{
			if(keepupdated) { rebuild(); }
			if (index<count)
			{
				return true;
			}
			return false;
		}

		public boolean hasPreviousElement()
		{
			if(keepupdated) { rebuild(); }
			if(index>0)
			{
				return true;
			}
			return false;
		}

		public boolean isKeptUpdated() { return keepupdated; }

		public void keepUpdated(boolean keepUpdated) { keepupdated = keepUpdated; }

		public byte[] nextRecord() throws InvalidRecordIDException
		{
			//System.out.println("> Next Record");
			if(keepupdated) { rebuild(); }
			if(index>=count)
			{
				throw(new InvalidRecordIDException());
			}
			index++;
			return records.get(elements[index-1]);
		}

		public int nextRecordId() throws InvalidRecordIDException
		{
			//System.out.println("> Next Record ID (idx:"+index+" cnt:"+count+")");
			if(keepupdated) { rebuild(); }
			if(index>=count) { throw(new InvalidRecordIDException()); }
			return elements[index];
		}

		public int numRecords()
		{
			//System.out.println("> numRecords()");
			if(keepupdated) { rebuild(); }
			return count;
		}

		public byte[] previousRecord() throws InvalidRecordIDException
		{
			//System.out.println("> Previous Record");
			if(keepupdated) { rebuild(); }
			index--;
			if(index>=0)
			{
				return records.get(elements[index]);
			}
			if(index<0)
			{
				index = count-1;
				return records.get(elements[index]);
			}
			return null;
		}

		public int previousRecordId() throws InvalidRecordIDException
		{
			//System.out.println("> Previous Record ID");
			if(keepupdated) { rebuild(); }
			if(index==0) { throw(new InvalidRecordIDException()); }
			return elements[index-1];
		}

		public void rebuild()
		{
			build();
			if(index >= count) { index = count-1; }
			if(index < 0) { index = 0; }
		}

		public void reset()
		{
			if(keepupdated) { rebuild(); }
			index = 0;
		}
	}
}
