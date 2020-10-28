package org.scijava.android.ui.viewer;

import android.view.View;

public abstract class AbstractAndroidDisplayPanel<W extends View>  extends AbstractAndroidDataView<W> implements AndroidDisplayPanel<W> {

    @Override
    public void redoLayout() {

    }

    @Override
    public void setLabel(String s) {
        getDisplay().setName(s);
    }

    @Override
    public boolean isLabeled() {
        return getDisplay().getName() != null;
    }

    @Override
    public String getLabel() {
        return getDisplay().getName();
    }
}
