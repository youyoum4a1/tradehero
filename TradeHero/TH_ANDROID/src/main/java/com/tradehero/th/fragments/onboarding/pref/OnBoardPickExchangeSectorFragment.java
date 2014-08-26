package com.tradehero.th.fragments.onboarding.pref;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.models.market.ExchangeSectorCompactKey;
import com.tradehero.th.persistence.market.ExchangeSectorCompactListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OnBoardPickExchangeSectorFragment extends BaseFragment
{
    @Inject ExchangeSectorCompactListCache exchangeSectorCompactListCache;
    @NotNull OnBoardPickExchangeSectorViewHolder viewHolder;
    @Nullable DTOCacheNew.Listener<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO> exchangeSectorListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        exchangeSectorListener = createExchangeSectorListener();
        viewHolder = new OnBoardPickExchangeSectorViewHolder(getActivity());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.onboard_select_exchange_sector, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewHolder.attachView(view);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchExchangeSectors();
    }

    @Override public void onStop()
    {
        detachExchangeSectorCompactListCache();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        viewHolder.detachView();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        exchangeSectorListener = null;
        super.onDestroy();
    }

    protected void fetchExchangeSectors()
    {
        detachExchangeSectorCompactListCache();
        ExchangeSectorCompactKey key = new ExchangeSectorCompactKey();
        exchangeSectorCompactListCache.register(key, exchangeSectorListener);
        exchangeSectorCompactListCache.getOrFetchAsync(key);
    }

    protected void detachExchangeSectorCompactListCache()
    {
        exchangeSectorCompactListCache.unregister(exchangeSectorListener);
    }

    protected DTOCacheNew.Listener<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>
        createExchangeSectorListener()
    {
        return new OnBoardPickExchangeSectorListener();
    }

    protected class OnBoardPickExchangeSectorListener
            implements DTOCacheNew.Listener<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>
    {
        @Override public void onDTOReceived(@NotNull ExchangeSectorCompactKey key, @NotNull ExchangeSectorCompactListDTO value)
        {
            viewHolder.setExchangeSector(value);
        }

        @Override public void onErrorThrown(@NotNull ExchangeSectorCompactKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.market_on_board_error_fetch_exchange_sector);
        }
    }

    public OnBoardPrefDTO getOnBoardPrefs()
    {
        return viewHolder.getOnBoardPrefs();
    }
}
