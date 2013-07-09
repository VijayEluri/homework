/**
 * 
 */
package fatworm.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import fatworm.db.Schema;
import fatworm.db.Table;

/**
 * @author mrain
 *
 */
public class RecordFile {
	BufferedFile file;
	public int used = 0;
	
	public RecordFile(String filename) throws FileNotFoundException {
		file = new BufferedFile(filename);
	}
	
	public RecordFile(String filename, int size) throws FileNotFoundException {
		file = new BufferedFile(filename);
		used = size;
	}
	
	public RecordPage fetchPage(int offset, Schema schema) throws IOException {
		byte[] data = new byte[12];
		file.read(data, offset, 12);
		ByteBuffer bb = ByteBuffer.wrap(data);
		int prev = bb.getInt(0);
		int length = bb.getInt(4);
		int next = bb.getInt(8);
		data = new byte[length];
		file.read(data, offset + 12, length);
		return new RecordPage(file, /*schema, */offset, prev, next, data);
	}
	
	public PageMetaData fetchPageMetaData(int offset) throws IOException {
		byte[] data = new byte[12];
		file.read(data, offset, 12);
		ByteBuffer bb = ByteBuffer.wrap(data);
		int prev = bb.getInt(0);
		int length = bb.getInt(4);
		int next = bb.getInt(8);
		return new PageMetaData(offset, prev, length, next);
	}
	
	public int addEmptyPage() {
		int ret = used;
		int next = ret, prev = ret;
		byte[] data = new byte[12];
		data[11] = (byte) (next & 0xFF); next >>= 8;
		data[10] = (byte) (next & 0xFF); next >>= 8;
		data[9] = (byte) (next & 0xFF); next >>= 8;
		data[8] = (byte) (next & 0xFF);
		data[4] = data[5] = data[6] = data[7] = 0;
		data[3] = (byte) (prev & 0xFF); prev >>= 8;
		data[2] = (byte) (prev & 0xFF); prev >>= 8;
		data[1] = (byte) (prev & 0xFF); prev >>= 8;
		data[0] = (byte) (prev & 0xFF);
		try {
			file.write(data, ret, 12);
			used += 12;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return ret;
	}
	
	public void mkEmptyPage(int offset) throws IOException {
		PageMetaData empty = fetchPageMetaData(offset);
		empty.next = empty.prev = offset;
		empty.commit();
	}
	

	public void removePage(int offset, Table t) throws IOException {
		PageMetaData cur = fetchPageMetaData(offset);
		PageMetaData next = fetchPageMetaData(cur.next);
		PageMetaData prev = fetchPageMetaData(cur.prev);
		
		if (next.offset == prev.offset) {
			next.next = next.prev = next.offset;
			next.commit();
		} else {
			next.prev = prev.offset;
			prev.next = next.offset;
			next.commit();
			prev.commit();
		}
		
		if (t.offset == offset)
			t.offset = next.offset;
	}
	
	public int addPage(int before, byte[] data) throws IOException {
		PageMetaData next = fetchPageMetaData(before);
		PageMetaData prev = fetchPageMetaData(next.prev);
		
		RecordPage rp = new RecordPage(file, used, prev.offset, next.offset, data);
		rp.commit();
		if (next.offset != prev.offset) {
			next.prev = prev.next = used;
			next.commit();
			prev.commit();
		} else {
			next.prev = next.next = used;
			next.commit();
		}
		
		used += rp.length();
		return used - rp.length();
	}

	public void close() throws IOException {
		file.close();
	}
	
	private class PageMetaData {
		public int prev, next, length, offset;
		
		public PageMetaData(int offset, int prev, int length, int next) {
			this.offset = offset;
			this.prev = prev;
			this.length = length;
			this.next = next;
		}
		
		public void commit() throws IOException {
			ByteBuffer bb = ByteBuffer.allocate(12);
			bb.putInt(prev);
			bb.putInt(length);
			bb.putInt(next);
			file.write(bb.array(), offset, 12);
		}
	}
	
	public int length() {
		return used;
	}

	public void fixPage(int offset) throws IOException {
		PageMetaData cur = fetchPageMetaData(offset);
		PageMetaData next = fetchPageMetaData(cur.next);
		next.prev = offset;
		next.commit();
		PageMetaData prev = fetchPageMetaData(cur.prev);
		prev.next = offset;
		prev.commit();
	}

}
