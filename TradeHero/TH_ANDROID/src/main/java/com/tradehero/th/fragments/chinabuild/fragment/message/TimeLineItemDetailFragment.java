package com.tradehero.th.fragments.chinabuild.fragment.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.adapters.SecurityTimeLineDiscussOrNewsAdapter;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class TimeLineItemDetailFragment extends DashboardFragment implements DiscussionListCacheNew.DiscussionKeyListListener
{

    public static final String BUNDLE_ARGUMENT_DISCUSSTION_ID = "bundle_argment_discusstion_id";

    @Inject protected DiscussionCache discussionCache;
    private DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO> discussionFetchListener;
    DiscussionKey timelineItemDTOKey;
    private PaginatedDiscussionListKey discussionListKey;
    @Inject DiscussionListCacheNew discussionListCache;

    @InjectView(R.id.tvTimeLineDetailContent) TextView tvTimeLineDetailContent;

    private SecurityTimeLineDiscussOrNewsAdapter adapter;
    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;

    @Inject DiscussionKeyFactory discussionKeyFactory;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        discussionFetchListener = createDiscussionCacheListener();
        initArgment();
        adapter = new SecurityTimeLineDiscussOrNewsAdapter(getActivity(),true);
    }

    public void initArgment()
    {
        Bundle bundle = getArguments();
        if (bundle.containsKey(BUNDLE_ARGUMENT_DISCUSSTION_ID))
        {
            timelineItemDTOKey = discussionKeyFactory.fromBundle(bundle.getBundle(BUNDLE_ARGUMENT_DISCUSSTION_ID));

            fetchDiscussion(timelineItemDTOKey);
            discussionListKey = new PaginatedDiscussionListKey(timelineItemDTOKey.getType(), timelineItemDTOKey.id, 1, 50);
            fetchDiscussList(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("详情");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_item_detail, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    public void initView()
    {
        listTimeLine.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listTimeLine.setAdapter(adapter);

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                fetchDiscussList(true);
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("上拉加载更多");
                //refreshDataMore(false);
            }
        });
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachDiscussionFetchTask();
        detachDiscussionFetch();
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

    private void fetchDiscussion(DiscussionKey discussionKey)
    {
        detachDiscussionFetchTask();
        discussionCache.register(discussionKey, discussionFetchListener);
        discussionCache.getOrFetchAsync(discussionKey);
    }

    private void detachDiscussionFetchTask()
    {
        discussionCache.unregister(discussionFetchListener);
    }

    private void detachDiscussionFetch()
    {
        discussionListCache.unregister(this);
    }

    public void fetchDiscussList(boolean force)
    {
        if (discussionListKey != null)
        {
            detachDiscussionFetch();
            discussionListCache.register(discussionListKey, this);
            discussionListCache.getOrFetchAsync(discussionListKey, force);
        }
    }

    protected DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO> createDiscussionCacheListener()
    {
        return new PrivateDiscussionViewDiscussionCacheListener();
    }

    protected class PrivateDiscussionViewDiscussionCacheListener implements DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO>
    {
        @Override public void onDTOReceived(@NotNull DiscussionKey key, @NotNull AbstractDiscussionCompactDTO value)
        {
            //linkWithInitiating((PrivateDiscussionDTO) value, true);
            linkWithDTO(value);
            Timber.d("");
        }

        @Override public void onErrorThrown(@NotNull DiscussionKey key, @NotNull Throwable error)
        {
            //THToast.show(R.string.error_fetch_private_message_initiating_discussion);
            Timber.d("");
        }
    }

    public void linkWithDTO(AbstractDiscussionCompactDTO value)
    {
        if (value instanceof TimelineItemDTO)
        {
            tvTimeLineDetailContent.setText(((TimelineItemDTO) value).text);
        }
        else if(value instanceof NewsItemCompactDTO)
        {
            tvTimeLineDetailContent.setText(((NewsItemCompactDTO) value).description);
        }
        else if(value instanceof DiscussionDTO)
        {
            tvTimeLineDetailContent.setText(((DiscussionDTO) value).text);
        }
    }

    @Override public void onDTOReceived(@NotNull DiscussionListKey key, @NotNull DiscussionKeyList value)
    {
        Timber.d("");
        Timber.d("value = " + value.size());
        List<AbstractDiscussionCompactDTO> listData = new ArrayList<>();
        for (int i = 0; i < value.size(); i++)
        {
            AbstractDiscussionCompactDTO dto = discussionCache.get(value.get(i));
            listData.add(dto);
        }
        adapter.setListData(listData);
        listTimeLine.onRefreshComplete();
    }

    @Override public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error)
    {
        Timber.d("");
        listTimeLine.onRefreshComplete();
    }
}
