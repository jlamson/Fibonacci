package com.possible.mobile.fibonacci;

import android.widget.AbsListView;

/**
 * Created by Joshua Lamson on 9/5/14.
 */
public class FibonnaciScrollListener implements AbsListView.OnScrollListener {

    private static final int MORE_ROWS_THRESHOLD = 5;

    private FibonacciAdapter mFibonacciAdapter;

    public FibonnaciScrollListener(FibonacciAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("The Provided FibonacciAdapter may not be null");
        }

        mFibonacciAdapter = adapter;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (shouldLoadMore(firstVisibleItem + visibleItemCount, totalItemCount)) {
            mFibonacciAdapter.loadMoreItems();
        }
    }

    private boolean shouldLoadMore(int largestIndexShown, int totalItemCount) {
        return largestIndexShown >= totalItemCount - MORE_ROWS_THRESHOLD;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // do nothing
    }
}
