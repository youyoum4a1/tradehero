package com.androidth.general.fragments.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.api.news.NewsItemCompactDTO;
import com.androidth.general.api.news.key.NewsItemListKey;
import com.androidth.general.api.news.key.NewsItemListSecurityKey;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.SecurityIntegerId;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.discovery.newsfeed.NewsfeedDisplayDTO;
import com.androidth.general.fragments.discovery.newsfeed.NewsfeedNewsDisplayDTO;
import com.androidth.general.fragments.discovery.newsfeed.NewsfeedPaginatedAdapter;
import com.androidth.general.fragments.security.AbstractSecurityInfoFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.persistence.news.NewsItemCompactListCacheRx;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;
import com.androidth.general.rx.TimberOnErrorAction1;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Lazy;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SecurityNewsfeedFragment extends AbstractSecurityInfoFragment
{
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PER_PAGE = 30;
    @Inject SecurityCompactCacheRx securityCompactCache;

    @Inject NewsItemCompactListCacheRx newsTitleCache;
    @Inject Lazy<DashboardNavigator> dashboardNavigatorLazy;

    @Bind(R.id.recycler_news_headline_progressbar) ProgressBar progressBar;
    @Bind(R.id.recycler_news_headline) RecyclerView recyclerView;
    @Bind(R.id.default_unavailable_text)
    TextView defaultText;
    private PrettyTime prettyTime;
    private NewsfeedPaginatedAdapter newsfeedPaginatedAdapter;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_news_headline_recycler, container, false);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        newsfeedPaginatedAdapter = new NewsfeedPaginatedAdapter(getActivity());
        newsfeedPaginatedAdapter.setOnItemClickedListener(new TypedRecyclerAdapter.OnItemClickedListener<NewsfeedDisplayDTO>()
        {
            @Override
            public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<NewsfeedDisplayDTO> viewHolder, NewsfeedDisplayDTO object)
            {
                if (object instanceof NewsfeedNewsDisplayDTO && ((NewsfeedNewsDisplayDTO) object).url != null)
                {
                    Bundle args = new Bundle();
                    NewsWebFragment.putUrl(args, ((NewsfeedNewsDisplayDTO) object).url);
                    NewsWebFragment.putNewsId(args, object.id);
                    dashboardNavigatorLazy.get().pushFragment(NewsWebFragment.class, args);
                }
            }
        });
        prettyTime = new PrettyTime();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        HierarchyInjector.inject(getActivity(), this);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(newsfeedPaginatedAdapter);

        onDestroyViewSubscriptions.add(securityCompactCache.getOne(securityId)
                        .subscribeOn(Schedulers.computation())
                        .flatMap(new Func1<Pair<SecurityId, SecurityCompactDTO>, Observable<PaginatedDTO<NewsItemCompactDTO>>>()
                        {
                            @Override public Observable<PaginatedDTO<NewsItemCompactDTO>> call(Pair<SecurityId, SecurityCompactDTO> pair)
                            {
                                securityCompactDTO = pair.second;

                                SecurityIntegerId id = pair.second.getSecurityIntegerId();
                                if(pair.second.UnderlyingSecurityId!=null){
                                    id = new SecurityIntegerId(pair.second.UnderlyingSecurityId);
                                }
                                return newsTitleCache.get(new NewsItemListSecurityKey(
                                        id,
                                        DEFAULT_PAGE, DEFAULT_PER_PAGE))
                                        .map(new PairGetSecond<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>());
                            }
                        })
                        .map(new Func1<PaginatedDTO<NewsItemCompactDTO>, List<NewsItemCompactDTO>>()
                        {
                            @Override public List<NewsItemCompactDTO> call(PaginatedDTO<NewsItemCompactDTO> newsItemCompactDTOPaginatedDTO)
                            {
                                return newsItemCompactDTOPaginatedDTO.getData();
                            }
                        })
                        .map(new Func1<List<NewsItemCompactDTO>, List<NewsfeedDisplayDTO>>()
                        {
                            @Override public List<NewsfeedDisplayDTO> call(List<NewsItemCompactDTO> newsItemCompactDTOs)
                            {
                                List<NewsfeedDisplayDTO> list = new ArrayList<>(newsItemCompactDTOs.size());
                                for (NewsItemCompactDTO newsItemCompactDTO : newsItemCompactDTOs)
                                {
                                    list.add(NewsfeedNewsDisplayDTO.from(newsItemCompactDTO, prettyTime));
                                }
                                return list;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<NewsfeedDisplayDTO>>()
                        {
                            @Override public void call(List<NewsfeedDisplayDTO> newsfeedDisplayDTOs)
                            {
                                progressBar.setVisibility(View.GONE);
                                newsfeedPaginatedAdapter.addAll(newsfeedDisplayDTOs);
                                if(newsfeedDisplayDTOs.isEmpty()){
                                    defaultText.setVisibility(View.VISIBLE);
                                    defaultText.setText("News not available");
                                }
                                else {
                                    defaultText.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, new TimberOnErrorAction1("Failed to fetch security news :" + securityId.toString()))
        );
    }
}
