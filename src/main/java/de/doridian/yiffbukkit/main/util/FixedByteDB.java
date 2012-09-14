package de.doridian.yiffbukkit.main.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FixedByteDB<EntryT extends FixedByteDB.Entry> {
	private final File file;
	private final EntryFactory<EntryT> factory;

	private long entryCount = 0;
	private final Object entryCountLock = new Object();

	private RandomAccessFile fileAccess = null;
	private final Object fileAccessLock = new Object();

	public FixedByteDB(EntryFactory<EntryT> factory, File file) {
		this.file = file;
		this.factory = factory;
		load();
	}

	public void close() {
		synchronized (fileAccessLock) {
		synchronized (entryCountLock) {
			entryCount = 0;

			if(fileAccess == null) return;

			try {
				fileAccess.close();
				fileAccess = null;
			} catch(IOException e) { }
		}
		}
	}

	public void load() {
		close();
		try {
			synchronized (fileAccessLock) {
			synchronized (entryCountLock) {
				fileAccess = new RandomAccessFile(file, "rw");
				fileAccess.seek(0);
				entryCount = fileAccess.readLong();
				for(int i = 0; i < entryCount; i++) {
					EntryT entry = factory.createEntry();
					entry.id = i;
					entry.read(fileAccess);
				}
			}
			}
		} catch(IOException e) { }
	}

	public void delete(EntryT entry) {
		if(entry.id < 0) return;

		synchronized (entryCountLock) {
			if(entry.id >= entryCount) return;

			entryCount--;

			synchronized (fileAccessLock) {
				writeEntryCount();
				if(entryCount > entry.id) {
					EntryT newEntry = get(entryCount);
					newEntry.id = entry.id;
					put(newEntry);
				}

				try {
					fileAccess.setLength(entryCount * entry.getSize());
				} catch(IOException e) { }
			}
		}
	}

	public void put(EntryT entry) {
		synchronized (fileAccessLock) {
			synchronized (entryCountLock) {
				if(entry.id < 0) {
					entry.id = entryCount++;
					writeEntryCount();
				}
				try {
					fileAccess.seek(entry.id * entry.getSize());
					entry.write(fileAccess);
				} catch(IOException e) { }
			}
		}
	}

	//For refreshing entries or something...
	public EntryT get(EntryT entry) {
		return get(entry.id);
	}

	public EntryT get(long id) {
		if(id < 0) return null;

		synchronized (entryCountLock) {
			if(id >= entryCount) return null;

			EntryT entry = factory.createEntry();
			try {
				synchronized (fileAccessLock) {
					fileAccess.seek(id * entry.getSize());
					entry.read(fileAccess);
				}
			}
			catch(IOException e) {
				return null;
			}
			return entry;
		}
	}

	private void writeEntryCount() {
		synchronized (fileAccessLock) {
		synchronized (entryCountLock) {
			try {
				fileAccess.seek(0);
				fileAccess.writeLong(entryCount);
			} catch(IOException e) { }
		}
		}
	}

	public static abstract class Entry {
		private long id = -1;
		public long getID() {
			return id;
		}
		public abstract int getSize();
		public abstract int read(DataInput input);
		public abstract int write(DataOutput output);
	}

	public static interface EntryFactory<EntityT extends Entry> {
		public EntityT createEntry();
	}
}
