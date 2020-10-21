/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2020 SciJava developers.
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
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.scijava.AbstractContextual;
import org.scijava.android.AndroidService;
import org.scijava.android.R;
import org.scijava.module.Module;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.widget.InputHarvester;


@Plugin(type = PreprocessorPlugin.class, priority = InputHarvester.PRIORITY)
public class AndroidInputHarvester extends AbstractContextual implements PreprocessorPlugin {

	@Parameter
	private AndroidService androidService;

	@Parameter
	private ThreadService threadService;

	@Override
	public void process(Module module) {

		// display input panel in a dialog
		final String title = module.getInfo().getTitle();
		final boolean modal = !module.getInfo().isInteractive();
		final boolean allowCancel = module.getInfo().canCancel();

		ViewGroup controlView = androidService.getActivity().findViewById(R.id.scijava_control);
		if(controlView == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(androidService.getActivity());
			ViewGroup view = setupAdapter(module, null);
			builder.setTitle(title)
					.setView(view.findViewById(R.id.scijava_control_panel_rw));

			builder.setNeutralButton("Close", (dialog, which) -> {
				dialog.dismiss();
			});
			threadService.queue(() -> {
				AlertDialog dialog = builder.create();
				dialog.show();
			});
		} else {
			threadService.queue(() -> {
				ViewGroup uiComponent = setupAdapter(module, controlView);
				TextView titleView = uiComponent.findViewById(R.id.title);
				titleView.setText(title);
				controlView.addView(uiComponent);
			});
		}

	}

	private ViewGroup setupAdapter(Module module, ViewGroup root) {
		ViewGroup controlWindow = (ViewGroup) androidService.getActivity().getLayoutInflater().inflate(R.layout.scijava_control_window, root, false);
		RecyclerView rvInputs = controlWindow.findViewById(R.id.scijava_control_panel_rw);
		ModuleInputsAdapter adapter = new ModuleInputsAdapter(context(), module);
		rvInputs.setAdapter(adapter);
		rvInputs.setLayoutManager(new LinearLayoutManager(androidService.getActivity()));
		return controlWindow;
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void cancel(String reason) {

	}

	@Override
	public String getCancelReason() {
		return null;
	}
}
