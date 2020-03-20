package Img.opChunks;

import org.eclipse.swt.graphics.Point;

public class Chunk {
	public Point cpos;
	public int crows;
	public int ccols;
	public Chunk(int x, int y, int rows, int cols) {
		cpos = new Point(x,y);
		crows = rows;
		ccols = cols; 
	}
}
