package hashMapSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ellipsoidDetector.Intersectionobject;
import kalmanForSegments.Segmentobject;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.Listordereing;

public class SortTimeorZ {

	
	/**
	 * Sort Z or T hashmap by comparing the order in Z or T
	 * 
	 * @param map
	 * @return
	 */
	public static HashMap<String, Integer> sortByValues(HashMap<String, Integer> map) {
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, Integer> sortedHashMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
	public static HashMap<String, ArrayList<Segmentobject>> sortByInteger(HashMap<String, ArrayList<Segmentobject>> map) {
		List<Entry<String, ArrayList<Segmentobject>>> list = new LinkedList<Entry<String, ArrayList<Segmentobject>>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, ArrayList<Segmentobject>>>() {

			@Override
			public int compare(Entry<String, ArrayList<Segmentobject>> o1, Entry<String, ArrayList<Segmentobject>> o2) {
				
				return Integer.parseInt(o1.getKey()) - Integer.parseInt(o2.getKey());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, ArrayList<Segmentobject>> sortedHashMap = new LinkedHashMap<String, ArrayList<Segmentobject>>();
		for (Iterator<Entry<String, ArrayList<Segmentobject>>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, ArrayList<Segmentobject>> entry = (Map.Entry<String, ArrayList<Segmentobject>>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
	public static HashMap<String, ArrayList<Intersectionobject>> sortByIntegerInter(HashMap<String, ArrayList<Intersectionobject>> map) {
		List<Entry<String, ArrayList<Intersectionobject>>> list = new LinkedList<Entry<String, ArrayList<Intersectionobject>>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, ArrayList<Intersectionobject>>>() {

			@Override
			public int compare(Entry<String, ArrayList<Intersectionobject>> o1, Entry<String, ArrayList<Intersectionobject>> o2) {
				
				return Integer.parseInt(o1.getKey()) - Integer.parseInt(o2.getKey());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, ArrayList<Intersectionobject>> sortedHashMap = new LinkedHashMap<String, ArrayList<Intersectionobject>>();
		for (Iterator<Entry<String, ArrayList<Intersectionobject>>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, ArrayList<Intersectionobject>> entry = (Map.Entry<String, ArrayList<Intersectionobject>>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
	
	
	

	
	
	
}
