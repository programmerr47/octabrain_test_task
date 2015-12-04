package com.github.programmerr47.imageviewer.representation.adapters;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.github.programmerr47.imageviewer.representation.adapters.holders.PhotoItemHolder;
import com.github.programmerr47.imageviewer.representation.adapters.items.PhotoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Spitsin
 * @since 2015-08-06
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoItemHolder> {

    private List<PhotoItem> mPhotoItems;

    public PhotoAdapter() {
        this(new ArrayList<PhotoItem>());
    }

    public PhotoAdapter(@NonNull List<PhotoItem> photoItems) {
        mPhotoItems = photoItems;
    }

    @Override
    public PhotoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PhotoItem.produce(parent);
    }

    @Override
    public void onBindViewHolder(PhotoItemHolder holder, int position) {
        PhotoItem item = mPhotoItems.get(position);
        item.bindView(holder, position);
    }

    @Override
    public int getItemCount() {
        return mPhotoItems.size();
    }

    public void updateItems(List<PhotoItem> newItems) {
        if (mPhotoItems.isEmpty()) {
            mPhotoItems = newItems;
            notifyItemRangeInserted(0, newItems.size());
        } else {
            mPhotoItems = newItems;
            notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        mPhotoItems.remove(position);
        notifyItemRemoved(position);
    }
}
