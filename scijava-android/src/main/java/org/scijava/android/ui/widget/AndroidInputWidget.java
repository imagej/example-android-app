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

package org.scijava.android.ui.widget;

import android.view.ViewGroup;

import androidx.appcompat.widget.LinearLayoutCompat;

import org.scijava.android.AndroidService;
import org.scijava.android.R;
import org.scijava.android.ui.AndroidUI;
import org.scijava.plugin.Parameter;
import org.scijava.ui.AbstractUIInputWidget;
import org.scijava.ui.UserInterface;
import org.scijava.widget.WidgetModel;

/**
 * Common superclass for Android-based input widgets.
 * 
 * @author Deborah Schmidt
 */
public abstract class AndroidInputWidget<T> extends
	AbstractUIInputWidget<T, ViewGroup>
{

	@Parameter
	AndroidService androidService;

	private LinearLayoutCompat uiComponent;

	// -- WrapperPlugin methods --

	@Override
	public void set(final WidgetModel model) {
		super.set(model);
//		ViewGroup root = androidService.getActivity().findViewById(R.id.scijava_control);
		uiComponent = new LinearLayoutCompat(androidService.getActivity(), null, R.style.scijava_input_widget);
	}

	// -- UIComponent methods --

	@Override
	public ViewGroup getComponent() {
		return uiComponent;
	}

	@Override
	public Class<ViewGroup> getComponentType() {
		return ViewGroup.class;
	}

	// -- AbstractUIInputWidget methods --

	@Override
	protected UserInterface ui() {
		return ui(AndroidUI.NAME);
	}

}
