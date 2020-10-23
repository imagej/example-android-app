package org.scijava.android.ui.viewer.text;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

import org.scijava.android.R;
import org.scijava.android.ui.viewer.AndroidDisplayPanel;
import org.scijava.display.TextDisplay;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.ui.viewer.text.TextDisplayPanel;

public class AndroidTextDisplayPanel implements TextDisplayPanel, AndroidDisplayPanel {
	private final TextDisplay display;
	private final DisplayWindow window;

	private final TextView panel;

	public AndroidTextDisplayPanel(TextDisplay display, DisplayWindow window, Activity activity) {
		panel = new MaterialTextView(activity, null, R.style.Widget_SciJava_Panel_Text);
		panel.setAutoSizeTextTypeUniformWithConfiguration(
				1, 100, 1, TypedValue.COMPLEX_UNIT_PX);
		panel.setTypeface(Typeface.MONOSPACE);
		display.getContext().inject(this);
		this.display = display;
		this.window = window;
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
		panel.refreshDrawableState();
	}

	@Override
	public void setLabel(final String s) {
		// nothing happening here
	}

	@Override
	public void redraw() {
		// The strategy is to compare the lines in the text area against
		// those in the display. We clear the control if we find a mismatch.

//		final StringBuffer targetText = new StringBuffer();
//		for (final Object line : display) {
//			targetText.append(line.toString() + "\n");
//		}
		final String text = display.get(display.size()-1);
		final boolean html = text.startsWith("<html>");
		if(html) {
			panel.setText(Html.fromHtml(text));
		} else {
			panel.setText(text);
			long lineCount = text.chars().filter(ch -> ch == '\n').count()+1;
			panel.setMaxLines((int) lineCount);
		}
		redoLayout();
	}

	@Override
	public View getPanel() {
		return panel;
	}
}
