package org.scijava.android.ui.viewer;

import android.view.View;

public abstract class AbstractAndroidDataView<W extends View> implements AndroidDataView<W> {

    private AndroidViewHolder<W> holder;

    @Override
    public void attach(AndroidViewHolder<W> holder) {
        this.holder = holder;
    }

    @Override
    public void detach(AndroidViewHolder<W> holder) {
        this.holder = null;
    }

    @Override
    public AndroidViewHolder<W> getViewHolder() {
        return holder;
    }
}
