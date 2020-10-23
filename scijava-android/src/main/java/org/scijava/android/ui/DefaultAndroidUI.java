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
import org.scijava.android.AndroidService;
import org.scijava.android.R;
import org.scijava.android.ui.viewer.module.ModuleDisplay;
import org.scijava.app.AppService;
import org.scijava.display.Display;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.menu.MenuService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.ui.AbstractUserInterface;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.SystemClipboard;
import org.scijava.ui.UIService;
import org.scijava.ui.UserInterface;
import org.scijava.ui.viewer.DisplayWindow;

/**
 * Implementation for Android-based user interfaces.
 * 
 * @author Deborah Schmidt
 */
@Plugin(type = UserInterface.class, name = AndroidUI.NAME)
public class DefaultAndroidUI extends AbstractUserInterface implements
	AndroidUI {

	@Parameter
	private AppService appService;

	@Parameter
	private EventService eventService;

	@Parameter
	private MenuService menuService;

	@Parameter
	private UIService uiService;

	@Parameter
	private ThreadService threadService;

	@Parameter
	private LogService log;

	@Parameter
	private AndroidService androidService;

	@Parameter
	private Context context;

	private AndroidApplicationFrame appFrame;
	private AndroidToolBar toolBar;
	private AndroidStatusBar statusBar;
	private AndroidConsolePane consolePane;
	private AndroidClipboard systemClipboard;

	private DisplayWindowsAdapter viewAdapter;
	private DisplayWindowsAdapter controlAdapter;

	// -- UserInterface methods --

	@Override
	public AndroidApplicationFrame getApplicationFrame() {
		return appFrame;
	}

	@Override
	public AndroidToolBar getToolBar() {
		return toolBar;
	}

	@Override
	public AndroidStatusBar getStatusBar() {
		return statusBar;
	}

	@Override
	public AndroidConsolePane getConsolePane() {
		return consolePane;
	}

	@Override
	public SystemClipboard getSystemClipboard() {
		return systemClipboard;
	}

	@Override
	public DisplayWindow createDisplayWindow(Display<?> display) {
		if(display instanceof ModuleDisplay) {
			return new AndroidDisplayWindow(display, controlAdapter);
		} else {
			return new AndroidDisplayWindow(display, viewAdapter);
		}
	}

	@Override
	public DialogPrompt dialogPrompt(String message, String title, DialogPrompt.MessageType messageType, DialogPrompt.OptionType optionType) {
		// TODO
		return null;
	}

	@Override
	public void showContextMenu(String menuRoot, Display<?> display, int x, int y) {
		// TODO
	}

	@Override
	public boolean requiresEDT() {
		return true;
	}

	@EventHandler
	private void initAdapters(final org.scijava.ui.event.UIShownEvent e) {
		threadService.queue(() -> {
			viewAdapter = WindowRecycleViewBuilder.build(androidService, getContext(), R.id.scijava_view, R.layout.scijava_view_window);
			controlAdapter = WindowRecycleViewBuilder.build(androidService, getContext(), R.id.scijava_control, R.layout.scijava_control_window);
		});
	}

}
