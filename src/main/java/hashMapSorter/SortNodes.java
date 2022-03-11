package hashMapSorter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import curvatureUtils.Node;
import net.imglib2.RealLocalizable;

public class SortNodes {

	/*
	 * Sorts the Nodes of the tree by the size of the parent being closest to the Inlier number, in this way we never miss the points on which a function has to be fitted on
	 * 
	 * 
	 */
	public static HashMap<String, Node<RealLocalizable>> sortByValues(HashMap<String, Node<RealLocalizable>> map, int Inliernumber) {
		List<Entry<String, Node<RealLocalizable>>> list = new LinkedList<Entry<String, Node<RealLocalizable>>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, Node<RealLocalizable>>>() {

			@Override
			public int compare(Entry<String, Node<RealLocalizable>> o1, Entry<String, Node<RealLocalizable>> o2) {
				
				int l1 = (int)Math.abs(o1.getValue().leftTree.size() -  Inliernumber);
				int l2 = (int)Math.abs(o2.getValue().leftTree.size() -  Inliernumber);
				return (l1 - l2);
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, Node<RealLocalizable>> sortedHashMap = new LinkedHashMap<String, Node<RealLocalizable>>();
		for (Iterator<Entry<String, Node<RealLocalizable>>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Node<RealLocalizable>> entry = (Map.Entry<String, Node<RealLocalizable>>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
	/*
	 * Sorts the Nodes of the tree by the size of the parent being closest to the Inlier number, in this way we never miss the points on which a function has to be fitted on
	 * 
	 * 
	 */
	public static HashMap<String, Node<RealLocalizable>> sortByRightValues(HashMap<String, Node<RealLocalizable>> map, int Inliernumber) {
		List<Entry<String, Node<RealLocalizable>>> list = new LinkedList<Entry<String, Node<RealLocalizable>>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, Node<RealLocalizable>>>() {

			@Override
			public int compare(Entry<String, Node<RealLocalizable>> o1, Entry<String, Node<RealLocalizable>> o2) {
				
				int l1 = (int)Math.abs(o1.getValue().rightTree.size() -  Inliernumber);
				int l2 = (int)Math.abs(o2.getValue().rightTree.size() -  Inliernumber);
				return (l1 - l2);
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, Node<RealLocalizable>> sortedHashMap = new LinkedHashMap<String, Node<RealLocalizable>>();
		for (Iterator<Entry<String, Node<RealLocalizable>>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Node<RealLocalizable>> entry = (Map.Entry<String, Node<RealLocalizable>>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
	
}
