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
import org.scijava.app.AppService;
import org.scijava.app.StatusService;
import org.scijava.app.event.StatusEvent;
import org.scijava.event.EventHandler;
import org.scijava.options.OptionsService;
import org.scijava.plugin.Parameter;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.StatusBar;
import org.scijava.ui.UIService;

/**
 * Android implementation of {@link StatusBar}.
 * 
 * @author Deborah Schmidt
 */
public class AndroidStatusBar implements StatusBar {

	@Parameter
	private OptionsService optionsService;

	@Parameter
	private StatusService statusService;

	@Parameter
	private AppService appService;

	@Parameter
	private UIService uiService;

	public AndroidStatusBar(final Context context) {
		context.inject(this);
		//TODO
	}

	@Override
	public void setStatus(final String message) {
		if (message == null) return; // no change
		final String text;
		if (message.isEmpty()) text = " ";
		else text = message;
		//TODO
	}

	@Override
	public void setProgress(final int val, final int max) {
		//TODO
	}

	// -- Event handlers --

	@EventHandler
	protected void onEvent(final StatusEvent event) {
		if (event.isWarning()) {
			// report warning messages to the user in a dialog box
			final String message = event.getStatusMessage();
			if (message != null && !message.isEmpty()) {
				uiService.showDialog(message, MessageType.WARNING_MESSAGE);
			}
		}
		else {
			// report status updates in the status bar
			final int val = event.getProgressValue();
			final int max = event.getProgressMaximum();
			final String message = uiService.getStatusMessage(event);
			setStatus(message);
			setProgress(val, max);
		}
	}

}
