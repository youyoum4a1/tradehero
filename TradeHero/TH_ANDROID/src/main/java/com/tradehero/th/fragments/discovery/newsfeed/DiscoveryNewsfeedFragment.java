package com.tradehero.th.fragments.discovery.newsfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.text.ClickableTagProcessor;
import com.tradehero.common.text.LinkTagProcessor;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedRecyclerAdapter;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedPagedCache;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedPagedDTOKey;
import com.tradehero.th.fragments.BasePagedRecyclerRxFragment;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.discovery.DiscoveryMainFragment;
import com.tradehero.th.fragments.news.NewsWebFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
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
                        if (userAction instanceof LinkTagProcessor.WebUserAction)
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
