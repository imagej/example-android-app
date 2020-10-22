package net.imagej.android.ui.viewer.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import net.imagej.display.ColorTables;
import net.imagej.display.SourceOptimizedCompositeXYProjector;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.ComputeMinMax;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.converter.RealLUTConverter;
import net.imglib2.display.projector.composite.CompositeXYProjector;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

import org.scijava.Context;
import org.scijava.android.ui.viewer.AndroidDisplayPanel;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.thread.ThreadService;
import org.scijava.ui.viewer.DisplayWindow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class AndroidRandomAccessibleDisplayPanel implements RandomAccessibleIntervalDisplayPanel, AndroidDisplayPanel {
	private final AndroidRandomAccessibleIntervalDisplay display;
	private final DisplayWindow window;

	private final ImageView content;

	@Parameter
	private ThreadService threadService;

	@Parameter
	private LogService logService;

	public AndroidRandomAccessibleDisplayPanel(Activity activity, AndroidRandomAccessibleIntervalDisplay display, DisplayWindow window) {
		display.context().inject(this);
		this.display = display;
		content = new ImageView(activity);
		this.window = window;
		window.setContent(this);
	}

	// -- DisplayPanel methods --

	@Override
	public RandomAccessibleIntervalDisplay getDisplay() {
		return display;
	}

	@Override
	public DisplayWindow getWindow() {
		return window;
	}

	@Override
	public void redoLayout() {
		// Nothing to layout
	}

	@Override
	public void setLabel(final String s) {
		// nothing happening here
	}

	@Override
	public void redraw() {

		Handler handler = new BitmapHandler(this);
		new Thread(() -> {
			Bitmap bitmap = toBufferedImage(display.getActiveRAI());
			redraw(bitmap);
			Message msg = Message.obtain(handler, 0, bitmap);
			handler.sendMessage(msg);
		}).start();
	}

	static class BitmapHandler extends Handler {
		private final WeakReference<AndroidRandomAccessibleDisplayPanel> panel;

		public BitmapHandler(AndroidRandomAccessibleDisplayPanel panel) {
			this.panel = new WeakReference<>(panel);
		}
		@Override
		public void handleMessage(Message msg){
			panel.get().redraw((Bitmap) msg.obj);
		}
	}

	private void redraw(Bitmap bitmap) {
		threadService.queue(() -> content.setImageBitmap(bitmap));
	}

	@Override
	public View getPanel() {
		return content;
	}

	private <T extends RealType<T>, U> Bitmap toBufferedImage(RandomAccessibleInterval<U> img) {
		for (int i = 2; i < img.numDimensions(); i++) {
			img = Views.hyperSlice(img, i, 0);
		}
		long width = img.dimension(0);
		long height = img.dimension(1);
		Converter<U, ARGBType > converter = null;
		U first = img.randomAccess().get();
		Img<ARGBType> screenImage = new ArrayImgFactory<>(new ARGBType()).create(width, height);
		if(RealType.class.isAssignableFrom(first.getClass())) {
			converter = (Converter<U, ARGBType>) getRealTypeConverter((RandomAccessibleInterval<T>)img);
		}
		if(first instanceof ARGBType) {
			converter = (Converter<U, ARGBType>) getARGBConverter();
		}
		if(converter == null) {
			logService.error("Could not convert RandomAccessibleInterval of type " + first.getClass());
		}
		ArrayList<Converter<U, ARGBType >> converters = new ArrayList<>();
		converters.add(converter);
		CompositeXYProjector<U> projector;
		if (AbstractCellImg.class.isAssignableFrom(img.getClass())) {
			projector =
					new SourceOptimizedCompositeXYProjector<>(iterable(img),
							screenImage, converters, -1);
		}
		else {
			projector =
					new CompositeXYProjector<>(img, Views.iterable(screenImage),
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

	public static < T, S extends IterableInterval<T> & RandomAccessibleInterval<T> > S iterable(final RandomAccessibleInterval< T > randomAccessibleInterval )
	{
		if ( IterableInterval.class.isInstance( randomAccessibleInterval ) )
		{
			final Class< ? > raiType = Util.getTypeFromInterval( randomAccessibleInterval ).getClass();
			final Iterator< ? > iter = ( ( IterableInterval< ? > ) randomAccessibleInterval ).iterator();
			final Object o = iter.hasNext() ? iter.next() : null;
			if ( raiType.isInstance( o ) )
				return ( S ) randomAccessibleInterval;
		}
		return (S) new IterableRandomAccessibleInterval<T>( randomAccessibleInterval );
	}

	private <T extends RealType<T>> RealLUTConverter<T> getRealTypeConverter(RandomAccessibleInterval<T> img) {
		T min = img.randomAccess().get().copy();
		T max = img.randomAccess().get().copy();
		ComputeMinMax<T> minMax = new ComputeMinMax<>(Views.iterable(img), min, max);
		minMax.process();
		return new RealLUTConverter<>(min.getRealDouble(),
				max.getRealDouble(), ColorTables.GRAYS);
	}

	private Converter<ARGBType, ARGBType> getARGBConverter() {
		return ARGBType::set;
	}

}
