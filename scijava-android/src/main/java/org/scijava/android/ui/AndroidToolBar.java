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

package org.scijava.android.ui;

import org.scijava.Context;
import org.scijava.event.EventHandler;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginInfo;
import org.scijava.tool.Tool;
import org.scijava.tool.ToolService;
import org.scijava.tool.event.ToolActivatedEvent;
import org.scijava.tool.event.ToolDeactivatedEvent;
import org.scijava.ui.ToolBar;
import org.scijava.ui.UIService;

/**
 * @author Deborah Schmidt
 */
public class AndroidToolBar implements ToolBar {

	@Parameter
	private ToolService toolService;

	@Parameter
	private UIService uiService;
	
	@Parameter
	private LogService log;

	public AndroidToolBar(final Context context) {
		context.inject(this);
		populateToolBar();
	}

	// -- Helper methods --

	private void populateToolBar() {
		final Tool activeTool = toolService.getActiveTool();
		Tool lastTool = null;
		for (final Tool tool : toolService.getTools()) {
			//TODO
		}
		
	}

	// -- Event handlers --

	@EventHandler
	protected void onEvent(final ToolActivatedEvent event) {
		final PluginInfo<?> info = event.getTool().getInfo();
		if (info == null) return; // no info, no button
		final String name = info.getName();
		if (name == null) return; // no name, no button?
		//TODO
		log.debug("Selected " + name + " button.");
	}

	@EventHandler
	protected void onEvent(final ToolDeactivatedEvent event) {
		final PluginInfo<?> info = event.getTool().getInfo();
		if (info == null) return; // no info, no button
		final String name = info.getName();
		if (name == null) return; // no name, no button?
		//TODO
		log.debug("Deactivated " + name + " button.");
	}

}
