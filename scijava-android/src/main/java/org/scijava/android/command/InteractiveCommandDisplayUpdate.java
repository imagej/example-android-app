package org.scijava.android.command;

import org.scijava.Named;
import org.scijava.command.InteractiveCommand;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/*
 * Interactive command being able to update the display of an output by setting the output
 */
public class InteractiveCommandDisplayUpdate extends InteractiveCommand {

    WeakHashMap<String, Display> outputDisplays = new WeakHashMap<>();

    @Parameter
    private UIService uiService;

    private DisplayService ds;

    @Override
    public void preview() {
        super.preview();
        ds = context().service(DisplayService.class);

        for (final ModuleItem<?> outputItem : getInfo().outputs()) {
//            if (isOutputResolved(outputItem.getName())) continue;
            handleOutput(outputItem.getName(), getOutput(outputItem.getName()));
        }
    }

    // -- Helper methods --

    /**
     * Displays output objects.
     *
     * @param defaultName The default name for the display, if not already set.
     * @param output The object to display.
     */
    private boolean handleOutput(final String defaultName, final Object output) {
        if (output == null) return false; // ignore null outputs

        if (output instanceof Display) {
            // output is itself a display; just update it
            final Display<?> display = (Display<?>) output;
            display.update();
            return true;
        }

        final ArrayList<Display<?>> displays = new ArrayList<>();

        if(outputDisplays.containsKey(defaultName)) {
            displays.add(outputDisplays.get(defaultName));
        }

        displays.addAll(ds.getDisplays(output));
        if (displays.size() > 0) {
            outputDisplays.put(defaultName, displays.get(0));
        }
        if (displays.isEmpty()) {
            // output was not already displayed
            final Display<?> activeDisplay = ds.getActiveDisplay();

            if (activeDisplay != null && activeDisplay.canDisplay(output)) {
                // add output to existing display if possible
                activeDisplay.display(output);
                displays.add(activeDisplay);
            }
            else {
                // create a new display for the output
                String name = null;

                // TODO rework how displays are named
                if (output instanceof Named) name = ((Named)output).getName();

                if (name == null) name = defaultName;

                final Display<?> display = ds.createDisplay(name, output);
                if (display != null) {
                    outputDisplays.put(defaultName, display);
                }
            }
        }

        if (!displays.isEmpty()) {
            // output was successfully associated with at least one display
            for (final Display<?> display : displays) {
                display.display(output);
                display.update();
            }
            return true;
        }

        if (output instanceof Map) {
            // handle each item of the map separately
            final Map<?, ?> map = (Map<?, ?>) output;
            for (final Object key : map.keySet()) {
                final String itemName = key.toString();
                final Object itemValue = map.get(key);
                handleOutput(itemName, itemValue);
            }
            return true;
        }

        if (output instanceof Collection) {
            // handle each item of the collection separately
            final Collection<?> collection = (Collection<?>) output;
            for (final Object item : collection) {
                handleOutput(defaultName, item);
            }
            return true;
        }
        return false;
    }

    private boolean addToExisting(final Object output) {
        // TODO - find a general way to decide this
        // Current thinking is that with the pending display refactoring, each
        // display will stop being a list and instead handle a single object.
        // Once that happens, the idea of adding a new output to an existing display
        // will largely disappear, obviating the need for this logic. In the case
        // of images specifically there could be another PostprocessorPlugin that
        // handles that special case in some fashion, though.
        return false;
    }

    private String defaultName(final ModuleItem<?> item) {
        final String label = item.getLabel();
        if (label != null && !label.isEmpty()) return label;
        final String name = item.getName();
        if (name != null && !name.isEmpty()) return name;
        return "Unnamed";
    }

}
