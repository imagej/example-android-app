package net.imagej.android.ui.viewer.image;

import net.imglib2.RandomAccessibleInterval;

import org.scijava.display.Display;

public interface RandomAccessibleIntervalDisplay extends Display<RandomAccessibleInterval> {

    RandomAccessibleInterval getActiveRAI();

    boolean isVisible(RandomAccessibleInterval rai);

}
