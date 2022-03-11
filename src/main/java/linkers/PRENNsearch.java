package linkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JProgressBar;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import utility.PreRoiobject;

public class PRENNsearch implements BlobTracker {

	private final ConcurrentHashMap<String, ArrayList<PreRoiobject>> Allblobs;
	private final double maxdistance;
	private int T;
	public JProgressBar jpb;
	private SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > graph;
	protected Logger logger = Logger.DEFAULT_LOGGER;
	protected String errorMessage;
	private HashMap<String, Integer> AccountedZ;
	public PRENNsearch(
			final ConcurrentHashMap<String, ArrayList<PreRoiobject>> Allblobs, final double maxdistance, final int T,
			 final HashMap<String, Integer> AccountedZ, JProgressBar jpb){
		this.Allblobs = Allblobs;
		this.maxdistance = maxdistance;
		this.T = T;
		this.AccountedZ = AccountedZ;
		this.jpb = jpb;
		
		
	}
	
	
	

	@Override
	public boolean process() {

		reset();
		
		Iterator<Map.Entry<String, Integer>> it = AccountedZ.entrySet().iterator();
		int percent = 0;
		while(it.hasNext()) {
		
           percent++;
		
			int Z = it.next().getValue();
			
			
			while(it.hasNext()) {
				

				utility.CovsitoProgressBar.CovistoSetProgressBar(jpb, 100 * percent / (AccountedZ.size() - 1),
						"Computing Nearest Neighbours for " + " T = " + T 
								+ " Z = " + Z);
				int nextZ = it.next().getValue();
			

				String uniqueID = Integer.toString(Z) + Integer.toString(T);
				String uniqueIDnext = Integer.toString(nextZ) + Integer.toString(T);
				
				
			ArrayList<PreRoiobject> Spotmaxbase = Allblobs.get(uniqueID);
			
			ArrayList<PreRoiobject> Spotmaxtarget = Allblobs.get(uniqueIDnext);
			
			
			if (Spotmaxtarget != null && Spotmaxtarget.size() > 0) {
			
			Iterator<PreRoiobject> baseobjectiterator = Spotmaxbase.iterator();
			
			
			
	        final int Targetblobs = Spotmaxtarget.size();
	        
			final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Targetblobs);

			final List<FlagNode<PreRoiobject>> targetNodes = new ArrayList<FlagNode<PreRoiobject>>(Targetblobs);
			
			
	      
			for (int index = 0; index < Spotmaxtarget.size(); ++index){
				
				
				
				targetCoords.add(new RealPoint(Spotmaxtarget.get(index).geometriccenter));

				targetNodes.add(new FlagNode<PreRoiobject>(Spotmaxtarget.get(index)));
				
				
			}
			
			if (targetNodes.size() > 0 && targetCoords.size() > 0){
			
			final KDTree<FlagNode<PreRoiobject>> Tree = new KDTree<FlagNode<PreRoiobject>>(targetNodes, targetCoords);
			
			final NNFlagsearchKDtree<PreRoiobject> Search = new NNFlagsearchKDtree<PreRoiobject>(Tree);
			
			
			while(baseobjectiterator.hasNext()){
				
				final PreRoiobject source = baseobjectiterator.next();
				final RealPoint sourceCoords = new RealPoint(source.geometriccenter);
				Search.search(sourceCoords);
				final double squareDist = Search.getSquareDistance();
				
				if (squareDist > maxdistance)
					continue;
				
				
				final FlagNode<PreRoiobject> targetNode = Search.getSampler().get();
				

				targetNode.setVisited(true);
				
				synchronized (graph) {
					
					graph.addVertex(source);
					graph.addVertex(targetNode.getValue());
					final DefaultWeightedEdge edge = graph.addEdge(source, targetNode.getValue());
					graph.setEdgeWeight(edge, squareDist);
					
					
				}
			
		       
			}
			
		}
		}
			
			Z = nextZ;
		}
	}
			return true;
			
		}
	

	@Override
	public void setLogger( final Logger logger) {
		this.logger = logger;
		
	}
	

	@Override
	public SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > getResult()
	{
		return graph;
	}
	
	
	
	@Override
	public boolean checkInput() {
		final StringBuilder errrorHolder = new StringBuilder();;
		final boolean ok = checkInput();
		if (!ok) {
			errorMessage = errrorHolder.toString();
		}
		return ok;
	}
	

	public void reset() {
		
		graph = new SimpleWeightedGraph<PreRoiobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		if (Allblobs!=null && Allblobs.size() > 0) {
			
		ArrayList<PreRoiobject> firstobject = 	Allblobs.entrySet().iterator().next().getValue();
			
		
		
		final Iterator<PreRoiobject> it = firstobject.iterator();
		while (it.hasNext()) {
			graph.addVertex(it.next());
		}
		
		}
	}

	@Override
	public String getErrorMessage() {
		
		return errorMessage;
	}
}
