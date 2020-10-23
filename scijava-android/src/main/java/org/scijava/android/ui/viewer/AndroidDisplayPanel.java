package org.scijava.android.ui.viewer;

import android.view.View;

import org.scijava.ui.viewer.DisplayPanel;

public interface AndroidDisplayPanel<W extends View> extends AndroidDataView<W>, DisplayPanel {
    default boolean isLabeled() {
        return getDisplay().getName() != null;
    }

    default String getLabel() {
        return getDisplay().getName();
    }

    default void update() {
        redraw();
    }
}
