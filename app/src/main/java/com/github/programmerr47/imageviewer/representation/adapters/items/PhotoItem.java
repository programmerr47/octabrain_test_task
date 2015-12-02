package com.github.programmerr47.imageviewer.representation.adapters.items;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.programmerr47.imageviewer.R;
import com.github.programmerr47.imageviewer.representation.adapters.holders.PhotoItemHolder;
import com.github.programmerr47.imageviewer.representation.adapters.holders.PhotoItemHolder.PhotoItemHolderParams;

import static com.github.programmerr47.imageviewer.representation.ImageViewerApplication.getImageLoader;

/**
 * @author Michael Spitsin
 * @since 2015-08-06
 */
public class PhotoItem {

    private Uri mPhotoUri;
    private String mShortDescr;

    public static PhotoItem createInstance(Uri photoUri, String shortDescr) {
        PhotoItem result = new PhotoItem();
        result.mPhotoUri = photoUri;
        result.mShortDescr = shortDescr;
        return result;
    }

    private PhotoItem() {}

    public void bindView(PhotoItemHolder holder, @SuppressWarnings("unused") int position) {
        getImageLoader().displayImage(mPhotoUri.toString(), holder.getImageContainer());
        holder.getmImageShortDescription().setText(mShortDescr);
    }

    public static PhotoItemHolder produce(ViewGroup parentView, int dimension) {
        LayoutInflater layoutInflater = LayoutInflater.from(parentView.getContext());
        View view = layoutInflater.inflate(R.layout.item_photo, parentView, false);

        if (view == null) {
            throw new IllegalStateException("View not created");
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = dimension;
        params.height = dimension;

        PhotoItemHolderParams holderParams = new PhotoItemHolderParams();
        holderParams.photoId = R.id.photo;
        holderParams.shortDescrId = R.id.short_descr;

        return new PhotoItemHolder(view, holderParams);
    }
}
