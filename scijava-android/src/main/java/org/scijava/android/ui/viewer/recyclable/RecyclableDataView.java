/*
 * #%L
 * SciJava UI components for Java Swing.
 * %%
 * Copyright (C) 2010 - 2017 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.android.ui.viewer.recyclable;

import android.view.View;

import org.scijava.android.ui.viewer.AndroidDataView;

/**
 * Common interface for Android-based data views.
 *
 * @author Deborah Schmidt
 */
public interface RecyclableDataView<W extends View> extends AndroidDataView<W>
{

	/**
	 * Attach this view to an existing holder (e.g. attach listeners)
	 */
	void attach(LabeledViewHolder<W> holder);

	/**
	 * Detach this view from an existing holder (e.g. detach listeners)
	 */
	void detach(LabeledViewHolder<W> holder);

	/**
	 * Provide method for building a holder matching this data view.
	 */
	default LabeledViewHolderBuilder<W> getViewHolderBuilder(RecyclableDataViewAdapter adapter) {
		return (parent, content) -> new LabeledViewHolder<>(adapter, parent, content, createView(content));
	}

	/**
	 * @return the holder currently attached to this view.
	 */
	LabeledViewHolder<W> getViewHolder();

	/**
	 * Update the currently attached holder based on the data of this view.
	 */
	default void updateHolder() {
		if(getViewHolder() == null) return;
		W item = getViewHolder().getItem();
		updateView(item);
	}
}
