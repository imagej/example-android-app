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

package org.scijava.android.ui.viewer.widget;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.scijava.android.ui.AndroidUI;
import org.scijava.android.ui.viewer.module.DefaultModuleDisplay;
import org.scijava.display.Display;
import org.scijava.module.Module;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.ui.AbstractInputHarvesterPlugin;
import org.scijava.ui.UIService;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.widget.InputHarvester;
import org.scijava.widget.InputPanel;


@Plugin(type = PreprocessorPlugin.class, priority = InputHarvester.PRIORITY)
public class AndroidInputHarvester extends AbstractInputHarvesterPlugin<RecyclerView, View> {
	@Parameter
	private UIService uiService;

	@Parameter
	private ThreadService threadService;

	@Override
	public InputPanel<RecyclerView, View> createInputPanel() {
		return new AndroidInputPanel(getContext());
	}

	@Override
	public boolean harvestInputs(final InputPanel<RecyclerView, View> inputPanel,
								 final Module module)
	{
		// FIXME with the current implementation there is no synchronous input harvesting
		//  input parameters will be displayed, but will only have an effect for InteractiveCommands
		threadService.queue(() -> {
			AndroidInputPanel panel = (AndroidInputPanel) inputPanel;
			panel.setLabel(getLabel(module));
			Display<Module> display = new DefaultModuleDisplay(panel);
			DisplayWindow window = uiService.getDefaultUI().createDisplayWindow(display);
			panel.setDisplay(display);
			panel.setWindow(window);
			window.setContent(panel);
			window.showDisplay(true);
		});
		return true;
	}

	private String getLabel(Module module) {
		return module.getInfo().getTitle();
	}

	// -- Internal methods --

	@Override
	protected String getUI() {
		return AndroidUI.NAME;
	}
}
