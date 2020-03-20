package Img.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import Img.opChunks.ImageOperator;

import org.eclipse.swt.widgets.Label;

import java.awt.image.BufferedImage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;


public class MainWindow {

	protected Shell shell;
	protected Spinner spinnerCols;
	protected Spinner spinnerRows;
	String[] filtro = { "*.jpg", "*.png"};
	Image imageA = new Image(Display.getDefault(), 1, 1);
	Image imageB = new Image(Display.getDefault(), 1, 1);
	BufferedImage img;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(804, 528);
		shell.setText("SWT Application");
		GridLayout gl_shell = new GridLayout(3, false);
		gl_shell.marginTop = 0;
		gl_shell.marginHeight = 0;
		gl_shell.horizontalSpacing = 20;
		gl_shell.verticalSpacing = 20;
		shell.setLayout(gl_shell);
		
		Label lblImg = new Label(shell, SWT.WRAP | SWT.CENTER);
		lblImg.setAlignment(SWT.CENTER);
		GridData gd_lblImg = new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 1);
		gd_lblImg.widthHint = 294;
		gd_lblImg.heightHint = 272;
		lblImg.setLayoutData(gd_lblImg);
		lblImg.setText("img1");
		new Label(shell, SWT.NONE);
		
		Label lblImg2 = new Label(shell, SWT.NONE);
		lblImg2.setAlignment(SWT.CENTER);
		GridData gd_lblImg2 = new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 1);
		gd_lblImg2.widthHint = 297;
		lblImg2.setLayoutData(gd_lblImg2);
		lblImg2.setText("img2");
		new Label(shell, SWT.NONE);
		
		Combo combo = new Combo(shell, SWT.READ_ONLY);
		GridData gd_combo = new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1);
		gd_combo.widthHint = 83;
		gd_combo.heightHint = 25;
		combo.setLayoutData(gd_combo);
		combo.setItems(new String[] {"+", "-", "*", "#"});
		combo.select(0);
		new Label(shell, SWT.NONE);
		
		Button btnCargarImg = new Button(shell, SWT.NONE);
		btnCargarImg.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnCargarImg.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				//Selecciona ImgA
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Seleccionar Img");
		        fd.setFilterPath("C:/");
		        fd.setFilterExtensions(filtro);
		        String selected = fd.open();
		   
		        imageA = new Image(Display.getCurrent(), selected);
		        ImageData data = imageA.getImageData();
		        int tamMin=Math.min(data.height,data.width);
		        int tamMax=Math.max(data.height,data.width);
		        double norm=((((double)tamMin)/((double)tamMax)))*((double)250);
	
		        if(data.height>data.width)
		        	data = imageA.getImageData().scaledTo((int)norm, 250);
		        else
		        	if(data.height<data.width)
		        		data = imageA.getImageData().scaledTo(250, (int)norm);
		        	else if (data.height==data.width)
		        		data = imageA.getImageData().scaledTo(250, 250);
		        		
		        Image imageScaled = new Image(Display.getDefault(), data);
		        lblImg.setImage(imageScaled);
		        
		        
			}
		});
		btnCargarImg.setText("Cargar Imagen A");
		
		Button btnOperar = new Button(shell, SWT.NONE);
		btnOperar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnOperar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Operacion");
				int cols = spinnerCols.getSelection();
				int rows = spinnerRows.getSelection();
				ImageOperator operator = new ImageOperator(imageA, imageB, cols,rows);
				int opc=combo.getSelectionIndex();
				operator.procesar(opc+1);

			    Dialogo d = new Dialogo(shell);	    
			    d.open();
			}
		});
		btnOperar.setText("Operar");
		
		Button btnCargarImg2 = new Button(shell, SWT.NONE);
		btnCargarImg2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnCargarImg2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				// Selecciona ImgB
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Seleccionar Img");
		        fd.setFilterPath("C:/");
		        fd.setFilterExtensions(filtro);
		        String selected = fd.open();
		
		        imageB = new Image(Display.getCurrent(), selected);
		        
		        ImageData data = imageB.getImageData();
		        int tamMin=Math.min(data.height,data.width);
		        int tamMax=Math.max(data.height,data.width);
		        double norm=((((double)tamMin)/((double)tamMax)))*((double)250);
		       
		        if(data.height>data.width)
		        	data = data.scaledTo((int)norm, 250);
		        else
		        	if(data.height<data.width)
		        		data = data.scaledTo(250, (int)norm);
		        	else if (data.height==data.width)
		        		data = data.scaledTo(250, 250);
		        		
		        Image imageScaled = new Image(Display.getDefault(), data);
		        lblImg2.setImage(imageScaled);
			}
		});
		btnCargarImg2.setText("Cargar Imagen B");
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setAlignment(SWT.CENTER);
		lblNewLabel_1.setText("Columnas");
		new Label(shell, SWT.NONE);
		
		Label lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setAlignment(SWT.CENTER);
		lblNewLabel_2.setText("Filas");
		
		spinnerCols = new Spinner(shell, SWT.BORDER);
		spinnerCols.setMaximum(10);
		spinnerCols.setMinimum(1);
		spinnerCols.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setText("X");
		
		spinnerRows = new Spinner(shell, SWT.BORDER);
		spinnerRows.setMaximum(10);
		spinnerRows.setMinimum(1);
		spinnerRows.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
	}
}
