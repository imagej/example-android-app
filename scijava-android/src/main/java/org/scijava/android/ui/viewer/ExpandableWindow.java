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

package org.scijava.android.ui.viewer;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;

import org.scijava.android.R;
import org.scijava.android.ui.viewer.recyclable.RecyclableDataViewAdapter;
import org.scijava.display.Display;

/**
 * A {@link RecyclableWindow} which can also be expanded by clicking on it.
 *
 * @author Deborah Schmidt
 */
public class ExpandableWindow<T, W extends View> extends RecyclableWindow<T, W> implements RecyclableDataViewAdapter.ItemClickListener {
	private final Activity activity;
	private AndroidDataView<? extends View> activeItem;

	public ExpandableWindow(Display<T> display, RecyclableDataViewAdapter<AndroidDisplayPanel<W>> adapter, Activity activity) {
		super(display, adapter);
		adapter.setOnClickListener(this);
		this.activity = activity;
	}

	@Override
	public void onItemClick(AndroidDataView<? extends View> item) {

		ViewGroup content = createDialogContent();

		itemToView(item, content);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(item.getLabel());
		builder.setView(content);
		if(item instanceof Shareable) {
			builder.setPositiveButton("Share", this::onShareClick);
//			builder.setPositiveButtonIcon(ContextCompat.getDrawable(activity, android.R.drawable.ic_menu_share));
		}
		builder.setNeutralButton("Close", this::onCloseClick);
//		builder.setNeutralButtonIcon(ContextCompat.getDrawable(activity, android.R.drawable.ic_menu_close_clear_cancel));

		this.activeItem = item;
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	private void onCloseClick(DialogInterface dialogInterface, int i) {
		dialogInterface.cancel();
	}

	private void onShareClick(DialogInterface dialogInterface, int i) {
		((Shareable) activeItem).share(activity);
	}

	private ViewGroup createDialogContent() {
		ViewGroup parent = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.scijava_view_popup, null);
		Rect displayRectangle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		parent.setMinimumWidth((int)(displayRectangle.width() * 0.8f));
		parent.setMinimumHeight((int)(displayRectangle.height() * 0.8f));
		return parent;
	}

	private <V extends View> void itemToView(AndroidDataView<V> item, ViewGroup parent) {
		ViewGroup content = parent.findViewById(R.id.content);
		V view = item.createView(content);
		item.updateView(view);
		content.addView(view);
	}
}
