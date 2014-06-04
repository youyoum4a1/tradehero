package com.tradehero.th.fragments.position;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import dagger.Lazy;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class PositionListFragment extends AbstractPositionListFragment
{
    @Inject Lazy<GetPositionsCache> getPositionsCache;

    @Override protected void fetchSimplePage()
    {
        fetchSimplePage(false);
    }

    @Override protected void fetchSimplePage(boolean force)
    {
        if (getPositionsDTOKey != null && getPositionsDTOKey.isValid())
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
        fetchGetPositionsDTOTask = createRefreshPositionsCacheFetchTask();
        //displayProgress(true);
        fetchGetPositionsDTOTask.execute();
    }

    @Override protected DTOCache.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsCacheListener()
    {
        return new GetPositionsListener();
    }

    protected DTOCache.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsRefreshCacheListener()
    {
        return new RefreshPositionsListener();
    }

    @Override protected DTOCache.GetOrFetchTask<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsCacheFetchTask(boolean force)
    {
        return getPositionsCache.get().getOrFetch(getPositionsDTOKey, force, createGetPositionsCacheListener());
    }

    protected DTOCache.GetOrFetchTask<GetPositionsDTOKey, GetPositionsDTO> createRefreshPositionsCacheFetchTask()
    {
        return getPositionsCache.get().getOrFetch(getPositionsDTOKey, true, createGetPositionsRefreshCacheListener());
    }

    protected class GetPositionsListener extends AbstractGetPositionsListener<GetPositionsDTOKey, GetPositionsDTO>
    {
        @Override public void onDTOReceived(GetPositionsDTOKey key, GetPositionsDTO value, boolean fromCache)
        {
            if (key.equals(getPositionsDTOKey))
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

    protected class RefreshPositionsListener extends AbstractGetPositionsListener<GetPositionsDTOKey, GetPositionsDTO>
    {
        @Override public void onDTOReceived(GetPositionsDTOKey key, GetPositionsDTO value, boolean fromCache)
        {
            if (!fromCache)
            {
                linkWith(value, true);
                showResultIfNecessary();
            }
        }

        @Override public void onErrorThrown(GetPositionsDTOKey key, Throwable error)
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
