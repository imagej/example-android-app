package net.imagej.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.RealViews;
import net.imglib2.realtransform.Scale;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import java.util.Arrays;

public class ImgLibUtils {

    public static void cameraBytesToImage(byte[] bytes, RandomAccessibleInterval<ARGBType> img) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        RandomAccess<ARGBType> ra = img.randomAccess();
        for (int i = 0; i < img.dimension(0); i++) {
            for (int j = 0; j < img.dimension(1); j++) {
                int p = bitmap.getPixel(i, j);
                int R = (p & 0xff0000) >> 16;
                int G = (p & 0x00ff00) >> 8;
                int B = (p & 0x0000ff) >> 0;
                ra.setPositionAndGet(i, j).set(ARGBType.rgba(R, G, B, 255));
            }
        }
    }

    public static RandomAccessibleInterval<UnsignedByteType> scale(RandomAccessibleInterval<UnsignedByteType> img, double factor) {
        final double[] factors = new double[img.numDimensions()];
        Arrays.fill(factors, factor);
        return scale(img, factors);
    }

    static RandomAccessibleInterval<UnsignedByteType> scale(RandomAccessibleInterval<UnsignedByteType> img, double[] factor) {
        final long[] newDims = Intervals.dimensionsAsLongArray(img);
        for (int i = 0; i < img.numDimensions(); i++) {
            newDims[i] = Math.round(img.dimension(i) * factor[i]);
        }
        InterpolatorFactory<UnsignedByteType, RandomAccessible<UnsignedByteType>> interpolator = new NearestNeighborInterpolatorFactory<>();
        return Views.interval(Views.raster(RealViews.affineReal(
                Views.interpolate(Views.extendMirrorSingle(img), interpolator),
                new Scale(factor))), new FinalInterval(newDims));
    }
}
