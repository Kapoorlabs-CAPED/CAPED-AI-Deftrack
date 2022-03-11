package utility;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;


public class ModelChangeEvent extends EventObject {

	private static final long serialVersionUID = -1L;
	/** Indicate that a ThreeDRoiobject was added to the model. */
	public static final int FLAG_ThreeDRoiobject_ADDED = 0;
	/** Indicate that a ThreeDRoiobject was removed from the model. */
	public static final int FLAG_ThreeDRoiobject_REMOVED = 1;
	/**
	 * Indicate a modification of the features of a ThreeDRoiobject. It may have changed of
	 * position and feature, but not of frame.
	 */
	public static final int FLAG_ThreeDRoiobject_MODIFIED = 2;
	/**
	 * Indicate that a ThreeDRoiobject has changed of frame, and possible of position,
	 * features, etc.. .
	 */
	public static final int FLAG_ThreeDRoiobject_FRAME_CHANGED = 3;
	/** Indicate that an edge was added to the model. */
	public static final int FLAG_EDGE_ADDED = 4;
	/** Indicate that an edge was removed from the model. */
	public static final int FLAG_EDGE_REMOVED = 5;
	/**
	 * Indicate that an edge has been modified. Edge modifications occur when
	 * the target or source ThreeDRoiobjects are modified, or when the weight of the edge
	 * has been modified.
	 */
	public static final int FLAG_EDGE_MODIFIED = 6;

	public static final Map<Integer, String> flagsToString = new HashMap<>(7);
	static {
		flagsToString.put(FLAG_ThreeDRoiobject_ADDED, "ThreeDRoiobject added");
		flagsToString.put(FLAG_ThreeDRoiobject_FRAME_CHANGED, "ThreeDRoiobject frame changed");
		flagsToString.put(FLAG_ThreeDRoiobject_MODIFIED, "ThreeDRoiobject modified");
		flagsToString.put(FLAG_ThreeDRoiobject_REMOVED, "ThreeDRoiobject removed");
		flagsToString.put(FLAG_EDGE_ADDED, "Edge added");
		flagsToString.put(FLAG_EDGE_MODIFIED, "Edge modified");
		flagsToString.put(FLAG_EDGE_REMOVED, "Edge removed");
	}


	/**
	 * Event type indicating that the ThreeDRoiobjects of the model were computed, and
	 * are now accessible through {@link Model#getThreeDRoiobjects()}.
	 */
	public static final int 	ThreeDRoiobjectS_COMPUTED = 4;
	/**
	 * Event type indicating that the ThreeDRoiobjects of the model were filtered.
	 */
	public static final int 	ThreeDRoiobjectS_FILTERED = 5;
	/**
	 * Event type indicating that the tracks of the model were computed.
	 */
	public static final int 	TRACKS_COMPUTED = 6;
	/**
	 * Event type indicating that the tracks of the model had their
	 * visibility changed.
	 */
	public static final int 	TRACKS_VISIBILITY_CHANGED = 7;
	
	/**
	 * Event type indicating that model was modified, by adding, removing or
	 * changing the feature of some ThreeDRoiobjects, and/or adding or removing edges in
	 * the tracks. Content of the modification can be accessed by
	 * {@link #getThreeDRoiobjects()}, {@link #getThreeDRoiobjectFlag(ThreeDRoiobject)},
	 * {@link #getFromFrame(ThreeDRoiobject)} and {@link #getToFrame(ThreeDRoiobject)}, and for the
	 * tracks: {@link #getEdges()} and {@link #getEdgeFlag(DefaultWeightedEdge)}
	 * .
	 */
	public static final int 	MODEL_MODIFIED = 8;

	/** ThreeDRoiobjects affected by this event. */
	private final HashSet<ThreeDRoiobject> ThreeDRoiobjects = new HashSet<>();
	/** Edges affected by this event. */
	private final HashSet<DefaultWeightedEdge> edges = new HashSet<>();
	/** For ThreeDRoiobjects removed or moved: frame from which they were removed or moved. */
	private final HashMap<ThreeDRoiobject, Integer> fromFrame = new HashMap<>();
	/** For ThreeDRoiobjects removed or added: frame to which they were added or moved. */
	private final HashMap<ThreeDRoiobject, Integer> toFrame = new HashMap<>();
	/** Modification flag for ThreeDRoiobjects affected by this event. */
	private final HashMap<ThreeDRoiobject, Integer> ThreeDRoiobjectFlags = new HashMap<>();
	/** Modification flag for edges affected by this event. */
	private final HashMap<DefaultWeightedEdge, Integer> edgeFlags = new HashMap<>();
	/** The event type for this instance. */
	private final int eventID;
	private Set<Integer> trackUpdated;

	/**
	 * Create a new event, reflecting a change in a {@link Model}.
	 *
	 * @param source
	 *            the object source of this event.
	 * @param eventID
	 *            the evend ID to use for this event.
	 */
	public ModelChangeEvent(final Object source, final int eventID) {
		super(source);
		this.eventID = eventID;
	}

	public int getEventID() {
		return this.eventID;
	}

	public boolean addAllThreeDRoiobjects(final Collection<ThreeDRoiobject> lThreeDRoiobjects) {
		return this.ThreeDRoiobjects.addAll(lThreeDRoiobjects);
	}

	public boolean addThreeDRoiobject(final ThreeDRoiobject ThreeDRoiobject) {
		return this.ThreeDRoiobjects.add(ThreeDRoiobject);
	}

	public boolean addAllEdges(final Collection<DefaultWeightedEdge> lEdges) {
		return this.edges.addAll(lEdges);
	}
	public boolean addEdge(final DefaultWeightedEdge edge) {
		return edges.add(edge);
	}

	public Integer putEdgeFlag(final DefaultWeightedEdge edge, final Integer flag) {
		return edgeFlags.put(edge, flag);
	}

	public Integer putThreeDRoiobjectFlag(final ThreeDRoiobject ThreeDRoiobject, final Integer flag) {
		return ThreeDRoiobjectFlags.put(ThreeDRoiobject, flag);
	}

	public Integer putFromFrame(final ThreeDRoiobject ThreeDRoiobject, final Integer lFromFrame) {
		return this.fromFrame.put(ThreeDRoiobject, lFromFrame);
	}

	public Integer putToFrame(final ThreeDRoiobject ThreeDRoiobject, final Integer lToFrame) {
		return this.toFrame.put(ThreeDRoiobject, lToFrame);
	}

	/**
	 * @return  the set of ThreeDRoiobject that are affected by this event. Is empty
	 * if no ThreeDRoiobject is affected by this event.
	 */
	public Set<ThreeDRoiobject> getThreeDRoiobjects() {
		return ThreeDRoiobjects;
	}

	/**
	 * @return  the set of edges that are affected by this event. Is empty
	 * if no edge is affected by this event.
	 */
	public Set<DefaultWeightedEdge> getEdges() {
		return edges;
	}

	/**
	 * Returns the modification flag for the given ThreeDRoiobject affected by this event.
	 * 
	 * @param ThreeDRoiobject
	 *            the ThreeDRoiobject to query.
	 * @return the modification flag.
	 * @see #FLAG_ThreeDRoiobject_ADDED
	 * @see #FLAG_ThreeDRoiobject_MODIFIED
	 * @see #FLAG_ThreeDRoiobject_REMOVED
	 */
	public Integer getThreeDRoiobjectFlag(final ThreeDRoiobject ThreeDRoiobject) {
		return ThreeDRoiobjectFlags.get(ThreeDRoiobject);
	}

	/**
	 * Returns the modification flag for the given edge affected by this event.
	 * 
	 * @param edge
	 *            the edge to query.
	 * @return the modification flag.
	 * @see #FLAG_EDGE_ADDED
	 * @see #FLAG_EDGE_REMOVED
	 */
	public Integer getEdgeFlag(final DefaultWeightedEdge edge) {
		return edgeFlags.get(edge);
	}

	public Integer getToFrame(final ThreeDRoiobject ThreeDRoiobject) {
		return toFrame.get(ThreeDRoiobject);
	}

	public Integer getFromFrame(final ThreeDRoiobject ThreeDRoiobject) {
		return fromFrame.get(ThreeDRoiobject);
	}

	public void setSource(final Object source) {
		this.source = source;
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder("[ModelChangeEvent]:\n");
		str.append(" - source: "+source.getClass() + "_" + source.hashCode()+"\n");
		str.append(" - event type: ");
		switch (eventID) {
		case ThreeDRoiobjectS_COMPUTED:
			str.append("ThreeDRoiobjects computed\n");
			break;
		case ThreeDRoiobjectS_FILTERED:
			str.append("ThreeDRoiobjects filtered\n");
			break;
		case TRACKS_COMPUTED:
			str.append("Tracks computed\n");
			break;
		case TRACKS_VISIBILITY_CHANGED:
			str.append("Track visibility changed\n");
			break;
		case MODEL_MODIFIED:
			str.append("Model modified, with:\n");
			str.append("\t- ThreeDRoiobjects modified: "+ (ThreeDRoiobjects != null ? ThreeDRoiobjects.size() : 0) +"\n");
			for (final ThreeDRoiobject ThreeDRoiobject : ThreeDRoiobjects) {
				str.append("\t\t" + ThreeDRoiobject + ": " + flagsToString.get(ThreeDRoiobjectFlags.get(ThreeDRoiobject)) + "\n");
			}
			str.append("\t- edges modified: "+ (edges != null ? edges.size() : 0) +"\n");
			for (final DefaultWeightedEdge edge : edges) {
				str.append("\t\t" + edge + ": " + flagsToString.get(edgeFlags.get(edge)) + "\n");
			}
			str.append("\t- tracks to update: " + trackUpdated + "\n");
		}
		return str.toString();
	}

	public void setTracksUpdated(final Set<Integer> tracksToUpdate) {
		this.trackUpdated = tracksToUpdate;
	}

	/**
	 * @return the IDs of track that were modified or created by this event.
	 */
	public Set<Integer> getTrackUpdated() {
		return trackUpdated;
	}
}
