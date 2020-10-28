package org.scijava.android.ui.viewer;

import android.view.View;

import org.scijava.android.ui.viewer.recyclable.RecyclableDataView;
import org.scijava.ui.viewer.DisplayPanel;

/**
 * An Android panel for displaying data
 *
 * @author Deborah Schmidt
 * @param <W> the type of {@link View} housing the panel
 */
public interface AndroidDisplayPanel<W extends View> extends DisplayPanel, RecyclableDataView<W> {

    default void redraw() {
        contentUpdated();
        if(getViewHolder() != null) updateView(getViewHolder().getItem());
    }

}
