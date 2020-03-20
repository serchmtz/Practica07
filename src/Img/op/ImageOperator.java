package Img.op;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class ImageOperator {
	private Image imgA;
	private Image imgB;
	private ImageData imgDataA;
	private ImageData imgDataB;
	public ImageOperator(Image imgA, Image imgB) {
		this.imgA = imgA;
		this.imgB = imgB;
		this.imgDataA = this.imgA.getImageData();
		this.imgDataB = this.imgB.getImageData();
		System.out.println("Image A depth:" + this.imgDataA.depth);
		System.out.println("Image B depth:" + this.imgDataB.depth);

	}
	public Image procesar(int opc){


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
		Image imageNew = new Image(Display.getDefault(), doOperation(ancho, altura, operation));

		System.out.println("Fin de Procesamiento");
		//imageSuma = imgA;
		ImageLoader saver = new ImageLoader();
		saver.data = new ImageData[] { imageNew.getImageData() };
		saver.save("output.png", SWT.IMAGE_PNG);

		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] {imageNew.getImageData()};
		imageLoader.save("a.jpg",SWT.IMAGE_JPEG);
		return imageNew;
	}
	public int sumar(int c1, int c2) {
		int suma=255;
		if((c1 + c2) <= 255) {
			suma = c1 + c2;
		}
		return suma;
	}
	private ImageData doOperation(int ancho, int alto, Operation operation) {
		int pixelSuma = 0;
		int pixelA, pixelB;

		int r,g,b;
		ImageData newData =  new ImageData(ancho, alto, imgDataA.depth, imgDataB.palette);
		for(int i=0; i < ancho; i++) {
			for(int j=0; j < alto; j++) {
				pixelA = imgDataA.getPixel(i,j);
				pixelB = imgDataB.getPixel(i,j);
				r = operation.operate(imgDataA.palette.getRGB(pixelA).red, imgDataB.palette.getRGB(pixelB).red);
				g = operation.operate(imgDataA.palette.getRGB(pixelA).green, imgDataB.palette.getRGB(pixelB).green);
				b = operation.operate(imgDataA.palette.getRGB(pixelA).blue, imgDataB.palette.getRGB(pixelB).blue);
				pixelSuma = (r << 16)| (g << 8) | b;
				newData.setPixel(i, j, pixelSuma);
			}
		}

		return newData;
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
