package listeners;

import java.awt.TextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.text.DecimalFormat;
import java.util.Iterator;

import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import pluginTools.IlastikEllipseFileChooser;
import pluginTools.InteractiveSimpleEllipseFit;

public class BackGroundListener implements TextListener {

	final InteractiveSimpleEllipseFit parent;
	
	public BackGroundListener(final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
	}
	
	
	@Override
	public void textValueChanged(TextEvent e) {
		
	
		
		
			
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	 	
	 	if (s.contains("-")) {
			s.replace("-", "").trim();
		}

		int neg = 1;
		if (s.length() > 0 && s.charAt(0) == '-') {
			s = s.substring(1).trim();
			neg = -1;
		}
	    if (s.length() > 0)
	    	parent.background = Integer.parseInt(s);
	    
	    parent.background = neg * parent.background;
	    parent.backField.setText(Integer.toString(parent.background));
	    
	}
	
	


	
	
}
