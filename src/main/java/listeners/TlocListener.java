package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.ShowView;

public class TlocListener implements TextListener {
	
	
	final InteractiveSimpleEllipseFit parent;
	
	boolean pressed;
	public TlocListener(final InteractiveSimpleEllipseFit parent, final boolean pressed) {
		
		this.parent = parent;
		this.pressed = pressed;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	   
		 tc.addKeyListener(new KeyListener(){
			 @Override
			    public void keyTyped(KeyEvent arg0) {
				   
			    }

			    @Override
			    public void keyReleased(KeyEvent arg0) {
			    	
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER ) {
						
						
						pressed = false;
						
					}

			    }

			    @Override
			    public void keyPressed(KeyEvent arg0) {
			    	String s = tc.getText();
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER&& !pressed) {
						pressed = true;
			    		if (parent.fourthDimension > parent.fourthDimensionSize) {
							IJ.log("Max frame number exceeded, moving to last frame instead");
							parent.fourthDimension = parent.fourthDimensionSize;
						} else
							parent.fourthDimension = Integer.parseInt(s);
			    		ShowView show = new ShowView(parent);
					show.shownewT();
					parent.timeText.setText("Current T = " + parent.fourthDimension);
					parent.updatePreview(ValueChange.FOURTHDIMmouse);
					
					parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension, parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
					parent.timeslider.repaint();
					parent.timeslider.validate();
					
					
					
			    		
					 }

			    }
			});
	

	

}

}
