package org.scijava.android.ui.viewer;

import android.view.View;

import org.scijava.ui.viewer.DisplayPanel;

public interface AndroidDisplayPanel<W extends View> extends DisplayPanel, AndroidDataView<W> {

    default void redraw() {
        AndroidDataView.super.redraw();
    }

}
