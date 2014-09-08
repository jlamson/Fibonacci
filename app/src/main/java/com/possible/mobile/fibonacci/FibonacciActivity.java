package com.possible.mobile.fibonacci;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by Joshua Lamson on 9/5/14.
 */
public class FibonacciActivity extends Activity {

    private static final String BKEY_ADAPTER_STATE = "BKEY_ADAPTER_STATE";

    private FibonacciAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fibonacci);

        initListView(savedInstanceState);
    }

    private void initListView(Bundle savedInstanceState) {
        ListView listView = (ListView) findViewById(R.id.list_view);
        if (savedInstanceState != null && savedInstanceState.containsKey(BKEY_ADAPTER_STATE)) {
            mAdapter = new FibonacciAdapter(this, savedInstanceState.getBundle(BKEY_ADAPTER_STATE));
        } else {
            mAdapter = new FibonacciAdapter(this);
        }
        FibonnaciScrollListener scrollListener = new FibonnaciScrollListener(mAdapter);

        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(scrollListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle(BKEY_ADAPTER_STATE, mAdapter.getInstanceStateBundle());
    }
}
