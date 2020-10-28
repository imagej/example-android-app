package net.imagej.android.example;

import net.imagej.android.ImgLibUtils;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

import org.scijava.android.command.DisplayUpdatingInteractiveCommand;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, name = "ASCII converter")
public class ASCIICommand extends DisplayUpdatingInteractiveCommand {

    @Parameter
    private RandomAccessibleInterval<ARGBType> input;

    @Parameter(min = "0.01", max = "0.2")
    private float scale = 0.1f;

    @Parameter
    private OpService ops;

    @Parameter
    private LogService log;

    @Override
    public void run() {
        if(input == null) {
            log.error("Input image missing");
            return;
        }
        RandomAccessibleInterval<UnsignedByteType> gray = Converters.argbChannel(input, 2);
        setOutput("gray", gray);
        gray = ImgLibUtils.scale(gray, scale);
        String ascii = ops.image().ascii(Views.iterable(gray));
        setOutput("ascii", ascii);
        saveInputs();
    }
}
