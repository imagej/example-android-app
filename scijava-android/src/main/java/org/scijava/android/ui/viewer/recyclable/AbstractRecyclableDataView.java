package org.scijava.android.ui.viewer.recyclable;

import android.view.View;

public abstract class AbstractRecyclableDataView<W extends View> implements RecyclableDataView<W> {

    private LabeledViewHolder<W> holder;

    @Override
    public void attach(LabeledViewHolder<W> holder) {
        this.holder = holder;
    }

    @Override
    public void detach(LabeledViewHolder<W> holder) {
        this.holder = null;
    }

    @Override
    public LabeledViewHolder<W> getViewHolder() {
        return holder;
    }
}
