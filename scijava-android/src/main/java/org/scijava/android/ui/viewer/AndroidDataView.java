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

package org.scijava.android.ui.viewer;

import android.view.View;
import android.view.ViewGroup;

/**
 * Common interface for Android-based data widgets.
 *
 * @author Deborah Schmidt
 */
public interface AndroidDataView<W extends View>
{

	W createView(ViewGroup parent);
	Class<W> getWidgetType();

	void attach(AndroidViewHolder<W> holder);
	void detach(AndroidViewHolder<W> holder);

	default AndroidViewHolderBuilder getViewHolderBuilder(ViewAdapter adapter) {
		return (parent, content) -> new AndroidViewHolder<>(adapter, parent, content, createView(content));
	}
	AndroidViewHolder<W> getViewHolder();

	default void redraw() {
		updateContent();
		if(getViewHolder() != null) updateView(getViewHolder().getItem());
	}
	default void updateHolder() {
		if(getViewHolder() == null) return;
		W item = getViewHolder().getItem();
		updateView(item);
	}
	void updateContent();
	void updateView(W item);

	boolean isLabeled();
	String getLabel();
}
