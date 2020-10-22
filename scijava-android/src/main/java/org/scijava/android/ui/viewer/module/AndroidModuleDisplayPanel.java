package org.scijava.android.ui.viewer.module;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.scijava.Context;
import org.scijava.android.R;
import org.scijava.android.ui.viewer.AndroidDisplayPanel;
import org.scijava.android.ui.widget.ModuleInputsAdapter;
import org.scijava.display.Display;
import org.scijava.module.Module;
import org.scijava.plugin.Parameter;
import org.scijava.ui.viewer.DisplayWindow;

public class AndroidModuleDisplayPanel implements AndroidDisplayPanel {

	@Parameter
	private Context context;

	private final Display<Module> display;
	private final DisplayWindow window;
	private ModuleInputsAdapter adapter;
	private ViewGroup panel;

	public AndroidModuleDisplayPanel(ModuleDisplay display, DisplayWindow window, Activity activity) {
		display.getContext().inject(this);

		ViewGroup controlView = activity.findViewById(R.id.scijava_control);
		panel = setupPanelAdapter(controlView, activity);

		this.display = display;
		this.window = window;
		window.setContent(this);
		redraw();
	}

	private ViewGroup setupPanelAdapter(ViewGroup root, Activity activity) {
		RecyclerView rvInputs = (RecyclerView) activity.getLayoutInflater().inflate(R.layout.scijava_control_window_recycle, root, false);
		adapter = new ModuleInputsAdapter(context);
		rvInputs.setAdapter(adapter);
		rvInputs.setLayoutManager(new LinearLayoutManager(activity));
		return rvInputs;
	}

	// -- DisplayPanel methods --

	@Override
	public Display<Module> getDisplay() {
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

		Module module = display.get(0);

		if(module == null) return;

		final String title = module.getInfo().getTitle();
		final boolean modal = !module.getInfo().isInteractive();
		final boolean allowCancel = module.getInfo().canCancel();
		adapter.load(module);

		redoLayout();
	}

	@Override
	public View getPanel() {
		return panel;
	}
}
