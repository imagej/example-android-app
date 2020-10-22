package net.imagej.android.example;

import net.imagej.android.ImgLibUtils;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

import org.scijava.InstantiableException;
import org.scijava.ItemIO;
import org.scijava.android.command.InteractiveCommandDisplayUpdate;
import org.scijava.command.Command;
import org.scijava.display.DisplayPostprocessor;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;
import org.scijava.plugin.SciJavaPlugin;

@Plugin(type = Command.class, name = "ASCII converter")
public class ASCIICommand extends InteractiveCommandDisplayUpdate {

    @Parameter
    private RandomAccessibleInterval<ARGBType> input;

    @Parameter(min = "0.01", max = "0.2")
    private float scale = 0.1f;

    @Parameter
    private OpService opService;

    @Parameter
    private LogService logService;

    @Parameter(type = ItemIO.OUTPUT)
    private RandomAccessibleInterval<UnsignedByteType> slice;

    @Parameter(type = ItemIO.OUTPUT)
    private String ascii;

    @Override
    public void run() {
        if(input == null) {
            logService.error("Input image missing");
            return;
        }
        RandomAccessibleInterval<UnsignedByteType> slice = convertGray(input);
        setOutput("slice", slice);
        RandomAccessibleInterval<UnsignedByteType> gray = ImgLibUtils.scale(slice, scale);
        String ascii = opService.image().ascii(Views.iterable(gray));
        setOutput("ascii", ascii);
        saveInputs();
    }

    @Override
    public void setOutput(String _name, Object value) {
        if(!getOutputs().containsKey(_name)) {
            super.addOutput(_name, value.getClass());
        }
        super.setOutput(_name, value);
    }

    private RandomAccessibleInterval<UnsignedByteType> convertGray(RandomAccessibleInterval<ARGBType> img) {
        return Converters.argbChannel( img, 2 );
    }

    public void setInput(RandomAccessibleInterval<ARGBType> img) {
        input = img;
    }
}
