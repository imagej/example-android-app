package org.scijava.android.ui.widget;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.scijava.Context;
import org.scijava.Contextual;
import org.scijava.android.R;
import org.scijava.convert.ConvertService;
import org.scijava.log.LogService;
import org.scijava.module.Module;
import org.scijava.module.ModuleException;
import org.scijava.module.ModuleItem;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.widget.DefaultWidgetModel;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;
import org.scijava.widget.WidgetService;

import java.util.ArrayList;
import java.util.List;

public class ModuleInputsAdapter extends
    RecyclerView.Adapter<ModuleInputsAdapter.ViewHolder> implements Contextual {

    @Parameter
    private WidgetService widgetService;

    @Parameter
    private ConvertService convertService;

    @Parameter
    private ObjectService objectService;

    @Parameter
    private LogService log;

    @Parameter
    private Context context;

    private final List<Pair<WidgetModel, InputWidget<?,?>>> inputs;

    public ModuleInputsAdapter(Context context) {
        setContext(context);
        inputs = new ArrayList<>();
    }

    public void load(Module module) {
        inputs.clear();
        module.getInfo().inputs().forEach(moduleItem -> {
            try {
                WidgetModel model = getWidgetModel(module, moduleItem);
                if(model == null) return;
                InputWidget<?, ?> widget = getWidget(model, moduleItem);
                if(widget == null) return;
                inputs.add(new Pair<>(model, widget));
                model.setInitialized(true);

            } catch (ModuleException e) {
                e.printStackTrace();
            }
        });
    }

    private <T> WidgetModel getWidgetModel(final Module module, final ModuleItem<T> item) throws ModuleException
    {
        final String name = item.getName();
        final boolean resolved = module.isInputResolved(name);
        if (resolved) return null; // skip resolved inputs

        final Class<T> type = item.getType();
        return new DefaultWidgetModel(context, null, module, item, getObjects(type));
    }

    private InputWidget<?, ?> getWidget(WidgetModel model, ModuleItem<?> moduleItem) throws ModuleException {

        final InputWidget<?, ?> widget = widgetService.create(model);
        if (widget == null) {
            log.debug("No widget found for input: " + model.getItem().getName());
            return null;
        }
        if (canDisplay(widget)) {
            return widget;
        }

        if (moduleItem.isRequired()) {
            throw new ModuleException("A " + moduleItem.getType().getSimpleName() +
                    " is required but none exist.");
        }

        // item is not required; we can skip it
        return null;
    }

    private boolean canDisplay(InputWidget<?, ?> widget) {
        return View.class.isAssignableFrom(widget.getComponentType());
    }

    /** Asks the object service and convert service for valid choices */
    private List<?> getObjects(final Class<?> type) {
        ArrayList<Object> compatibleInputs = new ArrayList<>(convertService.getCompatibleInputs(type));
        compatibleInputs.addAll(objectService.getObjects(type));
        return compatibleInputs;
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public Context getContext() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public FrameLayout contentView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.input_name);
            contentView = itemView.findViewById(R.id.input_widget);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        android.content.Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View inputPanelView = inflater.inflate(R.layout.scijava_control_window_panel, parent, false);

        return new ViewHolder(inputPanelView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Pair<WidgetModel, InputWidget<?, ?>> input = inputs.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(input.first.getWidgetLabel());
        FrameLayout content = holder.contentView;
        content.removeAllViews();
        content.addView((View) input.second.getComponent());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return inputs.size();
    }
}