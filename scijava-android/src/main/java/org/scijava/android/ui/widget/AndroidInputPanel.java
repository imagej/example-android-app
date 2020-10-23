package org.scijava.android.ui.widget;

import android.view.View;
import android.view.ViewGroup;

import org.scijava.widget.AbstractInputPanel;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

public class AndroidInputPanel extends AbstractInputPanel<ModuleInputsAdapter, AndroidInputWidget> {

    private ModuleInputsAdapter adapter;

    public AndroidInputPanel(ViewGroup root) {
        this.component = root;
    }

    @Override
    public void addWidget(final InputWidget<?, View> widget) {
        super.addWidget(widget);
        View widgetPane = widget.getComponent();
        final WidgetModel model = widget.get();

        // add widget to panel
        if (widget.isLabeled()) {
            // widget is prefixed by a label
            final JLabel l = new JLabel(model.getWidgetLabel());
            final String desc = model.getItem().getDescription();
            if (desc != null && !desc.isEmpty()) l.setToolTipText(desc);
            getComponent().add(l);
            getComponent().add(widgetPane);
        }
        else {
            // widget occupies entire row
            getComponent().add(widgetPane, "span");
        }
    }

    @Override
    public Class<View> getWidgetComponentType() {
        return View.class;
    }

    @Override
    public ViewGroup getComponent() {
        return component;
    }

    @Override
    public Class<ViewGroup> getComponentType() {
        return ViewGroup.class;
    }
}
