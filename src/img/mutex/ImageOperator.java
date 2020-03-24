package img.mutex;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;


public class ImageOperator extends Thread {
	//	private Image imgA;
	//	private Image imgB;
	private ImageData imgDataA;
	private ImageData imgDataB;
	private ImageData imgDataR;
	//	private RGB[][] img1;
	//	private RGB[][] img2;
	private volatile boolean chunkMatrix[][];
	private int chunkRows;
	private int chunkCols;
	private volatile int chunkCounter;
	private int ancho;
	private int altura;
	private double alpha;
	private double beta;
	private Lock lock;
	private boolean procesando;
	private Chunk chunk;
	private Operation operation;
	int op;
	int myPid;
	public ImageOperator(int pid, Image imgA, Image imgB,ImageData imgR, 
			int ancho, 
			int altura, 
			int ccols, 
			int crows, 
			double alpha, 
			double beta, 
			Lock lock, 
			int op,
			boolean chunkMatrix[][],
			int chunkCounter) {
		this.op = op;
		this.lock = lock;
		this.procesando = false;
		this.chunkCols = ccols;
		this.chunkRows = crows;
		this.chunkCounter = chunkCounter; 
		//		this.imgA = imgA;
		//		this.imgB = imgB;
		this.imgDataA = imgA.getImageData();
		this.imgDataB = imgB.getImageData();
		this.chunkMatrix = chunkMatrix;
		//		this.chunkMatrix = new boolean[ccols][crows];
		this.ancho = ancho;
		this.altura = altura;
		//		this.imgDataR = new ImageData(ancho, altura, imgDataA.depth, imgDataB.palette);
		this.imgDataR = imgR;
		//		pixel2matrix(imgDataA, img1);
		//		pixel2matrix(imgDataB, img2);
		this.myPid = pid;
		this.alpha = alpha;
		this.beta = beta;
	}

	//	public void pixel2matrix(ImageData imgData, RGB[][] rgbMatrix) {
	//		int pixel;
	//		for(int i = 0; i < imgData.width; i++) {
	//			for(int j = 0; j < imgData.height; j++) {
	//				pixel = imgData.getPixel(i,j);
	//				rgbMatrix[i][j] = imgData.palette.getRGB(pixel);			
	//			}
	//		}
	//	}
	public void regionCritica() {
		System.out.println("CR hilo:" + myPid);
		System.out.println("chunkCounter: " + chunkCounter);
		if(chunkCounter > 0) {
			if(!procesando) {
				for(int i = 0; i < chunkCols; i++) {
					for(int j = 0; j < chunkRows; j++) {
						System.out.println(i + ", " + j + ": " + chunkMatrix[i][j]);
						if(!chunkMatrix[i][j]) {
							chunkMatrix[i][j] = true;
							chunk = obtainChunk(i, j);
							procesando = true;
							break;
						}
					}
					if(chunkMatrix[chunkCols - 1][chunkRows - 1]) {
						chunkCounter = 0;
						return;
					}
					if(procesando) {
						procesar(op);					
						chunkCounter--;
						System.out.println("*chunkCounter: " + chunkCounter);
						procesando = false;
						break;
					}
				}				
			}
		}
	}
	public void regionNoCritica() {
		System.out.println("noCR hilo:" + myPid);
		System.out.println("chunkCounter: " + chunkCounter);
		doOperation(chunk, operation);
		Image imageNew = new Image(Display.getDefault(), imgDataR);

		System.out.println("Fin de Procesamiento");
		//imageSuma = imgA;
		ImageLoader saver = new ImageLoader();
		saver.data = new ImageData[] { imageNew.getImageData() };
		saver.save("output.png", SWT.IMAGE_PNG);

		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] {imageNew.getImageData()};
		imageLoader.save("a.jpg",SWT.IMAGE_JPEG);
	}
	public void run() {

		while( chunkCounter > 0) 
		{
			lock.requestCR(myPid);
			regionCritica();

			lock.releaseCR(myPid);
			regionNoCritica();
		}
	}
	
	//	public byte[] matrix2pixel(ImageData imgData) {
	//		return imgData.data;
	//	}

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


		System.out.println("Dimensiones: " + ancho + " x " + altura);	

		switch(op) 
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

		Image imageNew = new Image(Display.getDefault(), imgDataR);

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
		if(chunk == null) return imgDataR;

		int pixelSuma = 0;
		int pixelA, pixelB;

		int r,g,b;

		//		System.out.println("Chunk pos  : " + chunk.cpos);
		//		System.out.println("Chunk ccols: " + chunk.ccols);
		//		System.out.println("Chunk crows: " + chunk.crows);
		//		System.out.println("i < :  " + (chunk.cpos.x + chunk.ccols));
		//		System.out.println("j < :  " + (chunk.cpos.y + chunk.crows));
		//		System.out.println("img A: " + imgDataA.width + "x" + imgDataA.height);
		//		System.out.println("img B: " + imgDataB.width + "x" + imgDataB.height);

		for(int i = chunk.cpos.x; i < (chunk.cpos.x + chunk.ccols); i++) {
			for(int j = chunk.cpos.y; j < (chunk.cpos.y + chunk.crows); j++) {
				//				System.out.println("i,j: " + i + ", " + j);				
				pixelA = imgDataA.getPixel(j,j);
				pixelB = imgDataB.getPixel(i,j);
				r = operation.operate(imgDataA.palette.getRGB(pixelA).red,   imgDataB.palette.getRGB(pixelB).red);
				g = operation.operate(imgDataA.palette.getRGB(pixelA).green, imgDataB.palette.getRGB(pixelB).green);
				b = operation.operate(imgDataA.palette.getRGB(pixelA).blue,  imgDataB.palette.getRGB(pixelB).blue);
				pixelSuma = (r << 16) | (g << 8) | b;
				imgDataR.setPixel(i, j, pixelSuma);
			}
		}		
		System.out.println("Hilo: " + myPid + " Finalizado");
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
		double comb = 0;

		comb = (alpha*c1) + (beta*c2);
		return (int) comb;
	}	
}
