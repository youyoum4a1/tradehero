package com.androidth.general.fragments.education;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.R;
import com.androidth.general.adapters.PagedViewDTOAdapterImpl;
import com.androidth.general.api.education.PagedVideoCategories;
import com.androidth.general.api.education.PaginatedVideoCategoryDTO;
import com.androidth.general.api.education.VideoCategoryDTO;
import com.androidth.general.api.education.VideoCategoryDTOList;
import com.androidth.general.fragments.BasePagedListRxFragment;
import com.androidth.general.persistence.education.PaginatedVideoCategoryCacheRx;

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