package org.scijava.android.ui.viewer;

import android.view.View;

import org.scijava.android.ui.viewer.recyclable.AbstractRecyclableDataView;

public abstract class AbstractAndroidDisplayPanel<W extends View>  extends AbstractRecyclableDataView<W> implements AndroidDisplayPanel<W> {

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
