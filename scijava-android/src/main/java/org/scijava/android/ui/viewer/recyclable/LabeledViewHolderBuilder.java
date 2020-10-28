package org.scijava.android.ui.viewer.recyclable;

import android.view.ViewGroup;

import java.util.function.BiFunction;

public interface LabeledViewHolderBuilder<T> extends BiFunction<ViewGroup, ViewGroup, LabeledViewHolder<?>> {
}
