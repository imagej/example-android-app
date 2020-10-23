package org.scijava.android.ui.viewer;

import android.view.ViewGroup;

import java.util.function.BiFunction;

public interface AndroidViewHolderBuilder extends BiFunction<ViewGroup, ViewGroup, AndroidViewHolder<?>> {
}
