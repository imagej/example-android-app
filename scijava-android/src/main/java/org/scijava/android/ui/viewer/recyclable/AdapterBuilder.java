package org.scijava.android.ui.viewer.recyclable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.scijava.Context;
import org.scijava.android.AndroidService;
import org.scijava.android.R;
import org.scijava.android.ui.viewer.AndroidDisplayPanel;

/*
 * Helper class to create a row of scrollable windows which can be deleted by swiping them up
 */
public class AdapterBuilder {

    public static RecyclableDataViewAdapter<AndroidDisplayPanel<?>> build(AndroidService androidService, Context context, int parentId, int adapterLayoutId) {
        ViewGroup view = androidService.getActivity().findViewById(parentId);
        RecyclerView rv = view.findViewById(R.id.recyclerview);
        RecyclableDataViewAdapter<AndroidDisplayPanel<?>> adapter = new RecyclableDataViewAdapter<>(context, adapterLayoutId);
        initSwitcher(view, rv, adapter);
        initSwipeDelete(rv, adapter);
        adapter.setView(rv);
        rv.setAdapter(adapter);
        return adapter;
    }

    private static void initSwitcher(ViewGroup view, RecyclerView rv, RecyclableDataViewAdapter<AndroidDisplayPanel<?>> adapter) {
        ViewSwitcher switcher = view.findViewById(R.id.switcher);
        if(switcher != null) {
            View emptyView = switcher.getCurrentView();
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    if(adapter.getItemCount() == 1 && switcher.getCurrentView() == emptyView) switcher.showNext();
                    rv.scrollToPosition(adapter.getItemCount() - 1);
                    view.refreshDrawableState();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    if(adapter.getItemCount() == 0 && switcher.getCurrentView() != emptyView) switcher.showNext();
                    view.refreshDrawableState();
                }
            });
        }
    }

    private static void initSwipeDelete(RecyclerView rv, RecyclableDataViewAdapter<AndroidDisplayPanel<?>> adapter) {
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(rv);
    }

    static class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private final RecyclableDataViewAdapter<AndroidDisplayPanel<?>> adapter;

        public SwipeToDeleteCallback(RecyclableDataViewAdapter<AndroidDisplayPanel<?>> adapter) {
            super(0, ItemTouchHelper.UP);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.removeItem(viewHolder.getAdapterPosition());
        }

    }
}
