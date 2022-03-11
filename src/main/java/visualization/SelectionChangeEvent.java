package visualization;

import java.util.EventObject;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;

import utility.ThreeDRoiobject;


/**
 * An event that characterizes a change in the current selection. 
 * {@link ThreeDRoiobject} selection and {@link DefaultWeightedEdge} selection are dealt with separately, 
 * to keep the use of this class general.
 */
public class SelectionChangeEvent extends EventObject {

	private static final long serialVersionUID = -8920831578922412606L;

	/** Changes in {@link DefaultWeightedEdge} selection this event represents. */
	private final Map<DefaultWeightedEdge, Boolean> edges;

	/** Changes in {@link ThreeDRoiobject} selection this event represents. */
	protected Map<ThreeDRoiobject, Boolean> ThreeDRoiobjects;

	/*
	 * CONSTRUCTORS 
	 */
	
	/**
	 * Represents a change in the selection of a displayed TM model.
	 * <p>
	 * Two maps are given. The first one represent changes in the ThreeDRoiobject
	 * selection. The {@link Boolean} mapped to a {@link ThreeDRoiobject} key specifies if
	 * the ThreeDRoiobject was added to the selection (<code>true</code>) or removed from
	 * it (<code>false</code>). The same goes for the
	 * {@link DefaultWeightedEdge} map. <code>null</code>s are accepted for the
	 * two maps, to specify that no changes happened for the corresponding type.
	 * 
	 * @param source
	 *            the source object that fires this event.
	 * @param ThreeDRoiobjects
	 *            the ThreeDRoiobjects that are added or removed from the selection by this
	 *            event.
	 * @param edges
	 *            the edges that are added or removed from the selection by this
	 *            event.
	 */
	public SelectionChangeEvent(final Object source, final Map<ThreeDRoiobject, Boolean> ThreeDRoiobjects, final Map<DefaultWeightedEdge, Boolean> edges) {
		super(source);
		this.ThreeDRoiobjects = ThreeDRoiobjects;
		this.edges = edges;
	}
	
	/*
	 * METHODS
	 */
	
	/**
	 * Returns the ThreeDRoiobjects that have been added or removed from the selection.
	 * The {@link Boolean} 
	 * mapped to a {@link ThreeDRoiobject} key specifies if the ThreeDRoiobject was added to the selection (<code>true</code>)
	 * or removed from it (<code>false</code>).
	 * @return added or removed ThreeDRoiobjects, can be <code>null</code> if no changes on ThreeDRoiobject selection happened.
	 */
	public Map<ThreeDRoiobject, Boolean> getThreeDRoiobjects() {
		return ThreeDRoiobjects;
	}
	
	/**
	 * Returns the edges that have been added or removed from the selection.
	 * The {@link Boolean} mapped to a {@link DefaultWeightedEdge} key specifies 
	 * if the edge was added to the selection (<code>true</code>)
	 * or removed from it (<code>false</code>).
	 * @return added or removed edges, can be <code>null</code> if no changes on edge selection happened.
	 */
	public Map<DefaultWeightedEdge, Boolean> getEdges() {
		return edges;
	}

	


}
