package org.scijava.android.ui.viewer.widget;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.scijava.Context;
import org.scijava.Contextual;
import org.scijava.android.AndroidService;
import org.scijava.android.R;
import org.scijava.android.ui.viewer.AndroidDisplayPanel;
import org.scijava.android.ui.viewer.recyclable.LabeledViewHolder;
import org.scijava.display.Display;
import org.scijava.plugin.Parameter;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.widget.AbstractInputPanel;
import org.scijava.widget.InputWidget;

public class AndroidInputPanel extends AbstractInputPanel<RecyclerView, View> implements AndroidDisplayPanel<RecyclerView>, Contextual {

    @Parameter
    AndroidService androidService;

    @Parameter
    private Context context;

    private ModuleInputsAdapter adapter;
    private Display<?> display;
    private DisplayWindow window;
    private String label;
    private LabeledViewHolder<RecyclerView> holder;

    public AndroidInputPanel(Context context) {
        setContext(context);
        adapter = new ModuleInputsAdapter(getContext(), R.layout.scijava_control_window_panel);
    }

    @Override
    public void addWidget(final InputWidget<?, View> widget) {
        super.addWidget(widget);
        adapter.addItem((AndroidInputWidget<?, ?>)widget);
    }

    @Override
    public Class<View> getWidgetComponentType() {
        return View.class;
    }

    @Override
    public RecyclerView getComponent() {
        return null;
    }

    @Override
    public Class<RecyclerView> getComponentType() {
        return RecyclerView.class;
    }

    @Override
    public Display<?> getDisplay() {
        return display;
    }

    @Override
    public DisplayWindow getWindow() {
        return window;
    }

    @Override
    public void redoLayout() {
        //TODO
    }

    public void setDisplay(Display<?> display) {
        this.display = display;
    }

    public void setWindow(DisplayWindow window) {
        this.window = window;
    }

    @Override
    public RecyclerView createView(ViewGroup parent) {
        RecyclerView panel = (RecyclerView) androidService.getActivity().getLayoutInflater().inflate(R.layout.scijava_control_window_recycle, parent, false);
        panel.setLayoutManager(new LinearLayoutManager(androidService.getActivity()));
        return panel;
    }

    @Override
    public Class<RecyclerView> getViewType() {
        return RecyclerView.class;
    }

    @Override
    public void attach(LabeledViewHolder<RecyclerView> holder) {
        this.holder = holder;
        holder.getLabelView().setText(label);
        holder.getItem().setAdapter(adapter);
    }

    @Override
    public LabeledViewHolder<RecyclerView> getViewHolder() {
        return holder;
    }

    @Override
    public void contentUpdated() {
    }

    @Override
    public void updateView(RecyclerView item) {

    }

    @Override
    public void detach(LabeledViewHolder<RecyclerView> holder) {
        this.holder = null;
        // seems I can't detach the adapter ..
    }

    @Override
    public boolean isLabeled() {
        return label != null;
    }

    @Override
    public void setLabel(String s) {
        label = s;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public Context getContext() {
        return context;
    }
}
