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

package org.scijava.android.ui.viewer.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.scijava.android.AndroidService;
import org.scijava.android.ui.viewer.recyclable.LabeledViewHolder;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.ButtonWidget;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 * An Android widget that displays a button and invokes the callback of a parameter
 * when the button is clicked.
 * 
 * @author Deborah Schmidt
 */
@Plugin(type = InputWidget.class)
public class AndroidButtonWidget extends AndroidInputWidget<org.scijava.widget.Button, Button> implements
	ButtonWidget<View>
{

	@Parameter
	private AndroidService androidService;

	// -- InputWidget methods --

	@Override
	public org.scijava.widget.Button getValue() {
		return null;
	}

	@Override
	public boolean isLabeled() {
		return false;
	}

	// -- WrapperPlugin methods --

	@Override
	public void set(final WidgetModel model) {
		super.set(model);
	}

	// -- Typed methods --

	@Override
	public boolean supports(final WidgetModel model) {
		return model.isType(Button.class);
	}

	// -- AbstractUIInputWidget methods ---

	@Override
	public void doRefresh() {
		if(getViewHolder() != null) {
			getViewHolder().getItem().setText(get().getWidgetLabel());
		}
	}

	@Override
	public android.widget.Button getComponent() {
		return null;
	}

	@Override
	public Button createView(ViewGroup parent) {
		return new android.widget.Button(androidService.getActivity());
	}

	@Override
	public Class getViewType() {
		return Button.class;
	}

	@Override
	public void attach(LabeledViewHolder<Button> holder) {
		super.attach(holder);
		Button button = holder.getItem();
		button.setOnClickListener((view) -> {
			get().callback();
			get().getPanel().refresh();
		});
	}

	@Override
	public void contentUpdated() {
	}

	@Override
	public void updateView(Button item) {
		item.setText(get().getWidgetLabel());
	}

}
