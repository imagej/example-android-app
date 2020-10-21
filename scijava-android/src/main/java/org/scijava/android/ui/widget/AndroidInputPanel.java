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

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import org.scijava.android.R;
import org.scijava.widget.AbstractInputPanel;
import org.scijava.widget.InputPanel;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 * Swing implementation of {@link InputPanel}.
 * 
 * @author Curtis Rueden
 */
public class AndroidInputPanel extends AbstractInputPanel<ViewGroup, ViewGroup> {

	private final Activity activity;
	private ViewGroup uiComponent;


	public AndroidInputPanel(Activity activity) {
		this.activity = activity;
	}

	// -- InputPanel methods --

	@Override
	public void addWidget(final InputWidget<?, ViewGroup> widget) {
		super.addWidget(widget);
		final ViewGroup widgetPane = widget.getComponent();
		final WidgetModel model = widget.get();

		// add widget to panel
		if (widget.isLabeled()) {
			// widget is prefixed by a label
			TextView l = new TextView(activity);
			l.setText(model.getWidgetLabel());
			final String desc = model.getItem().getDescription();
			//TODO figure out what to do with description
			getComponent().addView(l);
		}
		getComponent().addView(widgetPane);
	}

	@Override
	public Class<ViewGroup> getWidgetComponentType() {
		return ViewGroup.class;
	}

	// -- UIComponent methods --

	@Override
	public ViewGroup getComponent() {
		if (uiComponent == null) {
			uiComponent = new LinearLayoutCompat(activity, null, R.style.scijava_input_panel);
		}
		return uiComponent;
	}

	@Override
	public Class<ViewGroup> getComponentType() {
		return ViewGroup.class;
	}

}
