package com.tradehero.th.adapters.trending;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.market.ExchangeStringId;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorBasicFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorPriceFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorVolumeFragment;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 10:27 AM To change this template use File | Settings | File Templates. */
public class TrendingFilterPagerAdapter extends FragmentStatePagerAdapter
{
    public static final String TAG = TrendingFilterPagerAdapter.class.getSimpleName();
    public static final int FRAGMENT_COUNT = 3;

    private final Context context;
    private WeakReference<TrendingFilterSelectorFragment.OnResumedListener> onResumedListener;
    private WeakReference<OnPositionedExchangeSelectionChangedListener> onPositionedExchangeSelectionChangedListener;

    private TrendingFilterSelectorFragment.OnExchangeSelectionChangedListener onExchangeSelectionChangedListenerBasic;
    private TrendingFilterSelectorFragment.OnExchangeSelectionChangedListener onExchangeSelectionChangedListenerVolume;
    private TrendingFilterSelectorFragment.OnExchangeSelectionChangedListener onExchangeSelectionChangedListenerPrice;

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
        onExchangeSelectionChangedListenerBasic = new TrendingFilterSelectorFragment.OnExchangeSelectionChangedListener()
        {
            @Override public void onExchangeSelectionChanged(ExchangeStringId exchangeId)
            {
                notifyPositionedExchangeSelectionChangedListener(TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER, exchangeId);
            }
        };
        onExchangeSelectionChangedListenerVolume = new TrendingFilterSelectorFragment.OnExchangeSelectionChangedListener()
            {
                @Override public void onExchangeSelectionChanged(ExchangeStringId exchangeId)
                {
                    notifyPositionedExchangeSelectionChangedListener(TrendingFilterSelectorVolumeFragment.POSITION_IN_PAGER, exchangeId);
                }
            };
        onExchangeSelectionChangedListenerPrice = new TrendingFilterSelectorFragment.OnExchangeSelectionChangedListener()
            {
                @Override public void onExchangeSelectionChanged(ExchangeStringId exchangeId)
                {
                    notifyPositionedExchangeSelectionChangedListener(TrendingFilterSelectorPriceFragment.POSITION_IN_PAGER, exchangeId);
                }
            };
    }

    @Override public int getCount()
    {
        return FRAGMENT_COUNT;
    }

    @Override public Fragment getItem(int position)
    {
        TrendingFilterSelectorFragment fragment = null;
        switch(position)
        {
            default:
                THLog.i(TAG, "Not supported index " + position);
            case TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER:
                fragment = new TrendingFilterSelectorBasicFragment();
                fragment.setOnExchangeSelectionChangedListener(onExchangeSelectionChangedListenerBasic);
                break;
            case TrendingFilterSelectorVolumeFragment.POSITION_IN_PAGER:
                fragment = new TrendingFilterSelectorVolumeFragment();
                fragment.setOnExchangeSelectionChangedListener(onExchangeSelectionChangedListenerVolume);
                break;
            case TrendingFilterSelectorPriceFragment.POSITION_IN_PAGER:
                fragment = new TrendingFilterSelectorPriceFragment();
                fragment.setOnExchangeSelectionChangedListener(onExchangeSelectionChangedListenerPrice);
                break;
        }

        fragment.setOnResumedListener(onResumedListener.get());

        // TODO listen to spinner

        fragment.setRetainInstance(false);
        return fragment;
    }

    @Override public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param onResumedListener
     */
    public void setOnResumedListener(TrendingFilterSelectorFragment.OnResumedListener onResumedListener)
    {
        this.onResumedListener = new WeakReference<>(onResumedListener);
    }

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

    public interface OnPositionedExchangeSelectionChangedListener
    {
        void onExchangeSelectionChanged(int fragmentPosition, ExchangeStringId exchangeId);
    }
}
