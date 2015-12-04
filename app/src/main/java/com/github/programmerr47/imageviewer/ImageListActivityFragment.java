package com.github.programmerr47.imageviewer;

import android.animation.Animator;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.programmerr47.imageviewer.representation.adapters.PhotoAdapter;
import com.github.programmerr47.imageviewer.representation.adapters.items.PhotoItem;
import com.github.programmerr47.imageviewer.representation.tasks.AsyncTaskWithListener;
import com.github.programmerr47.imageviewer.representation.tasks.GetImagesTask;
import com.github.programmerr47.imageviewer.util.AnimationUtils;

import java.util.List;

import static com.github.programmerr47.imageviewer.util.AndroidUtils.string;
import static com.github.programmerr47.imageviewer.util.AnimationUtils.getShowViewAnimation;
import static com.github.programmerr47.imageviewer.util.AnimationUtils.hideView;
import static com.github.programmerr47.imageviewer.util.AnimationUtils.showView;
import static com.github.programmerr47.imageviewer.util.AnimationUtils.swapViews;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImageListActivityFragment extends Fragment implements SearchView.OnQueryTextListener, AsyncTaskWithListener.OnTaskFinishedListener {

    private RecyclerView imagesView;
    private TextView emptyView;
    private ProgressBar progressView;

    private PhotoAdapter photoAdapter;

    private MenuItem searchItem;

    private boolean firstTime = true;

    public ImageListActivityFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        photoAdapter = new PhotoAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imagesView = (RecyclerView) view.findViewById(R.id.image_list);
        emptyView = (TextView) view.findViewById(R.id.empty_images_label);
        progressView = (ProgressBar) view.findViewById(R.id.loading_images_progress);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final GridLayoutManager imagesLayoutManager = new GridLayoutManager(getActivity(), 1);
        imagesView.setLayoutManager(imagesLayoutManager);
        imagesView.setAdapter(photoAdapter);

        imagesView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        imagesView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        int viewWidth = imagesView.getMeasuredWidth();
                        float cardViewWidth = getActivity().getResources().getDimension(R.dimen.image_thumb_width);
                        int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                        imagesLayoutManager.setSpanCount(newSpanCount);
                        imagesLayoutManager.requestLayout();
                    }
                });

        if (firstTime) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(string(R.string.init_images));
            progressView.setVisibility(View.GONE);
        } else {
            if (photoAdapter.getItemCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            progressView.setVisibility(View.GONE);
        }
        firstTime = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_image_list, menu);

        searchItem = menu.findItem(R.id.action_search);
        initSearchViewInMenuItem(searchItem);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchItem.collapseActionView();
        searchImages(query);

        if (emptyView.getVisibility() == View.VISIBLE) {
            swapViews(emptyView, progressView);
        } else {
            swapViews(imagesView, progressView);
        }

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onTaskFinished(String taskName, Object extraObject) {
        if (taskName.equals(GetImagesTask.class.getName())) {
            List<PhotoItem> newItems = (List<PhotoItem>) extraObject;
            photoAdapter.updateItems(newItems);

            if (!newItems.isEmpty()) {
                if (progressView.getVisibility() == View.VISIBLE) {
                    hideView(progressView);
                }

                if (emptyView.getVisibility() == View.VISIBLE) {
                    hideView(emptyView);
                }

                showView(imagesView);
            } else {
                emptyView.setText(string(R.string.no_images));
                swapViews(progressView, emptyView);
            }
        }
    }

    private void initSearchViewInMenuItem(MenuItem searchItem) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
    }

    private void searchImages(String query) {
        GetImagesTask imagesTask = new GetImagesTask();
        imagesTask.setOnTaskFinishedListener(this);
        imagesTask.execute(query);
    }
}
