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
public class AndroidViewHolder<W extends View> extends RecyclerView.ViewHolder implements View.OnClickListener {
	public final TextView labelView;
	public final ViewGroup content;
	private final W item;
	private final ViewAdapter adapter;
	public AndroidDataView<?> input;

	public AndroidViewHolder(ViewAdapter adapter, ViewGroup parent, ViewGroup content, W item) {
		super(parent);
		this.item = item;
		this.adapter = adapter;
		this.content = content;
		labelView = parent.findViewById(R.id.title);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		item.setLayoutParams(params);
		content.removeAllViews();
		content.addView(item);
		parent.setOnClickListener(this);
	}

	public W getItem() {
		return item;
	}

	@Override
	public void onClick(View v) {
		int position = getAdapterPosition();
		adapter.onItemClick(position);
	}
}