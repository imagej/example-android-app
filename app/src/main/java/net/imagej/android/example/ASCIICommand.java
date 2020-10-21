package net.imagej.android.example;

import net.imagej.android.ImgLibUtils;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.InteractiveCommand;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

@Plugin(type = Command.class, name = "ASCIICommand")
public class ASCIICommand extends InteractiveCommand {

    @Parameter
    private RandomAccessibleInterval<ARGBType> input;

    @Parameter(min = "0.01", max = "0.2")
    private float scale = 0.1f;

    @Parameter(type = ItemIO.OUTPUT, label = "Image")
    private RandomAccessibleInterval<UnsignedByteType> gray;

    @Parameter
    private OpService opService;

    @Parameter
    private LogService logService;

    @Parameter
    private UIService uiService;

    @Override
    public void run() {
        if(input == null) {
            logService.error("Input image missing");
            return;
        }
        RandomAccessibleInterval<UnsignedByteType> slice = convertGray(input);
        gray = ImgLibUtils.scale(slice, scale);
        String ascii = opService.image().ascii(Views.iterable(gray));
        uiService.show("ascii", ascii);
        saveInputs();
    }

    private RandomAccessibleInterval<UnsignedByteType> convertGray(RandomAccessibleInterval<ARGBType> img) {
        return Converters.argbChannel( img, 2 );
    }

}
