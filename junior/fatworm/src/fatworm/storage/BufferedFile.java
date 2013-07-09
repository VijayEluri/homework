/**
 * 
 */
package fatworm.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author mrain
 *
 */
public class BufferedFile {
	private static final int BufferSize = 1024 * 1024 * 1024 / Block.BlockSize;
	private int counter = 0;
	private DiskFile file;
	private Map<Integer, PageRecord> buffer;
	TreeSet<PageID> queue;

	public BufferedFile(String filename) throws FileNotFoundException {
		buffer = new HashMap<Integer, PageRecord>();
		file = new DiskFile(filename);
		queue = new TreeSet<PageID>();
	}
	
	private void commit(PageRecord blk) throws IOException {
		if (blk.dirty)
			file.writeBlock(blk.page);
		buffer.remove(blk);
		queue.remove(blk.id);
	}
	
	private void insert(PageRecord pr) throws IOException {
		if (buffer.size() == BufferSize) {
			PageID victimID = queue.pollFirst();
			queue.remove(victimID);
			int blk = victimID.blk;
			PageRecord victim = buffer.get(blk);
			commit(victim);
		}
		buffer.put(pr.page.getBlk(), pr);
		queue.add(pr.id);
	}
	
	public void close() throws IOException {
		for (PageRecord pr : buffer.values()) {
			if (pr.dirty) {
				file.writeBlock(pr.page);
			}
		}
		buffer.clear();
		queue.clear();
		file.close();
	}
	
	/*public void pinBlock(int blk) {
		if (buffer.containsKey(blk)) {
			PageRecord pr = buffer.get(blk);
			if (pr.pinned == false) {
				pr.pinned = true;
				queue.remove(pr.id);
			}
		}
	}
	
	public void unpinBlock(int blk) {
		if (buffer.containsKey(blk)) {
			PageRecord pr = buffer.get(blk);
			if (pr.pinned) {
				queue.add(pr.id);
				pr.pinned = false;
			}
		}
	}*/
	
	public byte[] readBlock(int blk) throws IOException {
		if (buffer.containsKey(blk)) {
			PageRecord pr = buffer.get(blk);
			if (!pr.pinned) {
				PageID id = pr.id;
				queue.remove(id);
				id.time = counter ++;
				queue.add(id);
			}
			return pr.page.getData();
		} else if (blk >= file.size()) {
			byte[] w = new byte[Block.BlockSize];
			writeBlock(blk, w);
			return buffer.get(blk).page.getData();
		} else {
			Block page = file.readBlock(blk);
			this.insert(new PageRecord(page, false));
			return page.getData();
		}
	}
	
	public void writeBlock(int blk, byte[] data) throws IOException {
		if (buffer.containsKey(blk)) {
			PageRecord pr = buffer.get(blk);
			if (!pr.pinned) {
				PageID id = pr.id;
				queue.remove(id);
				id.time = counter ++;
				queue.add(id);
			}
			pr.page.setData(data);
			pr.dirty = true;
		} else {
			Block page = new Block(blk, data);
			this.insert(new PageRecord(page, true));
		}
	}
	
	public void read(byte[] data, int offset, int length) throws IOException {
		int ind = 0;
		//System.out.println(offset + "-------" + length);
		for (int cur = offset; cur < offset + length;) {
			int blk = cur / Block.BlockSize;
			int l = Math.max(offset, blk * Block.BlockSize) % Block.BlockSize;
			int r = Math.min(blk * Block.BlockSize + Block.BlockSize - 1, offset + length - 1) % Block.BlockSize;
			//System.out.println(cur + " " + Block.BlockSize + " " + blk + " " + l + " " + r);
			byte[] tmp = readBlock(blk);
			System.arraycopy(tmp, l, data, ind, r - l + 1);
			ind += r - l + 1;
			cur = (blk + 1) * Block.BlockSize;
		}
	}
	
	public void write(byte[] data, int offset, int length) throws IOException {
		int ind = 0;
		for (int cur = offset; cur < offset + length;) {
			int blk = cur / Block.BlockSize;
			int l = Math.max(offset, blk * Block.BlockSize) % Block.BlockSize;
			int r = Math.min(blk * Block.BlockSize + Block.BlockSize - 1, offset + length - 1) % Block.BlockSize;
			byte[] w = new byte[Block.BlockSize];
			if (l != 0 || r != Block.BlockSize - 1) {
				byte[] tmp = readBlock(blk);
				System.arraycopy(data, ind, tmp, l, r - l + 1);
				setDirty(blk);
				ind += r - l + 1;
			} else {
				System.arraycopy(data, ind, w, l, r - l + 1);
				ind += r - l + 1;
				writeBlock(blk, w);
			}
			cur = (blk + 1) * Block.BlockSize;
		}
	}
	
	public void setDirty(int blk) {
		buffer.get(blk).dirty = true;
	}
	
	private class PageRecord {
		public PageID id;
		public Block page;
		public boolean dirty;
		public boolean pinned;
		
		public PageRecord(Block page, boolean dirty) {
			this.id = new PageID(page.getBlk());
			this.page = page;
			this.dirty = dirty;
			this.pinned = false;
		}
	}
	
	private class PageID implements Comparable<PageID> {
		public int time, blk;
		public PageID(int blk) {
			this.time = counter ++;
			this.blk = blk;
		}
		@Override
		public int compareTo(PageID pr) {
			if (pr.time < this.time) return -1;
			else if (pr.time == this.time) return 0;
			else return 1;
		}
	}
}
