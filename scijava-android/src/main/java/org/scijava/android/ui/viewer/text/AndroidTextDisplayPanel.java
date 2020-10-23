package org.scijava.android.ui.viewer.text;


import android.app.Activity;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.scijava.android.R;
import org.scijava.android.ui.viewer.AndroidDisplayPanel;
import org.scijava.android.ui.viewer.AndroidViewHolder;
import org.scijava.display.TextDisplay;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.ui.viewer.text.TextDisplayPanel;

public class AndroidTextDisplayPanel implements TextDisplayPanel, AndroidDisplayPanel<TextView> {
	private final TextDisplay display;
	private final DisplayWindow window;
	private final Activity activity;
	private AndroidViewHolder<TextView> holder;

	public AndroidTextDisplayPanel(TextDisplay display, DisplayWindow window, Activity activity) {
		display.getContext().inject(this);
		this.display = display;
		this.window = window;
		this.activity = activity;
		window.setContent(this);
	}

	@Override
	public void append(String text) {
		display.clear();
		display.add(text);
	}

	@Override
	public void clear() {
		display.clear();
	}

	// -- DisplayPanel methods --

	@Override
	public TextDisplay getDisplay() {
		return display;
	}

	@Override
	public DisplayWindow getWindow() {
		return window;
	}

	@Override
	public void redoLayout() {
		if(holder == null) return;
		holder.getItem().refreshDrawableState();
	}

	@Override
	public void setLabel(final String s) {
		// nothing happening here
	}

	@Override
	public void redraw() {
		if(holder == null) return;
		final String text = display.get(display.size()-1);
		final boolean html = text.startsWith("<html>");
		if(html) {
			holder.getItem().setText(Html.fromHtml(text));
		} else {
			holder.getItem().setText(text);
			long lineCount = text.chars().filter(ch -> ch == '\n').count()+1;
			holder.getItem().setMaxLines((int) lineCount);
		}
		redoLayout();
	}

	@Override
	public TextView createView(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		TextView panel = (TextView) inflater.inflate(R.layout.viewer_text, parent, false);
		panel.setAutoSizeTextTypeUniformWithConfiguration(
				1, 100, 1, TypedValue.COMPLEX_UNIT_PX);
		return panel;
	}

	@Override
	public Class<TextView> getWidgetType() {
		return TextView.class;
	}

	@Override
	public void attach(AndroidViewHolder<TextView> holder) {
		this.holder = holder;
	}

	@Override
	public void detach(AndroidViewHolder<TextView> holder) {
		this.holder = null;
	}

}
