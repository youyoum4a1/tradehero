package com.ayondo.academy.fragments.education;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tradehero.common.persistence.DTOCacheRx;
import com.ayondo.academy.R;
import com.ayondo.academy.adapters.PagedViewDTOAdapterImpl;
import com.ayondo.academy.api.education.PagedVideoCategories;
import com.ayondo.academy.api.education.PaginatedVideoCategoryDTO;
import com.ayondo.academy.api.education.VideoCategoryDTO;
import com.ayondo.academy.api.education.VideoCategoryDTOList;
import com.ayondo.academy.fragments.BasePagedListRxFragment;
import com.ayondo.academy.persistence.education.PaginatedVideoCategoryCacheRx;

import javax.inject.Inject;

public class VideoCategoriesFragment extends BasePagedListRxFragment<
        PagedVideoCategories,
        VideoCategoryDTO,
        VideoCategoryDTOList,
        PaginatedVideoCategoryDTO>
{
    @Inject PaginatedVideoCategoryCacheRx paginatedVideoCategoryCache;
    //TODO Change Analytics
    //@Inject Analytics analytics;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_video_categories, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        scheduleRequestData();
    }

    @Override public void onResume()
    {
        super.onResume();
        //TODO Change Analytics
        //analytics.fireEvent(new SimpleEvent(AnalyticsConstants.TabBar_Academy));
    }

    @Override @NonNull protected PagedViewDTOAdapterImpl<VideoCategoryDTO, VideoCategoryView> createItemViewAdapter()
    {
        return new PagedViewDTOAdapterImpl<>(getActivity(), R.layout.video_category_item_view);
    }

    @Override @NonNull protected DTOCacheRx<PagedVideoCategories, PaginatedVideoCategoryDTO> getCache()
    {
        return paginatedVideoCategoryCache;
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public PagedVideoCategories makePagedDtoKey(int page)
    {
        return new PagedVideoCategories(page, perPage);
    }
}