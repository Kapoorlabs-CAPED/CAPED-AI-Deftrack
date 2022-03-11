package utility;

import java.awt.Rectangle;
import java.util.ArrayList;

import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;

public class Roiobject {

	public final Roi[] roilist;
	public ArrayList<EllipseRoi> resultroi;
	public ArrayList<OvalRoi> resultovalroi;
	public ArrayList<Line> resultlineroi;
	public ArrayList<OvalRoi> resultcurvelineroi;
	public ArrayList<Roi> segmentrect;
	public final int fourthDimension;
	public final int thirdDimension;
	public final boolean isCreated;
	public final int Celllabel;

	public Roiobject(final Roi[] roilist, final int thirdDimension, final int fourthDimension, final boolean isCreated)

	{
		this.resultroi = null;
		this.resultovalroi = null;
		this.resultlineroi = null;
		this.roilist = roilist;
		this.fourthDimension = fourthDimension;
		this.thirdDimension = thirdDimension;
		this.isCreated = isCreated;
		this.Celllabel = 0;
	}

	public Roiobject(final ArrayList<EllipseRoi> resultroi, ArrayList<OvalRoi> resultovalroi,
			ArrayList<Line> resultlineroi, final int thirdDimension, final int fourthDimension, final boolean isCreated)

	{
		this.resultroi = resultroi;
		this.resultovalroi = resultovalroi;
		this.resultlineroi = resultlineroi;
		this.roilist = null;
		this.fourthDimension = fourthDimension;
		this.thirdDimension = thirdDimension;
		this.isCreated = isCreated;
		this.Celllabel = 0;
	}
	
	public Roiobject(final ArrayList<EllipseRoi> resultroi,final int thirdDimension, final int fourthDimension, final boolean isCreated)

	{
		this.resultroi = resultroi;
		this.resultovalroi = null;
		this.resultlineroi = null;
		this.roilist = null;
		this.fourthDimension = fourthDimension;
		this.thirdDimension = thirdDimension;
		this.isCreated = isCreated;
		this.Celllabel = 0;
	}

	public Roiobject(final ArrayList<EllipseRoi> resultroi, ArrayList<OvalRoi> resultovalroi, 
			ArrayList<Line> resultlineroi,ArrayList<OvalRoi> resultcurvelineroi, ArrayList<Roi> rect,  final int thirdDimension, final int fourthDimension, final int Celllabel, final boolean isCreated)

	{
		this.resultroi = resultroi;
		this.resultovalroi = resultovalroi;
		this.resultlineroi = resultlineroi;
		this.resultcurvelineroi = resultcurvelineroi;
		this.roilist = null;
		this.fourthDimension = fourthDimension;
		this.thirdDimension = thirdDimension;
		this.isCreated = isCreated;
		this.Celllabel = Celllabel;
		this.segmentrect = rect;
	}
	
	
	
}
