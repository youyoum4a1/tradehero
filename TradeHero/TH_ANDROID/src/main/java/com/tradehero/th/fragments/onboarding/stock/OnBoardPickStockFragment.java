package com.tradehero.th.fragments.onboarding.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OnBoardPickStockFragment extends BaseFragment
{
    @Inject SecurityCompactListCache securityCompactListCache;
    @NotNull OnBoardPickStockViewHolder viewHolder;
    @Nullable ExchangeSectorSecurityListType exchangeSectorSecurityListType;
    @Nullable DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListCacheListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewHolder = new OnBoardPickStockViewHolder(getActivity());
        securityListCacheListener = createSecurityListCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.onboard_select_stock, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewHolder.attachView(view);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchExchangeSectorSecurities();
    }

    @Override public void onStop()
    {
        detachSecurityListCache();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        viewHolder.detachView();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.securityListCacheListener = null;
        super.onDestroy();
    }

    public void setExchangeSectorSecurityListType(
            @Nullable ExchangeSectorSecurityListType exchangeSectorSecurityListType)
    {
        this.exchangeSectorSecurityListType = exchangeSectorSecurityListType;
        fetchExchangeSectorSecurities();
    }

    protected void fetchExchangeSectorSecurities()
    {
        if (exchangeSectorSecurityListType != null)
        {
            detachSecurityListCache();
            securityCompactListCache.register(exchangeSectorSecurityListType, securityListCacheListener);
            securityCompactListCache.getOrFetchAsync(exchangeSectorSecurityListType);
        }
    }

    protected void detachSecurityListCache()
    {
        securityCompactListCache.unregister(securityListCacheListener);
    }

    protected DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> createSecurityListCacheListener()
    {
        return new OnBoardPickStockCacheListener();
    }

    protected class OnBoardPickStockCacheListener implements DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList>
    {
        @Override public void onDTOReceived(@NotNull SecurityListType key, @NotNull SecurityCompactDTOList value)
        {
            viewHolder.setStocks(value);
        }

        @Override public void onErrorThrown(@NotNull SecurityListType key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_security_list_info);
        }
    }

    public SecurityCompactDTOList getSelectedStocks()
    {
        return viewHolder.getSelectedStocks();
    }
}
