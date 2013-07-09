/**
 * 
 */
package fatworm.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * @author mrain
 *
 */
public class DiskFile {
	
	//private String filename;
	private RandomAccessFile file;
	
	public DiskFile(String filename) throws FileNotFoundException {
		//this.filename = filename;
		file = new RandomAccessFile(filename, "rw");
	}
	
	public int size() throws IOException {
		return (int) (file.length() / Block.BlockSize);
	}
	
	public Block readBlock(int blk) throws IOException {
		byte[] data = new byte[Block.BlockSize];
		file.seek(blk * Block.BlockSize);
		int read = file.read(data, 0, Block.BlockSize);
		if (read != Block.BlockSize)
			throw new IOException();
		return new Block(blk, data);
	}
	
	public void writeBlock(Block page) throws IOException {
		int offset = page.getBlk() * Block.BlockSize;
		if (offset >= file.length())
			file.setLength(offset + Block.BlockSize);
		file.seek(offset);
		file.write(page.getData(), 0, Block.BlockSize);
	}

	public void close() throws IOException {
		file.close();
	}
}
