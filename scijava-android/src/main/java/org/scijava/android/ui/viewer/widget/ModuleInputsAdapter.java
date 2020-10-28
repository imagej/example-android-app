package org.scijava.android.ui.viewer.widget;

import org.scijava.Context;
import org.scijava.android.ui.viewer.recyclable.RecyclableDataViewAdapter;

public class ModuleInputsAdapter extends RecyclableDataViewAdapter<AndroidInputWidget<?, ?>> {
    public ModuleInputsAdapter(Context context, int adapterLayout) {
        super(context, adapterLayout);
    }
}
