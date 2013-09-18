package com.tradehero.th.widget.trending;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.fragments.trending.TrendingSearchType;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/17/13 Time: 5:46 PM To change this template use File | Settings | File Templates. */
public class TrendingBarListener implements  AdapterView.OnItemSelectedListener, TextWatcher
{
    public static final String TAG = TrendingBarStatusDTO.class.getSimpleName();
    private List<Callback> callbacks = new ArrayList<>();
    private TrendingSearchType mSearchType;
    private String mSearchText;

    //<editor-fold desc="AdapterView.OnItemSelectedListener">
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        THLog.i(TAG, "Spinner i " + i + ", l " + l + ", view " + view + ", type " + TrendingSearchType.fromInt(i));
        mSearchType = TrendingSearchType.fromInt(i);
        notifyCallbacks();
    }

    public void onNothingSelected(AdapterView<?> adapterView)
    {
        THLog.i(TAG, "Spinner Nothing selected");
        mSearchType = null;
        notifyCallbacks();
    }
    //</editor-fold>

    //<editor-fold desc="TextWatcher">
    @Override public void afterTextChanged(Editable editable)
    {
    }

    @Override public void beforeTextChanged(CharSequence charSequence, int start, int count, int after)
    {
    }

    @Override public void onTextChanged(CharSequence charSequence, int start, int before, int count)
    {
        mSearchText = charSequence.toString();
        notifyCallbacks();
    }
    //</editor-fold>

    public TrendingBarStatusDTO getCurrentStatus()
    {
        return new TrendingBarStatusDTO(mSearchType, mSearchText);
    }

    public void addCallback(Callback callback)
    {
        if (callback != null && !callbacks.contains(callback))
        {
            callbacks.add(callback);
        }
    }

    public void removeCallback(Callback callback)
    {
        callbacks.remove(callback);
    }

    public void clearCallbacks()
    {
        callbacks.clear();
    }

    public void notifyCallbacks()
    {
        TrendingBarStatusDTO currentStatus = getCurrentStatus();
        THLog.i(TAG, "Notifying " + callbacks.size() + " callbacks");
        for(Callback callback: callbacks)
        {
            callback.onTrendingBarChanged(currentStatus);
        }
    }

    public interface Callback
    {
        void onTrendingBarChanged(TrendingBarStatusDTO trendingBarStatusDTO);
    }
}
