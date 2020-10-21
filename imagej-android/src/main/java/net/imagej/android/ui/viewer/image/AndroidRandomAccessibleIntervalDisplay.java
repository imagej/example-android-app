/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2020 SciJava developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.android.ui.viewer.image;

import android.graphics.Bitmap;

import net.imagej.display.ColorTables;
import net.imagej.display.SourceOptimizedCompositeXYProjector;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.ComputeMinMax;
import net.imglib2.converter.RealLUTConverter;
import net.imglib2.display.projector.composite.CompositeXYProjector;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import org.scijava.Priority;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;

/**
 * Default display for text.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Display.class, priority = Priority.HIGH)
public class AndroidRandomAccessibleIntervalDisplay extends AbstractDisplay<RandomAccessibleInterval> implements
	RandomAccessibleIntervalDisplay
{


	public AndroidRandomAccessibleIntervalDisplay() {
		super(RandomAccessibleInterval.class);
	}


	@Override
	public RandomAccessibleInterval getActiveRAI() {
		return get(0);
	}

	@Override
	public boolean isVisible(RandomAccessibleInterval rai) {
		return false;
	}

	Bitmap getBitmap() {
		return toBufferedImage(getActiveRAI());
	}


	private <T extends RealType<T>> Bitmap toBufferedImage(RandomAccessibleInterval<T> img) {
		for (int i = 2; i < img.numDimensions(); i++) {
			img = Views.hyperSlice(img, i, 0);
		}
		long width = img.dimension(0);
		long height = img.dimension(1);
		RandomAccessibleInterval<ARGBType> screenImage = new ArrayImgFactory<>(new ARGBType()).create(width, height);
		T min = img.randomAccess().get().copy();
		T max = img.randomAccess().get().copy();
		ComputeMinMax<T> minMax = new ComputeMinMax<>(Views.iterable(img), min, max);
		minMax.process();
		RealLUTConverter<? extends RealType<?>> converter = new RealLUTConverter<>(min.getRealDouble(),
				max.getRealDouble(), ColorTables.GRAYS);
		ArrayList<RealLUTConverter<? extends RealType<?>>> converters = new ArrayList<>();
		converters.add(converter);
		CompositeXYProjector projector;
		if (AbstractCellImg.class.isAssignableFrom(img.getClass())) {
			projector =
					new SourceOptimizedCompositeXYProjector(img,
							screenImage, converters, -1);
		}
		else {
			projector =
					new CompositeXYProjector(img, Views.iterable(screenImage),
							converters, -1);
		}
		projector.setComposite(false);
		projector.map();
		Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.RGBA_F16, true);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				bitmap.setPixel(x, y, screenImage.getAt(x, y).get());
			}
		}
		return bitmap;
	}
}
