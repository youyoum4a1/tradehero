package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.tradehero.th.api.market.ExchangeStringId;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 10:27 AM To change this template use File | Settings | File Templates. */
public class TrendingFilterPagerAdapter extends FragmentStatePagerAdapter
{
    public static final String TAG = TrendingFilterPagerAdapter.class.getSimpleName();

    private final Context context;
    private WeakReference<TrendingFilterSelectorFragment.OnPreviousNextListener> parentOnPreviousNextListener = new WeakReference<>(null);
    private WeakReference<TrendingFilterSelectorFragment.OnResumedListener> onResumedListener = new WeakReference<>(null);
    private WeakReference<OnPositionedExchangeSelectionChangedListener> onPositionedExchangeSelectionChangedListener = new WeakReference<>(null);

    private Map<Integer, TrendingFilterSelectorFragment.OnExchangeSelectionChangedListener> onExchangeSelectionChangedListeners;

    //<editor-fold desc="Constructors">
    public TrendingFilterPagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);
        this.context = context;
        init();
    }
    //</editor-fold>

    private void init()
    {
        onExchangeSelectionChangedListeners = new HashMap<>();
        for (int position : TrendingFilterSelectorUtil.getPositions())
        {
            onExchangeSelectionChangedListeners.put(position, new TrendingAdapterExchangeSelectionChangedListener(position));
        }
    }

    @Override public int getCount()
    {
        return TrendingFilterSelectorUtil.FRAGMENT_COUNT;
    }

    @Override public Fragment getItem(int position)
    {
        TrendingFilterSelectorFragment fragment = TrendingFilterSelectorUtil.createNewFragment(position);
        fragment.setOnExchangeSelectionChangedListener(onExchangeSelectionChangedListeners.get(position));
        fragment.setOnPreviousNextListener(parentOnPreviousNextListener.get());
        fragment.setOnResumedListener(onResumedListener.get());

        // TODO listen to spinner

        fragment.setRetainInstance(false);
        return fragment;
    }

    @Override public int getItemPosition(Object object)
    {
        if (object instanceof TrendingFilterSelectorFragment)
        {
            return TrendingFilterSelectorUtil.getFragmentPosition((TrendingFilterSelectorFragment) object);
        }
        return POSITION_NONE;
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param parentOnPreviousNextListener
     */
    public void setOnPreviousNextListener(TrendingFilterSelectorFragment.OnPreviousNextListener parentOnPreviousNextListener)
    {
        this.parentOnPreviousNextListener = new WeakReference<>(parentOnPreviousNextListener);
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param onResumedListener
     */
    public void setOnResumedListener(TrendingFilterSelectorFragment.OnResumedListener onResumedListener)
    {
        this.onResumedListener = new WeakReference<>(onResumedListener);
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param onPositionedExchangeSelectionChangedListener
     */
    public void setOnPositionedExchangeSelectionChangedListener(OnPositionedExchangeSelectionChangedListener onPositionedExchangeSelectionChangedListener)
    {
        this.onPositionedExchangeSelectionChangedListener = new WeakReference<>(onPositionedExchangeSelectionChangedListener);
    }

    private void notifyPositionedExchangeSelectionChangedListener(int fragmentPosition, ExchangeStringId exchangeId)
    {
        OnPositionedExchangeSelectionChangedListener listener = onPositionedExchangeSelectionChangedListener.get();
        if (listener != null)
        {
            listener.onExchangeSelectionChanged(fragmentPosition, exchangeId);
        }
    }

    private class TrendingAdapterExchangeSelectionChangedListener implements TrendingFilterSelectorFragment.OnExchangeSelectionChangedListener
    {
        final private int position;

        public TrendingAdapterExchangeSelectionChangedListener(final int position)
        {
            this.position = position;
        }

        @Override public void onExchangeSelectionChanged(final ExchangeStringId exchangeId)
        {
            notifyPositionedExchangeSelectionChangedListener(this.position, exchangeId);
        }
    }

    public interface OnPositionedExchangeSelectionChangedListener
    {
        void onExchangeSelectionChanged(int fragmentPosition, ExchangeStringId exchangeId);
    }
}
