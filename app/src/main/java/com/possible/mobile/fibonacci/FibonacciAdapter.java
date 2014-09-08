package com.possible.mobile.fibonacci;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Joshua Lamson on 9/5/14.
 */
public class FibonacciAdapter extends BaseAdapter {

    private static final String TAG = FibonacciAdapter.class.getSimpleName();
    private static final String BKEY_INITIAL_COUNT = "BKEY_INITIAL_COUNT";
    private static final String BKEY_MIN_POSITION = "BKEY_MIN_POSITION";
    private static final String BKEY_MIN_VALUE = "BKEY_MIN_VALUE";
    private static final String BKEY_NEXT_VALUE = "BKEY_NEXT_VALUE";

    private static final int INITIAL_COUNT = 50;
    private static final int INCREMENT_AMOUNT = 25;
    private static final int DATA_COUNT = 25;

    private final String mRowFormat;

    private LayoutInflater mLayoutInflater;

    private SparseArray<BigInteger> mFibonacciNumbers;
    private int mMinPosition = 0;
    private int mMaxPosition = 0;

    private int mItemCount = INITIAL_COUNT;
    private NumberFormat mSmallNumberFormat;
    private NumberFormat mScientificFormat;

    public FibonacciAdapter(Context context) {
        this(context, null);
    }

    public FibonacciAdapter(Context context, Bundle savedInstanceState) {
        mLayoutInflater = LayoutInflater.from(context);
        mFibonacciNumbers = new SparseArray<BigInteger>();
        mRowFormat = context.getString(R.string.list_item_format);

        mSmallNumberFormat = DecimalFormat.getIntegerInstance(Locale.US);
        mSmallNumberFormat.setMaximumIntegerDigits(12);
        mScientificFormat = new DecimalFormat("0.######E0");

        if (savedInstanceState != null) {
            initFromBundle(savedInstanceState);
        }
    }

    private void initFromBundle(Bundle savedInstanceState) {
        mItemCount = savedInstanceState.getInt(BKEY_INITIAL_COUNT);

        mMinPosition = savedInstanceState.getInt(BKEY_MIN_POSITION);
        mMaxPosition = mMinPosition + DATA_COUNT;

        BigInteger minValue = new BigInteger(savedInstanceState.getString(BKEY_MIN_VALUE));
        mFibonacciNumbers.put(mMinPosition, minValue);
        BigInteger nextValue = new BigInteger(savedInstanceState.getString(BKEY_NEXT_VALUE));
        mFibonacciNumbers.put(mMinPosition + 1, nextValue);

        for (int i = mMinPosition + 2; i <= mMaxPosition; i++) {
            mFibonacciNumbers.put(i,
                    mFibonacciNumbers.get(i - 2).add(mFibonacciNumbers.get(i -1)));
        }
    }

    public void loadMoreItems() {
        mItemCount += INCREMENT_AMOUNT;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItemCount;
    }

    @Override
    public BigInteger getItem(int position) {
        BigInteger result;
        if (position < 0) {
            return BigInteger.ZERO;
        } else if (position == 0) {
            result = BigInteger.ONE;
        } else {
            result = mFibonacciNumbers.get(position);
            if (result == null) {
                if (position < mMinPosition) {
                    // Scroll towards top. If data has been trimmed n-1 and n-2 terms may not be
                    // calculated, so use n+1 and n+2
                    result = getItem(position + 2).subtract(getItem(position + 1));
                } else {
                    result = getItem(position - 1).add(getItem(position - 2));
                }
            }
        }

        if (mFibonacciNumbers.indexOfKey(position) < 0) {
            Log.d(TAG, String.format("add (%d -> %s)", position, result));
            mFibonacciNumbers.put(position, result);

            trimData(position);
        }

        return result;
    }

    /**
     * Trim the size of our solution array to avoid large memory usage based on the most recently
     * instantiated position
     * @param position the most recent position getItem() has been called for.
     */
    private void trimData(int position) {
        if (position > mMaxPosition) {
            // User is scrolling down. Remove stored values for positions below the new min position.
            mMaxPosition = position;
            final int newMinPosition = Math.max(0, mMaxPosition - DATA_COUNT);
            for (int i = mMinPosition; i < newMinPosition; i++) {
                mFibonacciNumbers.delete(i);
            }
            mMinPosition = newMinPosition;
        } else if (position < mMinPosition) {
            // User is scrolling up. Remove stored values for positions above the new max position.
            mMinPosition = position;
            final int newMaxPosition = mMinPosition + DATA_COUNT;
            for (int i = mMaxPosition; i > newMaxPosition; i--) {
                mFibonacciNumbers.delete(i);
            }
            mMaxPosition = newMaxPosition;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = (TextView) mLayoutInflater.inflate(R.layout.list_item, parent, false);
        } else {
            textView = (TextView) convertView;
        }

        String rowText = String.format(mRowFormat, position, getResultString(position));

        StyleSpan span = new StyleSpan(Typeface.ITALIC);
        SpannableString spannableString = new SpannableString(rowText);
        spannableString.setSpan(span, 0, rowText.indexOf("="), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);

        return textView;
    }

    private CharSequence getResultString(int position) {
        final BigInteger result = getItem(position);
        final String resultString = result.toString();

        if (resultString.length() > 12) {
            return mScientificFormat.format(result);
        } else {
            return mSmallNumberFormat.format(result);
        }
    }

    public Bundle getInstanceStateBundle() {
        Bundle bundle = new Bundle(4);
        bundle.putInt(BKEY_INITIAL_COUNT, mItemCount);
        bundle.putInt(BKEY_MIN_POSITION, mMinPosition);
        bundle.putString(BKEY_MIN_VALUE, mFibonacciNumbers.get(mMinPosition).toString());
        bundle.putString(BKEY_NEXT_VALUE, mFibonacciNumbers.get(mMinPosition + 1).toString());
        return bundle;
    }

}
