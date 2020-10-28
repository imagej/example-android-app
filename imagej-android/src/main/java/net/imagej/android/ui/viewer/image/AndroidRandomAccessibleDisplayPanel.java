package net.imagej.android.ui.viewer.image;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.imagej.display.ColorTables;
import net.imagej.display.SourceOptimizedCompositeXYProjector;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.ComputeMinMax;
import net.imglib2.converter.Converter;
import net.imglib2.converter.RealLUTConverter;
import net.imglib2.display.projector.composite.CompositeXYProjector;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

import org.scijava.android.AndroidService;
import org.scijava.android.ui.viewer.AbstractAndroidDisplayPanel;
import org.scijava.android.ui.viewer.Shareable;
import org.scijava.android.ui.viewer.recyclable.LabeledViewHolder;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.thread.ThreadService;
import org.scijava.ui.viewer.DisplayWindow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class AndroidRandomAccessibleDisplayPanel extends AbstractAndroidDisplayPanel<ImageView> implements Shareable {
	private static final int MY_PERMISSIONS_REQUEST = 234;
	private final RandomAccessibleIntervalDisplay display;
	private final DisplayWindow window;

	@Parameter
	private ThreadService threadService;

	@Parameter
	private LogService logService;

	@Parameter
	private AndroidService androidService;

	private LabeledViewHolder<ImageView> holder;
	private WeakReference<Bitmap> bitmapReference;

	public AndroidRandomAccessibleDisplayPanel(RandomAccessibleIntervalDisplay display, DisplayWindow window) {
		display.context().inject(this);
		this.display = display;
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
	public void contentUpdated() {
		if(holder == null) return;
		Handler handler = new BitmapHandler(this);
		if(bitmapReference != null) bitmapReference.clear();
		new Thread(() -> {
			Bitmap bitmap = toBufferedImage(display.get(0));
			Message msg = Message.obtain(handler, 0, bitmap);
			handler.sendMessage(msg);
		}).start();
	}

	@Override
	public void updateView(ImageView view) {
		if(bitmapReference == null) return;
		view.setImageBitmap(bitmapReference.get());
	}

	@Override
	public ImageView createView(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return (ImageView) inflater.inflate(net.imagej.android.R.layout.viewer_image, parent, false);
	}

	@Override
	public Class<ImageView> getViewType() {
		return ImageView.class;
	}

	@Override
	public void attach(LabeledViewHolder<ImageView> holder) {
		this.holder = holder;
		if(bitmapReference != null && bitmapReference.get() != null) {
			holder.getItem().setImageBitmap(bitmapReference.get());
		} else {
			contentUpdated();
		}
	}

	@Override
	public void detach(LabeledViewHolder<ImageView> holder) {
		this.holder = null;
	}

	@Override
	public void share(Activity activity) {
		if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (activity.shouldShowRequestPermissionRationale(
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				// Explain to the user why we need to read the contacts
			}

			activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					MY_PERMISSIONS_REQUEST);

			return;
		}
		String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmapReference.get(), getLabel(), null);
		Uri uri = Uri.parse(path);
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/png");
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		Intent shareIntent = Intent.createChooser(intent, "Share " + getLabel());
		activity.startActivity(shareIntent);
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
		bitmapReference = new WeakReference<>(bitmap);
		if(holder == null) return;
//		threadService.queue(() -> {
			if(holder.getItem() != null && holder.getInput() == this) holder.getItem().setImageBitmap(bitmap);
//		});
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
