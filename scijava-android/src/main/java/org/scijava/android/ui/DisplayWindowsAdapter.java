package org.scijava.android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.RecyclerView;

import org.scijava.Context;
import org.scijava.Contextual;
import org.scijava.android.R;
import org.scijava.android.ui.viewer.AndroidDisplayPanel;
import org.scijava.display.Display;
import org.scijava.display.event.DisplayDeletedEvent;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;

public class DisplayWindowsAdapter extends
    RecyclerView.Adapter<DisplayWindowsAdapter.ViewHolder> implements Contextual {

    @Parameter
    private LogService log;

    @Parameter
    private Context context;

    @Parameter
    private UIService uiService;

    @Parameter
    private EventService eventService;

    ObservableList<AndroidDisplayPanel> displayPanels;
    private final int adapterLayout;

    public DisplayWindowsAdapter(Context context, int adapterLayout) {
        setContext(context);
        this.adapterLayout = adapterLayout;
        displayPanels = new ObservableArrayList<>();
        displayPanels.addOnListChangedCallback(new ObservableListCallback());
        eventService.subscribe(this);
    }

    @EventHandler
    protected void onEvent(final DisplayDeletedEvent event) {
        removePanel(event.getObject());
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        android.content.Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View windowView = inflater.inflate(adapterLayout, parent, false);

        return new ViewHolder(windowView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        AndroidDisplayPanel input = displayPanels.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(input.getDisplay().getName());
        FrameLayout content = holder.contentView;
        content.removeAllViews();
        content.addView(input.getPanel());
    }

    @Override
    public int getItemCount() {
        return displayPanels.size();
    }

    public void showPanel(AndroidDisplayPanel panel, boolean visible) {
        if(visible) addPanel(panel);
        else removePanel(panel);
    }

    private void addPanel(AndroidDisplayPanel panel) {
        for (AndroidDisplayPanel p : displayPanels) {
            if(p.equals(panel)) {
                // already visible
                return;
            }
        }
        displayPanels.add(panel);
    }

    private void removePanel(AndroidDisplayPanel panel) {
        for (AndroidDisplayPanel p : displayPanels) {
            if(panel.equals(p)) {
                displayPanels.remove(panel);
                return;
            }
        }
    }

    private void removePanel(Display display) {
        for (AndroidDisplayPanel panel : displayPanels) {
            if(display.equals(panel.getDisplay())) {
                displayPanels.remove(panel);
                return;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public FrameLayout contentView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.title);
            contentView = itemView.findViewById(R.id.content);
        }
    }

    private class ObservableListCallback extends  ObservableList.OnListChangedCallback<ObservableList<AndroidDisplayPanel>>{


        @Override
        public void onChanged(ObservableList<AndroidDisplayPanel> sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<AndroidDisplayPanel> sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<AndroidDisplayPanel> sender, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<AndroidDisplayPanel> sender, int fromPosition, int toPosition, int itemCount) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(ObservableList<AndroidDisplayPanel> sender, int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }
}