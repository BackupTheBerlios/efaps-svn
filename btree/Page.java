
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Comparator;
import java.util.Formatter;
import java.util.SortedSet;
import java.util.TreeSet;


public class Page
{	
	static final int PAGESIZE = 1024;	// raw page size on disk
	static final int DATASIZE = PAGESIZE-Header.SIZE;	// data count on page
	
	
	class Header implements PageHeader
	{		
		static final int SIZE = 3 * LONGSIZE + 2 * INTSIZE;
		
		long pno;
		long next;
		long prev;
		int ecount;
		int free;
				
		public void write(ByteBuffer bb)
		{
			bb.putLong(pno);	// Page Number
			bb.putLong(next);	// next PageNo
			bb.putLong(prev);	// prev PageNo
			bb.putInt(free);	// free byte on page
			bb.putInt(ecount);	// entries on page			
		}
		
		public void read(ByteBuffer bb)
		{
			pno    = bb.getLong();
			next   = bb.getLong();
			prev   = bb.getLong();
			free   = bb.getInt();
			ecount = bb.getInt();			
		}
	}
	
	static class ByteArray implements Comparator<byte[]>
	{		
		public int compare(byte[] a, byte[] b)
		{
			if(a==b)
				return 0;
			
			int length = Math.min(a.length,b.length);
			
			for(int i=0;i<length;i++)
				if(a[i]>b[i])
					return 1;
				else
				if(b[i] > a[i])
					return -1;
			
			if(a.length == b.length)
				return 0;
			
			if(a.length > b.length)
				return 1;
			else
				return -1;
		}	
	}
	
	SortedSet<byte[]> s = new TreeSet<byte[]>(new ByteArray());
	
	ByteBuffer bb = ByteBuffer.allocateDirect(PAGESIZE);
	
	FileChannel file = null;
	
	Header ph;
	
	Page(FileChannel fc)
	{
		file = fc;
	}
	
	void write() throws Exception
	{
		ph.write(bb);
		
		for (byte[] i : s)	// write set to page
		{
			bb.putShort((short)i.length);	// length of entry
			bb.put(i);						// entry
		}
		
		bb.rewind();						// rewind buffer for write
		file.write(bb);						// write buffer	
		s.clear();							// clear & rewind buffer
	}
	
	void read() throws Exception
	{
		file.read(bb);
		bb.rewind();	
		
		ph.read(bb);

		for (int i = 0; i < ph.ecount; i++)
		{
			int esize = bb.getShort();
			byte[] e = new byte[esize];
			bb.get(e);
			s.add(e);
		}
		
		bb.clear();
	}
	
	void print()
	{
		System.out.println(ph.free);
		System.out.println(ph.ecount);
		
		for(byte[] i : s)
		{
			System.out.println(new String(i));
		}
	}
	
	void put(byte[] e)
	{
		if (ph.free - (e.length+PageHeader.SHORTSIZE) < 0)
			split();
		
		s.add(e);
		ph.ecount++;
		ph.free -= e.length+PageHeader.SHORTSIZE;
	}
	
	void split()
	{	
		int ds = DATASIZE/2;
		int headSize = 0;
		
		byte [] splitPoint = null;
		
		for(byte[] i : s)
		{
			headSize += i.length+PageHeader.SHORTSIZE;

			if(ds-headSize < 0)
			{
				splitPoint=i;
				break;							
			}
		}
		
		System.out.println("SplitPoint = "+new String(splitPoint));
		Page x = new Page(file);
		
		x.s = s.tailSet(splitPoint);
		x.ph.ecount=x.s.size();
		x.ph.free=DATASIZE-ph.free-headSize;
		x.ph.pno=2;
		x.ph.prev=1;
		
		s = s.headSet(splitPoint);
		ph.next = 2;
		ph.free = DATASIZE-headSize;
	}
	
	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws Exception
	{
		RandomAccessFile raf = new RandomAccessFile("data","rw");		
		FileChannel fc = raf.getChannel();

		Page p = new Page(fc);
		
		p.ph.pno = 1;
		p.ph.next = 0xa;
		p.ph.prev = 0xb;
		p.ph.free = DATASIZE;

		for(int i=0;i<110;i++)
			p.put(new Formatter().format("Hello%02d",i).toString().getBytes());
		
		
		p.put("Hello".getBytes());
		p.put("Andreas".getBytes());
		p.put("Test".getBytes());
		p.write();
		
		fc.write(p.bb);
		fc.position(0);

		p.read();
		p.print();	
	}
}
