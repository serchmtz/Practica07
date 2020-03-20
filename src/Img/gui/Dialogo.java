package Img.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class Dialogo extends Dialog {
		Label lblImg;
	  public Dialogo(Shell parent) {
	    super(parent);
	  }

	  public String open() {
	    Shell parent = getParent();
	    Shell dialog = new Shell(parent, SWT.DIALOG_TRIM
	        | SWT.APPLICATION_MODAL);
	    dialog.setSize(350, 375);
	    dialog.setText("Resultado");
	    Image imageC = new Image(Display.getCurrent(), "output.png");
	    Label lblImg = new Label(dialog, SWT.NONE);
		lblImg.setBounds(50, 50, 250, 250);
		lblImg.setText("img");
		ImageData data = imageC.getImageData();
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
        lblImg.setImage(imageScaled);
	    dialog.open();
	    Display display = parent.getDisplay();
	    while (!dialog.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    return "After Dialog";
	  }
	}
