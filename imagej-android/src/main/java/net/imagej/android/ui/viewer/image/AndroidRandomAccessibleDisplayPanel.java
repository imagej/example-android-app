package net.imagej.android.ui.viewer.image;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.scijava.android.R;
import org.scijava.android.ui.viewer.text.AndroidDisplayPanel;
import org.scijava.ui.viewer.DisplayWindow;

public class AndroidRandomAccessibleDisplayPanel implements RandomAccessibleIntervalDisplayPanel, AndroidDisplayPanel {
	private final AndroidRandomAccessibleIntervalDisplay display;
	private final DisplayWindow window;

	private final ImageView content;

	public AndroidRandomAccessibleDisplayPanel(AndroidRandomAccessibleIntervalDisplay display, DisplayWindow window, Activity activity) {
		this.display = display;
		ViewGroup root = activity.findViewById(R.id.scijava_view);
		content = (ImageView) activity.getLayoutInflater().inflate(net.imagej.android.R.layout.imagej_panel_image, root, false);
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
