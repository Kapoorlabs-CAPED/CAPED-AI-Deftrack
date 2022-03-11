package visualization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.GraphIterator;

import linkers.Model3D;
import utility.ThreeDRoiobject;

/**
 * A component of {@link Model} that handles ThreeDRoiobject and edges selection.
 * @author Jean-Yves Tinevez
 */
public class SelectionModel {

	private static final boolean DEBUG = false;

	/** The ThreeDRoiobject current selection. */
	private Set<ThreeDRoiobject> ThreeDRoiobjectSelection = new HashSet<>();
	/** The edge current selection. */
	private Set<DefaultWeightedEdge> edgeSelection = new HashSet<>();
	/** The list of listener listening to change in selection. */
	private List<SelectionChangeListener> selectionChangeListeners = new ArrayList<>();

	private final Model3D model;

	/*
	 * DEFAULT VISIBILITY CONSTRUCTOR
	 */

	public SelectionModel(Model3D parent) {
		this.model = parent;
	}

	/*
	 * DEAL WITH SELECTION CHANGE LISTENER
	 */

	public boolean addSelectionChangeListener(SelectionChangeListener listener) {
		return selectionChangeListeners.add(listener);
	}

	public boolean removeSelectionChangeListener(SelectionChangeListener listener) {
		return selectionChangeListeners.remove(listener);
	}

	public List<SelectionChangeListener> getSelectionChangeListener() {
		return selectionChangeListeners;
	}

	/*
	 * SELECTION CHANGES
	 */

	public void clearSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing selection");
		// Prepare event
		Map<ThreeDRoiobject, Boolean> ThreeDRoiobjectMap = new HashMap<>(ThreeDRoiobjectSelection.size());
		for (ThreeDRoiobject ThreeDRoiobject : ThreeDRoiobjectSelection)
			ThreeDRoiobjectMap.put(ThreeDRoiobject, false);
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edgeSelection.size());
		for (DefaultWeightedEdge edge : edgeSelection)
			edgeMap.put(edge, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, ThreeDRoiobjectMap, edgeMap);
		// Clear fields
		clearThreeDRoiobjectSelection();
		clearEdgeSelection();
		// Fire event
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void clearThreeDRoiobjectSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing ThreeDRoiobject selection");
		// Prepare event
		Map<ThreeDRoiobject, Boolean> ThreeDRoiobjectMap = new HashMap<>(ThreeDRoiobjectSelection.size());
		for (ThreeDRoiobject ThreeDRoiobject : ThreeDRoiobjectSelection)
			ThreeDRoiobjectMap.put(ThreeDRoiobject, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, ThreeDRoiobjectMap, null);
		// Clear field
		ThreeDRoiobjectSelection.clear();
		// Fire event
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void clearEdgeSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing edge selection");
		// Prepare event
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edgeSelection.size());
		for (DefaultWeightedEdge edge : edgeSelection)
			edgeMap.put(edge, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		// Clear field
		edgeSelection.clear();
		// Fire event
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addThreeDRoiobjectToSelection(final ThreeDRoiobject ThreeDRoiobject) {
		if (!ThreeDRoiobjectSelection.add(ThreeDRoiobject))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Adding ThreeDRoiobject " + ThreeDRoiobject + " to selection");
		Map<ThreeDRoiobject, Boolean> ThreeDRoiobjectMap = new HashMap<>(1);
		ThreeDRoiobjectMap.put(ThreeDRoiobject, true);
		if (DEBUG)
			System.out.println("[SelectionModel] Seding event to listeners: "+selectionChangeListeners);
		SelectionChangeEvent event = new SelectionChangeEvent(this, ThreeDRoiobjectMap, null);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeThreeDRoiobjectFromSelection(final ThreeDRoiobject ThreeDRoiobject) {
		if (!ThreeDRoiobjectSelection.remove(ThreeDRoiobject))
			return; // Do nothing was not already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Removing ThreeDRoiobject " + ThreeDRoiobject + " from selection");
		Map<ThreeDRoiobject, Boolean> ThreeDRoiobjectMap = new HashMap<>(1);
		ThreeDRoiobjectMap.put(ThreeDRoiobject, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, ThreeDRoiobjectMap, null);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addThreeDRoiobjectToSelection(final Collection<ThreeDRoiobject> ThreeDRoiobjects) {
		Map<ThreeDRoiobject, Boolean> ThreeDRoiobjectMap = new HashMap<>(ThreeDRoiobjects.size());
		for (ThreeDRoiobject ThreeDRoiobject : ThreeDRoiobjects) {
			if (ThreeDRoiobjectSelection.add(ThreeDRoiobject)) {
				ThreeDRoiobjectMap.put(ThreeDRoiobject, true);
				if (DEBUG)
					System.out.println("[SelectionModel] Adding ThreeDRoiobject " + ThreeDRoiobject + " to selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, ThreeDRoiobjectMap, null);
		if (DEBUG) 
			System.out.println("[SelectionModel] Seding event "+event.hashCode()+" to "+selectionChangeListeners.size()+" listeners: "+selectionChangeListeners);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeThreeDRoiobjectFromSelection(final Collection<ThreeDRoiobject> ThreeDRoiobjects) {
		Map<ThreeDRoiobject, Boolean> ThreeDRoiobjectMap = new HashMap<>(ThreeDRoiobjects.size());
		for (ThreeDRoiobject ThreeDRoiobject : ThreeDRoiobjects) {
			if (ThreeDRoiobjectSelection.remove(ThreeDRoiobject)) {
				ThreeDRoiobjectMap.put(ThreeDRoiobject, false);
				if (DEBUG)
					System.out.println("[SelectionModel] Removing ThreeDRoiobject " + ThreeDRoiobject + " from selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, ThreeDRoiobjectMap, null);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addEdgeToSelection(final DefaultWeightedEdge edge) {
		if (!edgeSelection.add(edge))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Adding edge " + edge + " to selection");
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(1);
		edgeMap.put(edge, true);
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);

	}

	public void removeEdgeFromSelection(final DefaultWeightedEdge edge) {
		if (!edgeSelection.remove(edge))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Removing edge " + edge + " from selection");
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(1);
		edgeMap.put(edge, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);

	}

	public void addEdgeToSelection(final Collection<DefaultWeightedEdge> edges) {
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edges.size());
		for (DefaultWeightedEdge edge : edges) {
			if (edgeSelection.add(edge)) {
				edgeMap.put(edge, true);
				if (DEBUG)
					System.out.println("[SelectionModel] Adding edge " + edge + " to selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeEdgeFromSelection(final Collection<DefaultWeightedEdge> edges) {
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<>(edges.size());
		for (DefaultWeightedEdge edge : edges) {
			if (edgeSelection.remove(edge)) {
				edgeMap.put(edge, false);
				if (DEBUG)
					System.out.println("[SelectionModel] Removing edge " + edge + " from selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public Set<ThreeDRoiobject> getThreeDRoiobjectSelection() {
		return ThreeDRoiobjectSelection;
	}

	public Set<DefaultWeightedEdge> getEdgeSelection() {
		return edgeSelection;
	}

	/*
	 * SPECIAL METHODS
	 */


	/**
	 * Search and add all ThreeDRoiobjects and links belonging to the same track(s) that of given <code>ThreeDRoiobjects</code> and 
	 * <code>edges</code> to current selection. A <code>direction</code> parameter allow specifying
	 * whether we should include only parts upwards in time, downwards in time or all the way through. 
	 * @param ThreeDRoiobjects  the ThreeDRoiobjects to include in search
	 * @param edges  the edges to include in search
	 * @param direction  the direction to go when searching. Positive integers will result in searching
	 * upwards in time, negative integers downwards in time and 0 all the way through.
	 */
	public void selectTrack(final Collection<ThreeDRoiobject> ThreeDRoiobjects, final Collection<DefaultWeightedEdge> edges, final int direction) {

		HashSet<ThreeDRoiobject> inspectionThreeDRoiobjects = new HashSet<>(ThreeDRoiobjects);

		for(DefaultWeightedEdge edge : edges) {
			// We add connected ThreeDRoiobjects to the list of ThreeDRoiobjects to inspect
			inspectionThreeDRoiobjects.add(model.getTrackModel().getEdgeSource(edge));
			inspectionThreeDRoiobjects.add(model.getTrackModel().getEdgeTarget(edge));
		}

		// Walk across tracks to build selection
		final HashSet<ThreeDRoiobject> lThreeDRoiobjectSelection 					= new HashSet<>();
		final HashSet<DefaultWeightedEdge> lEdgeSelection 	= new HashSet<>();

		if (direction == 0) { // Unconditionally
			for (ThreeDRoiobject ThreeDRoiobject : inspectionThreeDRoiobjects) {
				lThreeDRoiobjectSelection.add(ThreeDRoiobject);
				GraphIterator<ThreeDRoiobject, DefaultWeightedEdge> walker = model.getTrackModel().getDepthFirstIterator(ThreeDRoiobject, false);
				while (walker.hasNext()) { 
					ThreeDRoiobject target = walker.next();
					lThreeDRoiobjectSelection.add(target); 
					// Deal with edges
					Set<DefaultWeightedEdge> targetEdges = model.getTrackModel().edgesOf(target);
					for(DefaultWeightedEdge targetEdge : targetEdges) {
						lEdgeSelection.add(targetEdge);
					}
				}
			}

		} else { // Only upward or backward in time 
			for (ThreeDRoiobject ThreeDRoiobject : inspectionThreeDRoiobjects) {
				lThreeDRoiobjectSelection.add(ThreeDRoiobject);

				// A bit more complicated: we want to walk in only one direction,
				// when branching is occurring, we do not want to get back in time.
				Stack<ThreeDRoiobject> stack = new Stack<>();
				stack.add(ThreeDRoiobject);
				while (!stack.isEmpty()) { 
					ThreeDRoiobject inspected = stack.pop();
					Set<DefaultWeightedEdge> targetEdges = model.getTrackModel().edgesOf(inspected);
					for(DefaultWeightedEdge targetEdge : targetEdges) {
						ThreeDRoiobject other;
						if (direction > 0) {
							// Upward in time: we just have to search through edges using their source ThreeDRoiobjects
							other = model.getTrackModel().getEdgeSource(targetEdge);
						} else {
							other = model.getTrackModel().getEdgeTarget(targetEdge);
						}

						if (other != inspected) {
							lThreeDRoiobjectSelection.add(other);
							lEdgeSelection.add(targetEdge);
							stack.add(other);
						}
					}
				}
			}
		}

		// Cut "tail": remove the first an last edges in time, so that the selection only has conencted 
		// edges in it.
		ArrayList<DefaultWeightedEdge> edgesToRemove = new ArrayList<>();
		for(DefaultWeightedEdge edge : lEdgeSelection) {
			ThreeDRoiobject source = model.getTrackModel().getEdgeSource(edge);
			ThreeDRoiobject target = model.getTrackModel().getEdgeTarget(edge);
			if ( !(lThreeDRoiobjectSelection.contains(source) && lThreeDRoiobjectSelection.contains(target)) ) {
				edgesToRemove.add(edge);
			}
		}
		lEdgeSelection.removeAll(edgesToRemove);

		// Set selection
		addThreeDRoiobjectToSelection(lThreeDRoiobjectSelection);
		addEdgeToSelection(lEdgeSelection);
	}

}
