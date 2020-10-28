package org.scijava.android.ui.viewer.recyclable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.RecyclerView;

import org.scijava.Context;
import org.scijava.Contextual;
import org.scijava.android.AndroidService;
import org.scijava.android.R;
import org.scijava.android.ui.viewer.AndroidDataView;
import org.scijava.android.ui.viewer.RecyclableWindow;
import org.scijava.convert.ConvertService;
import org.scijava.display.event.DisplayUpdatedEvent;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.widget.WidgetService;

import java.util.ArrayList;
import java.util.List;

public class RecyclableDataViewAdapter<T extends RecyclableDataView<?>> extends RecyclerView.Adapter<LabeledViewHolder<?>> implements Contextual {

    @Parameter
    private WidgetService widgetService;

    @Parameter
    private ConvertService convertService;

    @Parameter
    private ObjectService objectService;

    @Parameter
    private AndroidService androidService;

    @Parameter
    private LogService log;

    @Parameter
    private Context context;

    private final ObservableList<T> items;

    private final List<Class<? extends View>> viewTypes;
    private final List<LabeledViewHolderBuilder<? extends View>> viewHolderBuilder;
    private final int adapterLayout;

    private ItemClickListener onClickListener;
    private RecyclerView view;

    public RecyclableDataViewAdapter(Context context, int adapterLayout) {
        setContext(context);
        this.adapterLayout = adapterLayout;
        items = new ObservableArrayList<>();
        items.addOnListChangedCallback(new ObservableListCallback());
        viewTypes = new ArrayList<>();
        viewHolderBuilder = new ArrayList<>();
    }

    public void addItem(T item) {
        items.add(item);
    }

    @Override
    public int getItemViewType(int position) {
        Class<? extends View> componentType = items.get(position).getViewType();
        for (int i = 0; i < viewTypes.size(); i++) {
            if (componentType.equals(viewTypes.get(i))) return i;
        }
        viewTypes.add(componentType);

        viewHolderBuilder.add(items.get(position).getViewHolderBuilder(this));
        return viewTypes.size()-1;
    }

    @NonNull
    @Override
    public LabeledViewHolder<? extends View> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(androidService.getActivity());
        ViewGroup parentView = (ViewGroup) inflater.inflate(adapterLayout, parent, false);
        ViewGroup contentView = parentView.findViewById(R.id.content);
        return viewHolderBuilder.get(viewType).apply(parentView, contentView);
    }
    @Override
    public void onBindViewHolder(final LabeledViewHolder holder, final int position) {
        T item = items.get(position);
        holder.setInput(item);
        item.attach(holder);
        TextView textView = holder.getLabelView();
        if (item.isLabeled()) {
            textView.setText(item.getLabel());
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
        item.updateHolder();
    }

    @Override
    public void onViewRecycled(@NonNull LabeledViewHolder<?> holder) {
        super.onViewRecycled(holder);
        RecyclableDataView item = holder.getInput();
        if(item != null) {
            item.detach(holder);
            holder.setInput(null);
        }
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    private void removeItem(T item) {
        int position = items.indexOf(item);
        removeItem(position);
    }

    public void showItem(T item, boolean visible) {
        if(visible) addItem(item);
        else removeItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setOnClickListener(ItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void onItemClick(int position) {
        if(onClickListener != null) onClickListener.onItemClick(items.get(position));
    }

    public void focus(T item) {
        if(view == null) return;
        view.smoothScrollToPosition(items.indexOf(item));
    }

    public void setView(RecyclerView view) {
        this.view = view;
    }

    private class ObservableListCallback extends  ObservableList.OnListChangedCallback<ObservableList<T>>{


        @Override
        public void onChanged(ObservableList<T> sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }

    }

    public interface ItemClickListener {
        void onItemClick(AndroidDataView<? extends View> item);
    }
}
