package org.scijava.android.ui.viewer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.scijava.android.R;

/*
 * Reusable ViewHolder for displaying anything with a label.
 *
 * @author Deborah Schmidt
 */
public class AndroidViewHolder<W extends View> extends RecyclerView.ViewHolder {
	public TextView labelView;
	private final W item;
	public AndroidDataView<?> input;

	public AndroidViewHolder(ViewGroup parent, ViewGroup content, W item) {
		super(parent);
		this.item = item;
		labelView = parent.findViewById(R.id.title);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 0);
		params.weight = 1.0f;
		item.setLayoutParams(params);
		content.addView(item);
		content.refreshDrawableState();
	}

	public W getItem() {
		return item;
	}
}