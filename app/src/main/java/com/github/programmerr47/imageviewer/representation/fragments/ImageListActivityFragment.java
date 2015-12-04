package com.github.programmerr47.imageviewer.representation.fragments;

import android.animation.Animator;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.programmerr47.imageviewer.R;
import com.github.programmerr47.imageviewer.representation.adapters.PhotoAdapter;
import com.github.programmerr47.imageviewer.representation.adapters.items.PhotoItem;
import com.github.programmerr47.imageviewer.representation.tasks.AsyncTaskWithListener;
import com.github.programmerr47.imageviewer.representation.tasks.GetImagesTask;
import com.github.programmerr47.imageviewer.util.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_SEARCH;
import static com.github.programmerr47.imageviewer.util.AndroidUtils.color;
import static com.github.programmerr47.imageviewer.util.AndroidUtils.dpToPx;
import static com.github.programmerr47.imageviewer.util.AndroidUtils.isNetworkConnected;
import static com.github.programmerr47.imageviewer.util.AndroidUtils.string;
import static com.github.programmerr47.imageviewer.util.AnimationUtils.hideView;
import static com.github.programmerr47.imageviewer.util.AnimationUtils.showAndHideSuccessNetworkState;
import static com.github.programmerr47.imageviewer.util.AnimationUtils.showNetworkState;
import static com.github.programmerr47.imageviewer.util.AnimationUtils.showView;
import static com.github.programmerr47.imageviewer.util.AnimationUtils.swapViews;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImageListActivityFragment extends Fragment implements SearchView.OnQueryTextListener, AsyncTaskWithListener.OnTaskFinishedListener, SearchView.OnSuggestionListener {

    private RecyclerView imagesView;
    private TextView emptyView;
    private ProgressBar progressView;
    private View networkStateContainer;
    private TextView connectionStateLabel;

    private PhotoAdapter photoAdapter;

    SearchView searchView;
    private MenuItem searchItem;

    private boolean firstTime = true;
    private Boolean isNetworkConnected;

    private Animator networkStateAnimation;

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                boolean isNetworkConnected = isNetworkConnected();
                changeConnectionStateViews(isNetworkConnected);

                if (isNetworkConnected != ImageListActivityFragment.this.isNetworkConnected) {
                    runProperNetworkAnimation(isNetworkConnected);
                    ImageListActivityFragment.this.isNetworkConnected = isNetworkConnected;
                }
            }
        }
    };

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
        networkStateContainer = view.findViewById(R.id.connection_state_container);
        connectionStateLabel = (TextView) view.findViewById(R.id.connection_state_label);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (networkStateAnimation != null && networkStateAnimation.isStarted()) {
            networkStateAnimation.end();
        }

        ViewCompat.setElevation(networkStateContainer, dpToPx(3));

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

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // callback for drag-n-drop, false to skip this feature
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // callback for swipe to dismiss, removing item from data and adapter
                photoAdapter.remove(viewHolder.getAdapterPosition());
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(imagesView);

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
    public void onResume() {
        super.onResume();
        boolean isNetworkConnected = AndroidUtils.isNetworkConnected();
        changeConnectionStateViews(isNetworkConnected);
        if (this.isNetworkConnected == null || this.isNetworkConnected != isNetworkConnected) {
            runProperNetworkAnimation(isNetworkConnected);
        } else if (!isNetworkConnected) {
            networkStateContainer.setY(0);
        } else {
            if (networkStateAnimation == null || !networkStateAnimation.isStarted()) {
                networkStateContainer.setVisibility(View.INVISIBLE);
            }
        }
        this.isNetworkConnected = isNetworkConnected;

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(networkStateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(networkStateReceiver);
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

        if (isNetworkConnected) {
            if (emptyView.getVisibility() == View.VISIBLE) {
                swapViews(emptyView, progressView);
            } else {
                swapViews(imagesView, progressView);
            }

            searchImages(query);
            launchQuerySearch(query);
        } else {
            photoAdapter.updateItems(new ArrayList<PhotoItem>());
            emptyView.setText(string(R.string.null_images));
            if (emptyView.getVisibility() != View.VISIBLE) {
                swapViews(imagesView, emptyView);
            }
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

            if (newItems == null) {
                emptyView.setText(string(R.string.null_images));
                swapViews(progressView, emptyView);
            } else {
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
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        String query = getSuggestionByPosition(position);
        onQueryTextSubmit(query);
        return true;
    }

    private void initSearchViewInMenuItem(MenuItem searchItem) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(this);
            searchView.setOnSuggestionListener(this);
        }
    }

    private void searchImages(String query) {
        GetImagesTask imagesTask = new GetImagesTask();
        imagesTask.setOnTaskFinishedListener(this);
        imagesTask.execute(query);
    }

    private void launchQuerySearch(String query) {
        Intent intent = new Intent(ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(getActivity().getComponentName());
        getContext().startActivity(intent);
    }

    private String getSuggestionByPosition(int position) {
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        return cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
    }

    private void changeConnectionStateViews(boolean isNetworkConnected) {
        if (isNetworkConnected) {
            connectionStateLabel.setText(string(R.string.network_connected));
            networkStateContainer.setBackgroundDrawable(new ColorDrawable(color(R.color.good_state)));
        } else {
            connectionStateLabel.setText(string(R.string.waiting_for_connection));
            networkStateContainer.setBackgroundDrawable(new ColorDrawable(color(R.color.wrong_state)));
        }
    }

    private void runProperNetworkAnimation(boolean isNetworkConnected) {
        if (networkStateAnimation != null && networkStateAnimation.isStarted()) {
            networkStateAnimation.cancel();
        }

        if (isNetworkConnected) {
            networkStateAnimation = showAndHideSuccessNetworkState(networkStateContainer, 0);
        } else {
            networkStateAnimation = showNetworkState(networkStateContainer, 0);
        }
    }
}
