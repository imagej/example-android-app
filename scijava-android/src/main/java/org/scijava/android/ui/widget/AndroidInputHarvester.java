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

import android.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.scijava.android.AndroidService;
import org.scijava.android.R;
import org.scijava.android.ui.AndroidUI;
import org.scijava.module.Module;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.ui.AbstractInputHarvesterPlugin;
import org.scijava.widget.InputHarvester;
import org.scijava.widget.InputPanel;

/**
 * SwingInputHarvester is an {@link InputHarvester} that collects input
 * parameter values from the user using a {@link AndroidInputPanel} dialog box.
 * 
 * @author Deborah Schmidt
 */
@Plugin(type = PreprocessorPlugin.class, priority = InputHarvester.PRIORITY)
public class AndroidInputHarvester extends
	AbstractInputHarvesterPlugin<ViewGroup, ViewGroup>
{

	@Parameter
	AndroidService androidService;

	@Parameter
	ThreadService threadService;

	// -- InputHarvester methods --

	@Override
	public AndroidInputPanel createInputPanel() {
		return new AndroidInputPanel(androidService.getActivity());
	}

	@Override
	public boolean harvestInputs(final InputPanel<ViewGroup, ViewGroup> inputPanel,
		final Module module)
	{
		final ViewGroup pane = inputPanel.getComponent();

		// display input panel in a dialog
		final String title = module.getInfo().getTitle();
		final boolean modal = !module.getInfo().isInteractive();
		final boolean allowCancel = module.getInfo().canCancel();

		LinearLayout controlView = androidService.getActivity().findViewById(R.id.scijava_control);
		if(controlView == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(androidService.getActivity());
			builder.setTitle(title)
					.setView(pane);

			builder.setNeutralButton("Close", (dialog, which) -> {
				dialog.dismiss();
			});
			threadService.queue(() -> {
				AlertDialog dialog = builder.create();
				dialog.show();
			});
		} else {
			threadService.queue(() -> {
				ViewGroup uiComponent = (ViewGroup) androidService.getActivity().getLayoutInflater().inflate(R.layout.scijava_control_window, controlView, false);
				ViewGroup content = uiComponent.findViewById(R.id.content);
				TextView titleView = uiComponent.findViewById(R.id.title);
				content.addView(pane);
				titleView.setText(title);
				controlView.addView(uiComponent);
			});
		}
		return true;
	}

	// -- Internal methods --

	@Override
	protected String getUI() {
		return AndroidUI.NAME;
	}

}
