package com.tradehero.th.fragments.chinabuild.fragment.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.adapters.SecurityTimeLineDiscussOrNewsAdapter;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheNew;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th2.R;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class SecurityDiscussOrNewsFragment extends DashboardFragment implements DiscussionListCacheNew.DiscussionKeyListListener
{
    public final static String BUNDLE_KEY_SECURITY_NAME = SecurityDiscussOrNewsFragment.class.getName() + ".securityName";
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = SecurityDiscussOrNewsFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_DISCUSS_OR_NEWS_TYPE = SecurityDiscussOrNewsFragment.class.getName() + ".discussOrNewsType";
    public final static String BUNDLE_KEY_SECURIYT_COMPACT_ID = SecurityDiscussOrNewsFragment.class.getName() + ".securityCompactDTOId";
    private Bundle securityIdBundle;
    private String securityName;
    private SecurityId securityId;
    private int securityDTOId;

    @Inject DiscussionCache discussionCache;
    @Inject DiscussionListCacheNew discussionListCache;
    private PaginatedDiscussionListKey discussionListKey;
    private NewsItemListKey listKey;

    public int typeDiscussOrNews;

    public static final int TYPE_DISCUSS = 0;
    public static final int TYPE_NEWS = 1;

    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;

    @Inject NewsItemCompactListCacheNew newsTitleCache;
    @Nullable private DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> newsCacheListener;

    private SecurityTimeLineDiscussOrNewsAdapter adapter;
    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            securityName = args.getString(BUNDLE_KEY_SECURITY_NAME);
            securityId = new SecurityId(securityIdBundle);
            securityDTOId = args.getInt(BUNDLE_KEY_SECURIYT_COMPACT_ID);
            typeDiscussOrNews = args.getInt(BUNDLE_KEY_DISCUSS_OR_NEWS_TYPE);
            Timber.d("SecurityID = " + securityId.toString());
            discussionListKey = new PaginatedDiscussionListKey(DiscussionType.SECURITY, securityDTOId, 1, 20);
            listKey = new NewsItemListSecurityKey(new SecurityIntegerId(securityDTOId), 1, 20);
        }
        newsCacheListener = createNewsCacheListener();
        adapter = new SecurityTimeLineDiscussOrNewsAdapter(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(securityName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.security_discuss_or_news, container, false);
        ButterKnife.inject(this, view);
        initView();

        if (adapter.getCount() == 0)
        {
            startLoadding();
            refreshData(false);
        }
        return view;
    }

    public void initView()
    {
        listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        listTimeLine.setAdapter(adapter);

        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater()
        {
            @Override public void OnTimeLineItemClicked(int position)
            {
                Timber.d("Item position = " + position);
            }

            @Override public void OnTimeLinePraiseClicked(int position)
            {
                Timber.d("Praise position = " + position);
            }

            @Override public void OnTimeLineCommentsClicked(int position)
            {
                AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                DiscussionKey discussionKey = dto.getDiscussionKey();
                Timber.d("Comments position = " + position);
                Bundle bundle = new Bundle();
                bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                        discussionKey.getArgs());
                pushFragment(DiscussSendFragment.class, bundle);
            }

            @Override public void OnTimeLineShareClied(int position)
            {
                Timber.d("Share position = " + position);
            }
        });

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");

                refreshData(true);
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("上拉加载更多");

                refreshDataMore(false);
            }
        });
    }

    public void refreshData(boolean force)
    {
        if (typeDiscussOrNews == TYPE_DISCUSS)
        {
            discussionListKey.page = 1;
            fetchSecurityDiscuss(force);
        }
        else if (typeDiscussOrNews == TYPE_NEWS)
        {
            listKey.page = 1;
            fetchSecurityNews(force);
        }
    }

    public void refreshDataMore(boolean force)
    {
        if (typeDiscussOrNews == TYPE_DISCUSS)
        {
            fetchSecurityDiscuss(force);
        }
        else if (typeDiscussOrNews == TYPE_NEWS)
        {
            fetchSecurityNews(force);
        }
    }

    public void startLoadding()
    {
        //alertDialogUtilLazy.get().dismissProgressDialog();
        if (getActivity() != null)
        {
            alertDialogUtilLazy.get().showProgressDialog(getActivity(), "加载中");
        }
    }

    public void endLoading()
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        listTimeLine.onRefreshComplete();
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachSecurityDiscuss();
        detachSecurityNews();

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @NotNull protected DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> createNewsCacheListener()
    {
        return new NewsHeadlineNewsListListener();
    }

    protected class NewsHeadlineNewsListListener implements DTOCacheNew.HurriedListener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull NewsItemListKey key,
                @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(key, value);
            finish();
        }

        @Override public void onDTOReceived(
                @NotNull NewsItemListKey key,
                @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(key, value);
            finish();
        }

        @Override public void onErrorThrown(
                @NotNull NewsItemListKey key,
                @NotNull Throwable error)
        {
            //THToast.show("");
            finish();
        }

        public void finish()
        {
            endLoading();
        }
    }

    public void linkWith(@NotNull NewsItemListKey key,
            @NotNull PaginatedDTO<NewsItemCompactDTO> value)
    {
        List<NewsItemCompactDTO> listData = value.getData();
        List<AbstractDiscussionCompactDTO> list = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++)
        {
            NewsItemCompactDTO dto = listData.get(i);
            list.add(dto);
        }

        if (key.page == 1)
        {
            adapter.setListData(list);
        }
        else
        {
            adapter.addListData(list);
        }

        if (listData != null && listData.size() > 0)
        {
            listKey.page += 1;
        }
    }

    private void detachSecurityNews()
    {
        newsTitleCache.unregister(newsCacheListener);
    }

    private void fetchSecurityNews(boolean force)
    {
        if (listKey != null)
        {
            detachSecurityNews();
            newsTitleCache.register(listKey, newsCacheListener);
            newsTitleCache.getOrFetchAsync(listKey, force);
        }
    }

    private void detachSecurityDiscuss()
    {
        discussionListCache.unregister(this);
    }

    public void fetchSecurityDiscuss(boolean force)
    {
        if (discussionListKey != null)
        {
            detachSecurityDiscuss();
            discussionListCache.register(discussionListKey, this);
            discussionListCache.getOrFetchAsync(discussionListKey, force);
        }
    }

    @Override public void onDTOReceived(@NotNull DiscussionListKey key, @NotNull DiscussionKeyList value)
    {
        Timber.d("value = " + value.size());
        List<AbstractDiscussionCompactDTO> listData = new ArrayList<>();
        for (int i = 0; i < value.size(); i++)
        {
            AbstractDiscussionCompactDTO dto = discussionCache.get(value.get(i));
            listData.add(dto);
        }

        if (discussionListKey.page == 1)
        {
            adapter.setListData(listData);
        }
        else
        {
            adapter.addListData(listData);
        }

        if (value != null && value.size() > 0)
        {
            discussionListKey.page += 1;
        }

        endLoading();
    }

    @Override public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error)
    {
        Timber.d(error.getMessage());
        endLoading();
    }
}
