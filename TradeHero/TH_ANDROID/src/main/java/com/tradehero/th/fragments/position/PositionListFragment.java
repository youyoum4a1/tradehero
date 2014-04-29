package com.tradehero.th.fragments.position;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by xavier on 2/3/14.
 */
public class PositionListFragment extends AbstractPositionListFragment<OwnedPortfolioId, PositionDTO, GetPositionsDTO>
{
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

    @Override protected void fetchSimplePage()
    {
        fetchSimplePage(false);
    }

    @Override protected void fetchSimplePage(boolean force)
    {
        if (shownOwnedPortfolioId != null && shownOwnedPortfolioId.isValid())
        {
            detachGetPositionsTask();
            fetchGetPositionsDTOTask = createGetPositionsCacheFetchTask(force);
            //displayProgress(true);
            fetchGetPositionsDTOTask.execute();
        }
    }

    @Override protected void refreshSimplePage()
    {
        detachGetPositionsTask();
        DTOCache.GetOrFetchTask<OwnedPortfolioId, GetPositionsDTO> fetchGetPositionsDTOTask = createRefreshPositionsCacheFetchTask();
        //displayProgress(true);
        fetchGetPositionsDTOTask.execute();
    }

    @Override protected DTOCache.Listener<OwnedPortfolioId, GetPositionsDTO> createGetPositionsCacheListener()
    {
        return new GetPositionsListener();
    }

    @Override protected DTOCache.GetOrFetchTask<OwnedPortfolioId, GetPositionsDTO> createGetPositionsCacheFetchTask(boolean force)
    {
        return getPositionsCache.get().getOrFetch(shownOwnedPortfolioId, force, getPositionsCacheListener);
    }

    protected DTOCache.GetOrFetchTask<OwnedPortfolioId, GetPositionsDTO> createRefreshPositionsCacheFetchTask()
    {
        return getPositionsCache.get().getOrFetch(shownOwnedPortfolioId, true, new RefreshPositionsListener());
    }

    protected class GetPositionsListener extends AbstractGetPositionsListener<OwnedPortfolioId, PositionDTO, GetPositionsDTO>
    {
        @Override public void onDTOReceived(OwnedPortfolioId key, GetPositionsDTO value, boolean fromCache)
        {
            if (key.equals(shownOwnedPortfolioId))
            {
                //displayProgress(false);
                linkWith(value, true);
                showResultIfNecessary();
            }
            else
            {
                showErrorView();
            }
        }
    }

    protected class RefreshPositionsListener extends AbstractGetPositionsListener<OwnedPortfolioId, PositionDTO, GetPositionsDTO>
    {
        @Override public void onDTOReceived(OwnedPortfolioId key, GetPositionsDTO value, boolean fromCache)
        {
            if (!fromCache)
            {
                linkWith(value, true);
                showResultIfNecessary();
            }
        }

        @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
        {
            //super.onErrorThrown(key, error);
            boolean loaded = checkLoadingSuccess();
            if (!loaded)
            {
                showErrorView();
            }
        }
    }
}
