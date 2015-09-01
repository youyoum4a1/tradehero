package com.tradehero.th.fragments.discovery.newsfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedRecyclerAdapter;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedPagedCache;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedPagedDTOKey;
import com.tradehero.th.fragments.BasePagedRecyclerRxFragment;
import javax.inject.Inject;

public class DiscoveryNewsfeedFragment extends BasePagedRecyclerRxFragment<
        NewsfeedPagedDTOKey,
        NewsfeedDisplayDTO,
        NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>,
        NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>
        >
{
    @Inject NewsfeedPagedCache newsfeedPagedCache;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override public void onStart()
    {
        super.onStart();
        scheduleRequestData();
    }

    @NonNull @Override protected PagedRecyclerAdapter<NewsfeedDisplayDTO> createItemViewAdapter()
    {
        return new NewsfeedPaginatedAdapter();
    }

    @NonNull @Override protected DTOCacheRx<NewsfeedPagedDTOKey, NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>> getCache()
    {
        return new NewsfeedDisplayDTOPaginatedCache(newsfeedPagedCache);
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public NewsfeedPagedDTOKey makePagedDtoKey(int page)
    {
        return new NewsfeedPagedDTOKey(page, perPage);
    }
}
