package net.imagej.android.ui.viewer.image;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import net.imagej.android.R;
import org.scijava.android.ui.viewer.AndroidDisplayPanel;
import org.scijava.ui.viewer.DisplayWindow;

public class AndroidRandomAccessibleDisplayPanel implements RandomAccessibleIntervalDisplayPanel, AndroidDisplayPanel {
	private final AndroidRandomAccessibleIntervalDisplay display;
	private final DisplayWindow window;

	private final ImageView content;

	public AndroidRandomAccessibleDisplayPanel(AndroidRandomAccessibleIntervalDisplay display, DisplayWindow window, Activity activity) {
		this.display = display;
		content = new ImageView(activity);
		display.getContext().inject(this);
		this.window = window;
		window.setContent(this);
	}

	// -- DisplayPanel methods --

	@Override
	public RandomAccessibleIntervalDisplay getDisplay() {
		return display;
	}

	@Override
	public DisplayWindow getWindow() {
		return window;
	}

	@Override
	public void redoLayout() {
		// Nothing to layout
	}

	@Override
	public void setLabel(final String s) {
		// nothing happening here
	}

	@Override
	public void redraw() {
		content.setImageBitmap(display.getBitmap());
	}

	@Override
	public View getPanel() {
		return content;
	}

}
