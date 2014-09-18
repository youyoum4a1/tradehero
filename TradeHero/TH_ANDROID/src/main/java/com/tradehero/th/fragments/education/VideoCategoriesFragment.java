package com.tradehero.th.fragments.education;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import com.special.residemenu.ResideMenu;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.education.PagedVideoCategories;
import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryDTOList;
import com.tradehero.th.fragments.BasePagedListFragment;
import com.tradehero.th.persistence.education.PaginatedVideoCategoryCache;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class VideoCategoriesFragment extends BasePagedListFragment<
        PagedVideoCategories, // But it also needs to be a PagedDTOKey
        VideoCategoryDTO,
        VideoCategoryDTOList,
        PaginatedVideoCategoryDTO,
        VideoCategoryView
        >
{
    @Inject PaginatedVideoCategoryCache paginatedVideoCategoryCache;
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject Analytics analytics;

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.dashboard_education);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        scheduleRequestData();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        resideMenuLazy.get().addIgnoredView(listView);
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.fireEvent(new SimpleEvent(AnalyticsConstants.TabBar_Academy));
    }

    @Override protected int getFragmentLayoutResId()
    {
        return R.layout.fragment_video_categories;
    }

    @Override protected VideoCategoriesAdapter createItemViewAdapter()
    {
        return new VideoCategoriesAdapter(getActivity(), R.layout.video_category_item_view);
    }

    @Override protected void unregisterCache(DTOCacheNew.Listener<PagedVideoCategories, PaginatedVideoCategoryDTO> listener)
    {
        paginatedVideoCategoryCache.unregister(listener);
    }

    @Override protected void registerCache(PagedVideoCategories key, DTOCacheNew.Listener<PagedVideoCategories, PaginatedVideoCategoryDTO> listener)
    {
        paginatedVideoCategoryCache.register(key, listener);
    }

    @Override protected void requestCache(PagedVideoCategories key)
    {
        paginatedVideoCategoryCache.getOrFetchAsync(key);
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NotNull @Override public PagedVideoCategories makePagedDtoKey(int page)
    {
        return new PagedVideoCategories(page, perPage);
    }
}