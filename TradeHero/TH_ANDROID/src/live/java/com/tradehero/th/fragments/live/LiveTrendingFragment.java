package com.tradehero.th.fragments.live;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingLiveSecurityListType;
import com.tradehero.th.fragments.security.SecurityPagedViewDTOAdapter;
import com.tradehero.th.fragments.trade.LiveBuySellFragment;
import com.tradehero.th.fragments.trending.TrendingBaseFragment;
import com.tradehero.th.models.parcelable.LiveBuySellParcelable;
import com.tradehero.th.network.service.DummyAyondoLiveServiceWrapper;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class LiveTrendingFragment extends TrendingBaseFragment
{
    @Inject DummyAyondoLiveServiceWrapper liveServiceWrapper;

    private int nextPageToRequest = FIRST_PAGE;

    public LiveTrendingFragment()
    {
    }

    @Override public void onStart()
    {
        super.onStart();
        requestDtos();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @NonNull @Override protected SecurityPagedViewDTOAdapter createItemViewAdapter()
    {
        return new SecurityPagedViewDTOAdapter(getActivity(), R.layout.trending_security_item);
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public SecurityListType makePagedDtoKey(int page)
    {
        return new TrendingLiveSecurityListType(page, DEFAULT_PER_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_stock_trending, container, false);
    }

    @Override protected void requestDtos()
    {
        if (!isLast(nextPageToRequest))
        {
            if (nextPageToRequest == FIRST_PAGE)
            {
                if (nearEndScrollListener != null)
                {
                    nearEndScrollListener.lowerEndFlag();
                    nearEndScrollListener.activateEnd();
                }
                itemViewAdapter.clear();
                updateVisibilities();
            }

            fetchLiveSecurity(makePagedDtoKey(nextPageToRequest));
            nextPageToRequest++;
        }
    }

    private void fetchLiveSecurity(final SecurityListType key)
    {
        liveServiceWrapper.getLiveCFDSecuritiesRx(key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SecurityCompactDTOList>()
                {
                    @Override public void call(SecurityCompactDTOList securityCompactDTOs)
                    {
                        itemViewAdapter.addPage(key.page, securityCompactDTOs.getList());
                        updateVisibilities();

                        nearEndScrollListener.lowerEndFlag();
                        if (securityCompactDTOs.size() == 0)
                        {
                            nearEndScrollListener.deactivateEnd();
                        }
                    }
                }, new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        Timber.e(throwable.toString());
                    }
                });
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
        if (item instanceof SecurityCompactDTO)
        {
            SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) item;

            LiveBuySellParcelable liveBuySellParcelable =
                    new LiveBuySellParcelable(securityCompactDTO.getSecurityId(), 0);
            getActivity().getIntent().putExtra("LiveBuySellParcelable", liveBuySellParcelable);

            navigator.get().pushFragment(LiveBuySellFragment.class);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled item " + item);
        }
    }
}
