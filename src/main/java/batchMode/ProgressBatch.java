package batchMode;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingWorker;

public class ProgressBatch extends SwingWorker<Void, Void> {

	final ExecuteBatch parent;
	
	public ProgressBatch(final ExecuteBatch parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {

		// set up executor service
		int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		
		if(parent.twochannel)
		ProcessFiles.process(parent.C1_AllImages, parent.C2_AllImages, parent.SegmentationImages, parent.channelA, parent.channelB, parent.channelSeg, parent.twochannel, taskExecutor);
		else
		ProcessFiles.processSingle(parent.C1_AllImages, parent.SegmentationImages, parent.channelA, parent.channelSeg, parent.twochannel, taskExecutor);	
		
		return null;

	}

	@Override
	protected void done() {
		try {
		//	parent.jpb.setIndeterminate(false);
			get();
			
		
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}


}
