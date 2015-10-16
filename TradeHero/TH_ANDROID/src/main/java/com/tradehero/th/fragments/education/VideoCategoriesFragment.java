package com.tradehero.th.fragments.education;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedViewDTOAdapterImpl;
import com.tradehero.th.api.education.PagedVideoCategories;
import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryDTOList;
import com.tradehero.th.fragments.BasePagedListRxFragment;
import com.tradehero.th.fragments.discovery.DiscoveryMainFragment;
import com.tradehero.th.persistence.education.PaginatedVideoCategoryCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.LiveWidgetScrollListener;
import com.tradehero.th.widget.MultiScrollListener;
import javax.inject.Inject;

public class VideoCategoriesFragment extends BasePagedListRxFragment<
        PagedVideoCategories,
        VideoCategoryDTO,
        VideoCategoryDTOList,
        PaginatedVideoCategoryDTO>
{
    @Inject PaginatedVideoCategoryCacheRx paginatedVideoCategoryCache;
    @Inject Analytics analytics;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_video_categories, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        scheduleRequestData();

        if (getParentFragment() instanceof DiscoveryMainFragment)
        {
            listView.setOnScrollListener(new MultiScrollListener(nearEndScrollListener, fragmentElements.get().getListViewScrollListener(),
                    new LiveWidgetScrollListener(fragmentElements.get(), ((DiscoveryMainFragment) getParentFragment()).getLiveFragmentUtil())));
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.fireEvent(new SimpleEvent(AnalyticsConstants.TabBar_Academy));
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