import java.nio.ByteBuffer;

public interface PageHeader
{
	static final int LONGSIZE = Long.SIZE/Byte.SIZE;
	static final int INTSIZE  = Integer.SIZE/Byte.SIZE;
	static final int SHORTSIZE = Short.SIZE/Byte.SIZE;
	
	void read(ByteBuffer b);	
	void write(ByteBuffer b);
}
