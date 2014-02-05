package com.tradehero.th.fragments.position;

import android.app.Activity;
import android.os.Handler;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.persistence.position.GetPositionsCache;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by xavier on 2/3/14.
 */
public class PositionListFragment extends AbstractPositionListFragment<OwnedPortfolioId, PositionDTO, GetPositionsDTO>
{
    public static final String TAG = PositionListFragment.class.getSimpleName();

    @Inject Lazy<GetPositionsCache> getPositionsCache;

    @Override protected void createPositionItemAdapter()
    {
        if (positionItemAdapter != null)
        {
            positionItemAdapter.setCellListener(null);
        }
        positionItemAdapter = new PositionItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                R.layout.position_item_header,
                R.layout.position_locked_item,
                R.layout.position_open_no_period,
                R.layout.position_closed_no_period,
                R.layout.position_quick_nothing);
        positionItemAdapter.setCellListener(this);
    }

    protected void fetchSimplePage()
    {
        if (ownedPortfolioId != null && ownedPortfolioId.isValid())
        {
            if (getPositionsCacheListener == null)
            {
                getPositionsCacheListener = createCacheListener();
            }
            if (fetchGetPositionsDTOTask != null)
            {
                fetchGetPositionsDTOTask.setListener(null);
            }
            fetchGetPositionsDTOTask = createCacheFetchTask();
            displayProgress(true);
            fetchGetPositionsDTOTask.execute();
        }
    }

    @Override protected DTOCache.Listener<OwnedPortfolioId, GetPositionsDTO> createCacheListener()
    {
        return new GetPositionsListener();
    }

    @Override protected DTOCache.GetOrFetchTask<OwnedPortfolioId, GetPositionsDTO> createCacheFetchTask()
    {
        return getPositionsCache.get().getOrFetch(ownedPortfolioId, getPositionsCacheListener);
    }

    protected class GetPositionsListener extends AbstractGetPositionsListener<OwnedPortfolioId, PositionDTO, GetPositionsDTO>
    {
        @Override public void onDTOReceived(OwnedPortfolioId key, GetPositionsDTO value, boolean fromCache)
        {
            if (key.equals(ownedPortfolioId))
            {
                displayProgress(false);
                linkWith(value, true);
            }
        }
    }

    @Override protected void createUserInteractor()
    {
        userInteractor = new PositionListTHIABUserInteractor(getActivity(), getBillingActor(), getView().getHandler());
    }

    public class PositionListTHIABUserInteractor extends AbstractPositionListTHIABUserInteractor
    {
        public PositionListTHIABUserInteractor(Activity activity, THIABActor billingActor, Handler handler)
        {
            super(activity, billingActor, handler);
        }
    }
}
