package Img.opChunks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ImageOperator {
	private Image imgA;
	private Image imgB;
	private ImageData imgDataA;
	private ImageData imgDataB;
	private ImageData imgDataR;
	private RGB[][] img1;
	private RGB[][] img2;
	private boolean chunkMatrix[][];
	private int chunkRows;
	private int chunkCols;
	private int chunkCounter;
	private int ancho;
	private int altura;

	public ImageOperator(Image imgA, Image imgB, int ccols, int crows) {
		this.chunkCols = ccols;
		this.chunkRows = crows;
		this.chunkCounter = ccols * crows; 
		this.imgA = imgA;
		this.imgB = imgB;
		this.imgDataA = this.imgA.getImageData();
		this.imgDataB = this.imgB.getImageData();
		this.img1 = new RGB[imgDataA.width][imgDataA.height];
		this.img2 = new RGB[imgDataB.width][imgDataB.height];
		this.chunkMatrix = new boolean[ccols][crows];
		ancho = Math.min(imgDataA.width, imgDataB.width);
		altura = Math.min(imgDataA.height,imgDataB.height);
		this.imgDataR = new ImageData(ancho, altura, imgDataA.depth, imgDataB.palette);	    
		pixel2matrix(imgDataA, img1);
		pixel2matrix(imgDataB, img2);

	}
	public void pixel2matrix(ImageData imgData, RGB[][] rgbMatrix) {
		int pixel;
		for(int i = 0; i < imgData.width; i++) {
			for(int j = 0; j < imgData.height; j++) {
				pixel = imgData.getPixel(i,j);
				rgbMatrix[i][j] = imgData.palette.getRGB(pixel);			
			}
		}
	}

	public byte[] matrix2pixel(ImageData imgData) {
		return imgData.data;
	}

	public Chunk obtainChunk(int x, int y) {
		int ccols, crows;
		int offsetx, offsety;
		ccols = (int) Math.ceil((double) ancho / chunkCols);
		crows = (int) Math.ceil((double) altura / chunkRows);
		offsetx = ccols;
		offsety = crows;

		if(x == chunkCols - 1 && (ancho % chunkCols) != 0) {
			ccols = ancho - (ccols*(chunkCols -1));			
		}
		if(y == chunkRows - 1 && (altura % chunkRows) != 0) {
			crows = altura - (crows*(chunkRows -1));
		}
		return new Chunk(x * offsetx, y * offsety, crows, ccols);
	}
	public void procesar(int opc){


		int ancho = Math.min(imgDataA.width, imgDataB.width);
		int altura = Math.min(imgDataA.height,imgDataB.height);


		System.out.println("Dimensiones: " + ancho + " x " + altura);	
		Operation operation;
		switch(opc) 
		{
		case 1:
			operation = this::sumar;
			break;
		case 2:
			operation = this::restar;
			break;
		case 3:
			operation = this::multiplicar;
			break;
		case 4:
			operation = this::cLineal;
			break;
		default:
			operation = this::sumar;
			break;
		}
		int count = 1;
		for(int i = 0; i < chunkCols; i++) {
			for(int j = 0; j < chunkRows; j++){
				System.out.println("Chunk: " + count + "/" + chunkCounter + " Chunk at (" + i + ", " + j + ")");
				doOperation(obtainChunk(i, j), operation);
				//chunkMatrix[i][j] = true;	
				count++;
			}

		}
		Image imageNew = new Image(Display.getDefault(), imgDataR );

		System.out.println("Fin de Procesamiento");
		//imageSuma = imgA;
		ImageLoader saver = new ImageLoader();
		saver.data = new ImageData[] { imageNew.getImageData() };
		saver.save("output.png", SWT.IMAGE_PNG);

		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] {imageNew.getImageData()};
		imageLoader.save("a.jpg",SWT.IMAGE_JPEG);

	}
	public int sumar(int c1, int c2) {
		int suma=255;
		if((c1 + c2) <= 255) {
			suma = c1 + c2;
		}
		return suma;
	}
	private ImageData doOperation(Chunk chunk, Operation operation) {
		int pixelSuma = 0;
	
		int r,g,b;
		System.out.println("Chunk pos  : " + chunk.cpos);
		System.out.println("Chunk ccols: " + chunk.ccols);
		System.out.println("Chunk crows: " + chunk.crows);
		
		for(int i = chunk.cpos.x; i < (chunk.cpos.x + chunk.ccols); i++) {
			for(int j = chunk.cpos.y; j < (chunk.cpos.y + chunk.crows); j++) {			
				r = operation.operate(img1[i][j].red,   img2[i][j].red);
				g = operation.operate(img1[i][j].green, img2[i][j].green);
				b = operation.operate(img1[i][j].blue,  img2[i][j].blue);
				pixelSuma = (r << 16) | (g << 8) | b;
				imgDataR.setPixel(i, j, pixelSuma);
			}
		}		
		return imgDataR;
	}
	public int restar(int c1, int c2) {
		int resta = 0;
		if((c1 - c2) >= 0) {
			resta = c1 - c2;
		}
		return resta;
	}

	public int multiplicar(int c1, int c2) {
		int multi = 0;
		multi = (c1*c2)/255;
		return multi;
	}

	public int cLineal(int c1, int c2) {
		double alfa = 0.7;
		double beta = 0.3;
		double comb = 0;

		comb = (alfa*c1) + (beta*c2);
		return (int) comb;
	}	
}
