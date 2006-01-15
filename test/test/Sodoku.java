/*
 * Meine Version $Id: Test.java 8 2005-08-27 14:30:57 +0200 (Sa, 27 Aug 2005)  $
 */

package test;

/**
 * @author jul
 */

public class Sodoku
{
    static final int ROWS = 0;
    static final int COLS = 1;
    static final int SQDIM = 3;
    
    static final int DIM = SQDIM * SQDIM;
        
    int[][] row = new int[DIM][DIM];    // rows
    int[][] col = new int[DIM][DIM];    // cols

    int[][][][] sq = new int[SQDIM][SQDIM][][]; // squares
    
    Sodoku(int start)
    {
        int k=start;
        
        for(int i=0;i<DIM;i++)
            for(int j=0;j<DIM;j++)
            {
                row[i][j] = k;
                col[j][i] = k;
                k++;
            }
        
        for(int i=0;i<SQDIM;i++)
            for(int j=0;j<SQDIM;j++)
                sq[i][j] = sq(i,j);
    }
 
    int[][] sq(int x, int y)
    {        
        int[][] s = new int[SQDIM][SQDIM];
        
        y *= SQDIM;
        x *= SQDIM;
        
        for(int i=0;i<SQDIM;i++)
            for(int j=0;j<SQDIM;j++)
                s[i][j] = row[x+i][y+j];

        return s;
    }
    
    int[] row(int y)
    {
        if(y <1 || y > DIM)
            return null;
        
        return row[y-1];
    }
    

    int[] col(int x)
    {
        if(x < 1 ||  x > DIM)
            return null;

        return col[x-1];
    }
    
    int[][] square(int x, int y)
    {
        if(x<1 || y<1 || x > SQDIM || y > SQDIM)
            return null;
        
        return sq[x-1][y-1];        
    }
    
    int[][] rcOfSquare(int x, int y)
    {
        int[][] rc = new int[SQDIM][2];

        x = (x-1)*SQDIM;
        y = (y-1)*SQDIM;
        
        for(int i=0;i<SQDIM;i++)
                rc[i] = new int[] {x+i+1, y+i+1};

        return rc;
        
    }
            
    void print()
    {
        for(int i=0;i<DIM;i++)
        {
            for(int j=0;j<DIM;j++)
                System.out.printf("%02d|",row[i][j]);
            System.out.println();
        }        
    }
    
    
    /**
     * @param args
     */

    public static void main(String[] args)
    {
        Sodoku s = new Sodoku(1);
        
        s.print();
        
        int[] c = s.col(1);
        
        System.out.println("1st col");
        
        for(int i=0;i<DIM;i++)
            System.out.printf("%02d\n",c[i]);
        
        int[] r = s.row(1);
        
        System.out.println("1st row");
        
        for(int i=0;i<DIM;i++)
            System.out.printf("%02d|",r[i]);
        
        System.out.println();
        
        int sqx = 1;
        int sqy = 2;
        
        System.out.println("Square "+sqx+","+sqy);

        int[][] sq = s.square(sqx,sqy);
        
        

        for(int i=0;i<SQDIM;i++)
        {
            for(int j=0;j<SQDIM;j++)
                System.out.printf("%02d|",sq[i][j]);
            System.out.println();
        }
                
        System.out.print("ROWS:");
        for(int i=0;i<SQDIM;i++)
                System.out.print(s.rcOfSquare(sqx,sqy)[i][ROWS]);                
        System.out.println();

        System.out.print("COLS:");
        
        for(int i=0;i<SQDIM;i++)
            System.out.print(s.rcOfSquare(sqx,sqy)[i][COLS]);                

        System.out.println(); 
    }
}
