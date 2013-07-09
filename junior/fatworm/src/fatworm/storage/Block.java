/**
 * 
 */
package fatworm.storage;

/**
 * @author mrain
 *
 */
public class Block {
	public static final int BlockSize = 8192;
	
	int blk;
	byte[] data = new byte[BlockSize];
	
	public Block(int blk, byte[] data) {
		this.blk = blk;
		System.arraycopy(data, 0, this.data, 0, BlockSize);
	}
	
	public int getBlk() {
		return blk;
	}

	public void setBlk(int blk) {
		this.blk = blk;
	}

	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		System.arraycopy(data, 0, this.data, 0, BlockSize);
	}
}
