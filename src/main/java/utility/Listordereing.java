package utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import kalmanForSegments.Segmentobject;
import kalmanTracker.NNFlagsearchKDtree;
import mpicbg.models.Point;
import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class Listordereing {

	// @VKapoor

	public static List<RealLocalizable> getCopyList(List<RealLocalizable> copytruths) {

		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>();
		Iterator<RealLocalizable> iter = copytruths.iterator();

		while (iter.hasNext()) {

			orderedtruths.add(iter.next());

		}

		return orderedtruths;
	}


	public static ArrayList<Pair<String, Intersectionobject>> getCopyInterList(
			ArrayList<Pair<String, Intersectionobject>> copytruths) {

		ArrayList<Pair<String, Intersectionobject>> orderedtruths = new ArrayList<Pair<String, Intersectionobject>>();
		Iterator<Pair<String, Intersectionobject>> iter = copytruths.iterator();

		while (iter.hasNext()) {

			orderedtruths.add(iter.next());

		}

		return orderedtruths;
	}

	public static List<RealLocalizable> getList(List<RealLocalizable> truths, int index) {

		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>();

		if (index > truths.size())
			index = truths.size();

		for (int i = index; i < truths.size(); ++i) {

			orderedtruths.add(truths.get(i));

		}
		for (int i = 0; i < index; ++i) {

			orderedtruths.add(truths.get(i));

		}

		return orderedtruths;

	}

	public static List<RealLocalizable> getNexinLine(List<RealLocalizable> truths, RealLocalizable Refpoint,
			RealLocalizable meanCord, int count) {

		List<RealLocalizable> copytruths = getCopyList(truths);

		List<RealLocalizable> sublisttruths = new ArrayList<RealLocalizable>();

		Iterator<RealLocalizable> listiter = copytruths.iterator();

		while (listiter.hasNext()) {

			RealLocalizable listpoint = listiter.next();

			double angledeg = Distance.AngleVectors(Refpoint, listpoint, meanCord);
			
				
				if (angledeg >= 0 )
					sublisttruths.add(listpoint);
				
			
				
			
		}
		return sublisttruths;

	}



	/**
	 * Return an ordered list of XY coordinates starting from the reference position
	 * which is the lowest point below the center of the list of points
	 * 
	 * @param truths
	 * @return
	 */

	public static Pair<RealLocalizable, List<RealLocalizable>> getOrderedList(List<RealLocalizable> truths,
			int resolution) {

		List<RealLocalizable> copytruths = getCopyList(truths);
		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>(truths.size());
		List<RealLocalizable> skiporderedtruths = new ArrayList<RealLocalizable>();
		// Get the starting minX and minY co-ordinates
		RealLocalizable minCord;

		RealLocalizable meanCord = getMeanCord(copytruths);
		minCord = getMinCord(copytruths);
		RealLocalizable refcord = minCord;

	
		orderedtruths.add(minCord);

		copytruths.remove(minCord);
		int count = 0;
		do {

			List<RealLocalizable> subcopytruths = getNexinLine(copytruths, minCord, meanCord, count);
			if (subcopytruths != null && subcopytruths.size() > 0) {
				count++;
				RealLocalizable nextCord = getNextNearest(minCord, subcopytruths);
				copytruths.remove(nextCord);
				if (copytruths.size() != 0) {
					copytruths.add(nextCord);

					RealLocalizable chosenCord = null;

					chosenCord = nextCord;

					minCord = chosenCord;
					orderedtruths.add(minCord);

					copytruths.remove(chosenCord);
				} else {

					orderedtruths.add(nextCord);
					break;

				}
			}
			else break;
		} while (copytruths.size() >= 0);

		
		for (int i = 0; i < orderedtruths.size(); i += resolution) {
			if (i + resolution > orderedtruths.size() - 1)
				break;
			else
				skiporderedtruths.add(orderedtruths.get(i));

		}

		return new ValuePair<RealLocalizable, List<RealLocalizable>>(refcord, skiporderedtruths);
	}

	

	public static RealLocalizable GetCurrentRefpoint(List<RealLocalizable> truths, RealLocalizable Refpoint,
			RealLocalizable SecRefpoint) {

		RealLocalizable returnCord;
		List<RealLocalizable> copytruths = getCopyList(truths);

		RealLocalizable nextCord = getNextNearest(Refpoint, copytruths);

		RealLocalizable SecnextCord = getNextNearest(SecRefpoint, copytruths);

		double distanceA = Distance.DistanceSqrt(nextCord, Refpoint) + Distance.DistanceSqrt(nextCord, SecRefpoint);

		double distanceB = Distance.DistanceSqrt(SecnextCord, Refpoint)
				+ Distance.DistanceSqrt(SecnextCord, SecRefpoint);

		returnCord = (distanceA <= distanceB) ? nextCord : SecnextCord;

		return returnCord;
	}

	/**
	 * Return an ordered list of XY coordinates starting from the min X position to
	 * the end of the list
	 * 
	 * 
	 * @param truths
	 * @return
	 */

	public static List<RealLocalizable> copyList(List<RealLocalizable> truths) {

		List<RealLocalizable> copytruths = new ArrayList<RealLocalizable>();

		Iterator<RealLocalizable> iterator = truths.iterator();

		while (iterator.hasNext()) {

			RealLocalizable nextvalue = iterator.next();

			copytruths.add(nextvalue);

		}

		return copytruths;
	}

	/**
	 * 
	 * Get the mean XY co-ordinates from the list
	 * 
	 * @param truths
	 * @return
	 */

	public static RealLocalizable getMeanCord(List<RealLocalizable> truths) {

		Iterator<RealLocalizable> iter = truths.iterator();
		double Xmean = 0, Ymean = 0;
		while (iter.hasNext()) {

			RealLocalizable currentpair = iter.next();

			RealLocalizable currentpoint = currentpair;

			Xmean += currentpoint.getDoublePosition(0);
			Ymean += currentpoint.getDoublePosition(1);

		}
		RealPoint meanCord = new RealPoint(new double[] { Xmean / truths.size(), Ymean / truths.size() });

		return meanCord;
	}

	public static RealLocalizable getMaxYCord(List<RealLocalizable> truths) {

		double maxVal = Double.MIN_VALUE;
		RealLocalizable minobject = null;
		Iterator<RealLocalizable> iter = truths.iterator();

		while (iter.hasNext()) {

			RealLocalizable currentpair = iter.next();

			if (currentpair.getDoublePosition(1) >= maxVal) {

				minobject = currentpair;
				maxVal = currentpair.getDoublePosition(1);

			}

		}

		return minobject;
	}

	/**
	 * 
	 * Get the mean XY co-ordinates from the list
	 * 
	 * @param truths
	 * @return
	 */

	public static double[] getMeanCordDouble(List<double[]> truths) {

		Iterator<double[]> iter = truths.iterator();
		double Xmean = 0, Ymean = 0;
		while (iter.hasNext()) {

			double[] currentpair = iter.next();

			double[] currentpoint = currentpair;

			Xmean += currentpoint[0];
			Ymean += currentpoint[1];

		}

		return new double[] { Xmean / truths.size(), Ymean / truths.size() };
	}

	public static RealLocalizable getMeanSeg(ArrayList<Pair<String, Segmentobject>> truths) {

		Iterator<Pair<String, Segmentobject>> iter = truths.iterator();

		double Xmean = 0, Ymean = 0;
		int size = 0;
		while (iter.hasNext()) {

			Pair<String, Segmentobject> currentpair = iter.next();

			RealLocalizable currentpoint = currentpair.getB().Cellcentralpoint;

			Xmean += currentpoint.getDoublePosition(0);
			Ymean += currentpoint.getDoublePosition(1);
			size++;
		}

		RealPoint meanCord = new RealPoint(new double[] { Xmean / size, Ymean / size });
		return meanCord;
	}

	/**
	 * 
	 * Get the starting XY co-ordinates to create an ordered list, start from minX
	 * and minY
	 * 
	 * @param truths
	 * @return
	 */

	public static RealLocalizable getRandomCord(List<RealLocalizable> truths, int index) {

		RealLocalizable minobject = truths.get(index);

		return minobject;
	}

	/**
	 * 
	 * Get the starting XY co-ordinates to create an ordered list, start from minX
	 * and minY
	 * 
	 * @param truths
	 * @return
	 */

	public static RealLocalizable getMinCord(List<RealLocalizable> truths) {

		double minVal = Double.MAX_VALUE;
		
		RealLocalizable minobject = null;
		Iterator<RealLocalizable> iter = truths.iterator();

		while (iter.hasNext()) {

			RealLocalizable currentpair = iter.next();

			if (currentpair.getDoublePosition(1) <= minVal ) {

				minobject = currentpair;
				minVal = currentpair.getDoublePosition(1);
				

			}
			

		}

		return minobject;
	}

	/**
	 * 
	 * Get the starting XY co-ordinates to create an ordered list, start from minX
	 * and minY
	 * 
	 * @param truths
	 * @return
	 */

	public static Pair<String, Segmentobject> getMinSegCord(ArrayList<Pair<String, Segmentobject>> truths) {

		double minVal = Double.MAX_VALUE;
		
		Pair<String, Segmentobject> minobject = null;
		Iterator<Pair<String, Segmentobject>> iter = truths.iterator();

		while (iter.hasNext()) {

			Pair<String, Segmentobject> currentpair = iter.next();
			if (currentpair.getB().centralpoint.getDoublePosition(1) <= minVal) {

				minobject = currentpair;
				minVal = currentpair.getB().centralpoint.getDoublePosition(1);
				
			}
			
		}

		return minobject;
	}

	public static Pair<String, Intersectionobject> getMinIntersectionCord(
			ArrayList<Pair<String, Intersectionobject>> truths) {

		double minVal = Double.MAX_VALUE;
		
		Pair<String, Intersectionobject> minobject = null;
		Iterator<Pair<String, Intersectionobject>> iter = truths.iterator();

		while (iter.hasNext()) {
			Pair<String, Intersectionobject> currentpair = iter.next();

			ArrayList<double[]> Linelist = currentpair.getB().linelist;

			for (int i = 0; i < Linelist.size(); ++i) {
				if (currentpair.getB().linelist.get(i)[0] < minVal) {

					minobject = currentpair;
					minVal = currentpair.getB().linelist.get(i)[0];
				}
				
			}
		}

		return minobject;
	}

	/**
	 * 
	 * 
	 * Get the Next nearest point in the list
	 * 
	 * @param minCord
	 * @param truths
	 * @return
	 */

	public static RealLocalizable getNextNearest(RealLocalizable minCord, List<RealLocalizable> truths) {

		RealLocalizable nextobject = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(truths.size());
		final List<FlagNode<RealLocalizable>> targetNodes = new ArrayList<FlagNode<RealLocalizable>>(truths.size());

		for (RealLocalizable localcord : truths) {

			targetCoords.add(new RealPoint(localcord));
			targetNodes.add(new FlagNode<RealLocalizable>(localcord));
		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealLocalizable>> Tree = new KDTree<FlagNode<RealLocalizable>>(targetNodes,
					targetCoords);

			final NNFlagsearchKDtree<RealLocalizable> Search = new NNFlagsearchKDtree<RealLocalizable>(Tree);

			Search.search(minCord);

			final FlagNode<RealLocalizable> targetNode = Search.getSampler().get();

			nextobject = targetNode.getValue();
		}

		return nextobject;

	}

	/**
	 * 
	 * 
	 * Get the Next nearest point in the list
	 * 
	 * @param minCord
	 * @param truths
	 * @return
	 */

	public static Pair<String, Segmentobject> getSegNextNearest(Pair<String, Segmentobject> minCord,
			List<Pair<String, Segmentobject>> truths) {

		Pair<String, Segmentobject> nextobject = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(truths.size());
		final List<FlagNode<Pair<String, Segmentobject>>> targetNodes = new ArrayList<FlagNode<Pair<String, Segmentobject>>>(
				truths.size());

		for (Pair<String, Segmentobject> localcord : truths) {

			targetCoords.add(new RealPoint(localcord.getB().centralpoint));
			targetNodes.add(new FlagNode<Pair<String, Segmentobject>>(localcord));
		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Pair<String, Segmentobject>>> Tree = new KDTree<FlagNode<Pair<String, Segmentobject>>>(
					targetNodes, targetCoords);

			final NNFlagsearchKDtree<Pair<String, Segmentobject>> Search = new NNFlagsearchKDtree<Pair<String, Segmentobject>>(
					Tree);

			Search.search(minCord.getB());

			final FlagNode<Pair<String, Segmentobject>> targetNode = Search.getSampler().get();

			nextobject = targetNode.getValue();
		}

		return nextobject;

	}

	public static Pair<String, Intersectionobject> getInterNextNearest(Pair<String, Intersectionobject> minCord,
			List<Pair<String, Intersectionobject>> truths) {

		Pair<String, Intersectionobject> nextobject = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(truths.size());
		final List<FlagNode<Pair<String, Intersectionobject>>> targetNodes = new ArrayList<FlagNode<Pair<String, Intersectionobject>>>(
				truths.size());

		for (Pair<String, Intersectionobject> localcord : truths) {

			targetCoords.add(new RealPoint(localcord.getB().Intersectionpoint));
			targetNodes.add(new FlagNode<Pair<String, Intersectionobject>>(localcord));
		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Pair<String, Intersectionobject>>> Tree = new KDTree<FlagNode<Pair<String, Intersectionobject>>>(
					targetNodes, targetCoords);

			final NNFlagsearchKDtree<Pair<String, Intersectionobject>> Search = new NNFlagsearchKDtree<Pair<String, Intersectionobject>>(
					Tree);

			Search.search(minCord.getB());

			final FlagNode<Pair<String, Intersectionobject>> targetNode = Search.getSampler().get();

			nextobject = targetNode.getValue();
		}

		return nextobject;

	}

	/**
	 * 
	 * 
	 * Get the Next nearest point in the list
	 * 
	 * @param minCord
	 * @param truths
	 * @return
	 */

	public static Pair<RealLocalizable, Double> getNextNearestPoint(RealLocalizable minCord,
			List<Pair<RealLocalizable, Double>> truths) {

		Pair<RealLocalizable, Double> nextobject = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(truths.size());
		final List<FlagNode<Pair<RealLocalizable, Double>>> targetNodes = new ArrayList<FlagNode<Pair<RealLocalizable, Double>>>(
				truths.size());

		for (Pair<RealLocalizable, Double> localcord : truths) {

			targetCoords.add(new RealPoint(localcord.getA()));
			targetNodes.add(new FlagNode<Pair<RealLocalizable, Double>>(localcord));
		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Pair<RealLocalizable, Double>>> Tree = new KDTree<FlagNode<Pair<RealLocalizable, Double>>>(
					targetNodes, targetCoords);

			final NNFlagsearchKDtree<Pair<RealLocalizable, Double>> Search = new NNFlagsearchKDtree<Pair<RealLocalizable, Double>>(
					Tree);

			Search.search(minCord);

			final FlagNode<Pair<RealLocalizable, Double>> targetNode = Search.getSampler().get();

			nextobject = targetNode.getValue();
		}

		return nextobject;

	}

}
