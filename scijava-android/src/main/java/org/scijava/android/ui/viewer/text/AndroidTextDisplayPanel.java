package org.scijava.android.ui.viewer.text;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import org.scijava.android.R;
import org.scijava.android.ui.viewer.AbstractAndroidDisplayPanel;
import org.scijava.android.ui.viewer.Shareable;
import org.scijava.display.TextDisplay;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.ui.viewer.text.TextDisplayPanel;

/**
 * Android panel to display texts
 *
 * @author Deborah Schmidt
 */
public class AndroidTextDisplayPanel extends AbstractAndroidDisplayPanel<TextView> implements TextDisplayPanel, Shareable {
	private final TextDisplay display;
	private final DisplayWindow window;
	private String displayText;

	public AndroidTextDisplayPanel(TextDisplay display, DisplayWindow window) {
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
	public void contentUpdated() {
		displayText = display.get(display.size()-1);
	}

	@Override
	public void updateView(TextView item) {
		final boolean html = displayText.startsWith("<html>");
		if(html) {
			item.setText(Html.fromHtml(displayText, Html.FROM_HTML_MODE_COMPACT));
		} else {
			item.setText(displayText);
			long lineCount = displayText.chars().filter(ch -> ch == '\n').count()+1;
			item.setMaxLines((int) lineCount);
		}
		redoLayout();
	}

	@Override
	public TextView createView(ViewGroup parent) {
		TextView panel = new TextView(parent.getContext(), null, R.style.Widget_SciJava_Panel_Text);
		panel.setTypeface(Typeface.MONOSPACE);
		panel.setAutoSizeTextTypeUniformWithConfiguration(
				1, 100, 1, TypedValue.COMPLEX_UNIT_PX);
		return panel;
	}

	@Override
	public Class<TextView> getViewType() {
		return TextView.class;
	}

	@Override
	public void share(Activity activity) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, displayText);
		intent.setType("text/plain");
		Intent shareIntent = Intent.createChooser(intent, "Share " + getLabel());
		activity.startActivity(shareIntent);
	}
}
