package com.github.programmerr47.imageviewer.representation.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Michael Spitsin
 * @since 2015-08-06
 */
public class PhotoItemHolder extends RecyclerView.ViewHolder {

    private ImageView mImageContainer;
    private TextView mImageShortDescription;

    public PhotoItemHolder(View itemView, PhotoItemHolderParams params) {
        super(itemView);

        mImageContainer = (ImageView) itemView.findViewById(params.photoId);
        mImageShortDescription = (TextView) itemView.findViewById(params.shortDescrId);
    }

    public ImageView getImageContainer() {
        return mImageContainer;
    }

    public TextView getmImageShortDescription() {
        return mImageShortDescription;
    }

    public static class PhotoItemHolderParams {
        public int photoId;
        public int shortDescrId;
    }
}
