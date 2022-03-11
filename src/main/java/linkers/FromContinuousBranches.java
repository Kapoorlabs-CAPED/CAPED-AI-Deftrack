package linkers;



import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import utility.ThreeDRoiobject;
import utility.ThreeDRoiobject;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class FromContinuousBranches implements OutputAlgorithm< SimpleWeightedGraph< ThreeDRoiobject, DefaultWeightedEdge > >, Benchmark
{

	private static final String BASE_ERROR_MSG = "[FromContinuousBranches] ";

	private long processingTime;

	private final Collection< List< ThreeDRoiobject >> branches;

	private final Collection< List< ThreeDRoiobject >> links;

	private String errorMessage;

	private SimpleWeightedGraph< ThreeDRoiobject, DefaultWeightedEdge > graph;

	public FromContinuousBranches( final Collection< List< ThreeDRoiobject >> branches, final Collection< List< ThreeDRoiobject >> links )
	{
		this.branches = branches;
		this.links = links;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public boolean checkInput()
	{
		final long start = System.currentTimeMillis();
		if ( null == branches )
		{
			errorMessage = BASE_ERROR_MSG + "branches are null.";
			return false;
		}
		if ( null == links )
		{
			errorMessage = BASE_ERROR_MSG + "links are null.";
			return false;
		}
		for ( final List< ThreeDRoiobject > link : links )
		{
			if ( link.size() != 2 )
			{
				errorMessage = BASE_ERROR_MSG + "A link is not made of two ThreeDRoiobjects.";
				return false;
			}
			if ( !checkIfInBranches( link.get( 0 ) ) )
			{
				errorMessage = BASE_ERROR_MSG + "A ThreeDRoiobject in a link is not present in the branch collection: " + link.get( 0 ) + " in the link " + link.get( 0 ) + "-" + link.get( 1 ) + ".";
				return false;
			}
			if ( !checkIfInBranches( link.get( 1 ) ) )
			{
				errorMessage = BASE_ERROR_MSG + "A ThreeDRoiobject in a link is not present in the branch collection: " + link.get( 1 ) + " in the link " + link.get( 0 ) + "-" + link.get( 1 ) + ".";
				return false;
			}
		}
		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public boolean process()
	{
		final long start = System.currentTimeMillis();

		graph = new SimpleWeightedGraph< ThreeDRoiobject, DefaultWeightedEdge >( DefaultWeightedEdge.class );
		for ( final List< ThreeDRoiobject > branch : branches )
		{
			for ( final ThreeDRoiobject ThreeDRoiobject : branch )
			{
				graph.addVertex( ThreeDRoiobject );
			}
		}

		for ( final List< ThreeDRoiobject > branch : branches )
		{
			final Iterator< ThreeDRoiobject > it = branch.iterator();
			ThreeDRoiobject previous = it.next();
			while ( it.hasNext() )
			{
				final ThreeDRoiobject ThreeDRoiobject = it.next();
				graph.addEdge( previous, ThreeDRoiobject );
				previous = ThreeDRoiobject;
			}
		}

		for ( final List< ThreeDRoiobject > link : links )
		{
			graph.addEdge( link.get( 0 ), link.get( 1 ) );
		}

		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public SimpleWeightedGraph< ThreeDRoiobject, DefaultWeightedEdge > getResult()
	{
		return graph;
	}

	private final boolean checkIfInBranches( final ThreeDRoiobject ThreeDRoiobject )
	{
		for ( final List< ThreeDRoiobject > branch : branches )
		{
			if ( branch.contains( ThreeDRoiobject ) ) { return true; }
		}
		return false;
	}

}
