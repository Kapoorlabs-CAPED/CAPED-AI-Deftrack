package strategies;

import net.imglib2.RandomAccessible;
import net.imglib2.type.logic.BitType;

/**
 * An interface for a simple thinning strategy employed by the thinningOp.
 * @author Andreas Burger, University of Konstanz
 */
public interface ThinningStrategy {

    /**
     * This method should determine whether to keep a foreground pixel or not.
     *
     * @param position Long Array containing the current position in the image.
     * @param access The image to thin.
     * @return True if pixel can be switched to background, false otherwise.
     */
    public  boolean removePixel(final long[] position, final RandomAccessible<BitType> accessible);

    /**
     * Returns the minimum number of iterations necessary for the algorithm to run. This delays termination of
     * the thinning algorithm until the end of the current cycle. If, for example, no changes occur during the second
     * iteration of a 4-iteration-cycle, iterations 3 and 4 still take place.
     *
     * @return The number of iterations per cycle.
     */
    public  int getIterationsPerCycle();

    /**
     * Called by the ThinningOp after each cycle, and thus exactly getIterationsPerCycle()-times per iteration. Used for
     * performing different calculations in each step of the cycle.
     */
    public  void afterCycle();

    /**
     * Returns a seperate copy of this strategy.
     * @return A new instance of this strategy with the same values.
     */
    public  ThinningStrategy copy();

}