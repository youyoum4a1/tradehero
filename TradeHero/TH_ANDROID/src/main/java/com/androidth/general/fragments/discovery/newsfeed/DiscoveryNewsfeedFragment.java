package com.androidth.general.fragments.discovery.newsfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.common.text.ClickableTagProcessor;
import com.androidth.general.common.text.LinkTagProcessor;
import com.androidth.general.R;
import com.androidth.general.adapters.PagedRecyclerAdapter;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.api.discussion.newsfeed.NewsfeedPagedCache;
import com.androidth.general.api.discussion.newsfeed.NewsfeedPagedDTOKey;
import com.androidth.general.fragments.BasePagedRecyclerRxFragment;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.news.NewsWebFragment;
import com.androidth.general.fragments.web.WebViewFragment;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;

import java.util.Locale;
import javax.inject.Inject;
import rx.functions.Action1;

public class DiscoveryNewsfeedFragment extends BasePagedRecyclerRxFragment<
        NewsfeedPagedDTOKey,
        NewsfeedDisplayDTO,
        NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>,
        NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>
        >
{
    @Inject NewsfeedPagedCache newsfeedPagedCache;
    @Inject DashboardNavigator navigator;
    @Inject Locale locale;

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

    @Override
    public void onResume() {
        super.onResume();
        GAnalyticsProvider.sendGAScreen(getActivity(), GAnalyticsProvider.LOCAL_DISCOVER_NEWSFEED);
    }

    @NonNull @Override protected PagedRecyclerAdapter<NewsfeedDisplayDTO> createItemViewAdapter()
    {
        NewsfeedPaginatedAdapter adapter = new NewsfeedPaginatedAdapter(getActivity());
        adapter.setOnItemClickedListener(new TypedRecyclerAdapter.OnItemClickedListener<NewsfeedDisplayDTO>()
        {
            @Override
            public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<NewsfeedDisplayDTO> viewHolder, NewsfeedDisplayDTO object)
            {
                if (object instanceof NewsfeedNewsDisplayDTO)
                {
                    Bundle args = new Bundle();
                    NewsWebFragment.putNewsId(args, object.id);
                    NewsWebFragment.putUrl(args, ((NewsfeedNewsDisplayDTO) object).url);
                    navigator.pushFragment(NewsWebFragment.class, args);
                }
                else if (object instanceof NewsfeedStockTwitDisplayDTO)
                {
                    if (((NewsfeedStockTwitDisplayDTO) object).link != null)
                    {
                        Bundle args = new Bundle();
                        WebViewFragment.putUrl(args, ((NewsfeedStockTwitDisplayDTO) object).link);
                        navigator.pushFragment(WebViewFragment.class, args);
                    }
                }
            }
        });
        onDestroySubscriptions.add(adapter.getUserActionObservable()
                .subscribe(new Action1<ClickableTagProcessor.UserAction>()
                {
                    @Override public void call(ClickableTagProcessor.UserAction userAction)
                    {
                        if(userAction instanceof LinkTagProcessor.WebUserAction)
                        {
                            Bundle args = new Bundle();
                            WebViewFragment.putUrl(args, ((LinkTagProcessor.WebUserAction) userAction).link);
                            navigator.pushFragment(WebViewFragment.class, args);
                        }
                    }
                }, new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {

                    }
                }));
        return adapter;
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
        return new NewsfeedPagedDTOKey(locale.getCountry(), locale.getLanguage(), page, perPage);
    }
}
