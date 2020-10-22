/*
 * #%L
 * SciJava UI components for Java Swing.
 * %%
 * Copyright (C) 2010 - 2020 SciJava developers.
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

import org.scijava.android.ui.viewer.AndroidDisplayPanel;
import org.scijava.display.Display;
import org.scijava.ui.viewer.DisplayPanel;
import org.scijava.ui.viewer.DisplayWindow;

/**
 * Android class implementation of the {@link DisplayWindow} interface.
 *
 * @author Deborah Schmidt
 */
public class AndroidDisplayWindow implements DisplayWindow {

	private final Display<?> display;
	private final DisplayWindowsAdapter adapter;
	private String title;
	private AndroidDisplayPanel panel;

	public AndroidDisplayWindow(Display<?> display, DisplayWindowsAdapter adapter) {
		this.display = display;
		this.adapter = adapter;
	}

	// -- DisplayWindow methods --

	@Override
	public void setTitle(String s) {
		title = s;
	}

	@Override
	public void setContent(final DisplayPanel panel) {
		this.panel = (AndroidDisplayPanel)panel;
	}

	@Override
	public void pack() {
	}

	@Override
	public void showDisplay(final boolean visible) {
		adapter.showPanel(panel, visible);
	}

	@Override
	public void requestFocus() {
		// TODO
	}

	@Override
	public void close() {
		adapter.showPanel(panel, false);
	}

	@Override
	public int findDisplayContentScreenX() {
		// TODO
		return 0;
	}

	@Override
	public int findDisplayContentScreenY() {
		// TODO
		return 0;
	}
}
