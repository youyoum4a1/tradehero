package com.tradehero.chinabuild.fragment.security;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
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
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.SocialSharerImpl;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheNew;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;
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

    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCacheA;

    @Inject DiscussionCache discussionCache;
    @Inject DiscussionListCacheNew discussionListCache;
    private PaginatedDiscussionListKey discussionListKey;
    private NewsItemListKey listKey;

    public int typeDiscussOrNews;

    public static final int TYPE_DISCUSS = 0;
    public static final int TYPE_NEWS = 1;

    @Inject NewsItemCompactListCacheNew newsTitleCache;
    @Nullable private DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> newsCacheListener;

    private SecurityTimeLineDiscussOrNewsAdapter adapter;
    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.tradeheroprogressbar_security_discuss_news) TradeHeroProgressBar progressBar;

    private Dialog mShareSheetDialog;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

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
        discussionListCache.invalidate(discussionListKey);
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
            refreshData(false);
        }

        if (adapter.getCount() == 0)
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.tradeheroprogressbar_security_discuss_news);
            progressBar.startLoading();
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTimeLine);
        }

        return view;
    }

    public void initView()
    {
        listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        listTimeLine.setAdapter(adapter);

        listTimeLine.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Timber.d("Clicked item position = " + l);
            }
        });

        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater()
        {
            @Override public void OnTimeLineItemClicked(int position)
            {
                Timber.d("Item position = " + position);
                AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                enterTimeLineDetail(dto);
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

            @Override public void OnTimeLineShareClicked(int position)
            {
                Timber.d("Share position = " + position);
                shareToWechatMoment(adapter.getItemString(position));
            }

            @Override public void OnTimeLineBuyClicked(int position)
            {

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

    public void enterTimeLineDetail(AbstractDiscussionCompactDTO dto)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSTION_ID, dto.getDiscussionKey().getArgs());
        pushFragment(TimeLineItemDetailFragment.class, bundle);
    }

    public void share(String strShare)
    {
        String show = getUnParsedText(strShare);

        mShareSheetTitleCache.set(show);
        ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.share_sheet_dialog_layout, null);
        contentView.setLocalSocialClickedListener(
                new ShareSheetDialogLayout.OnLocalSocialClickedListener()
                {
                    @Override public void onShareRequestedClicked()
                    {

                    }
                });
        mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
    }

    //Share to wechat moment and share to weibo on the background
    private void shareToWechatMoment(final String strShare)
    {
        String show = getUnParsedText(strShare);

        show = securityName + "(" + securityId.getDisplayName() + ") " + show;

        if (TextUtils.isEmpty(show))
        {
            return;
        }
        UserProfileDTO updatedUserProfileDTO = userProfileCacheA.get(currentUserId.toUserBaseKey());
        if (updatedUserProfileDTO != null)
        {
            if (updatedUserProfileDTO.wbLinked)
            {
                String outputStr = show;
                String downloadCNTradeHeroWeibo = getActivity().getResources().getString(R.string.download_tradehero_android_app_on_weibo);
                outputStr = WeiboUtils.getShareContentWeibo(outputStr, downloadCNTradeHeroWeibo);
                InviteFormDTO inviteFormDTO = new InviteFormWeiboDTO(outputStr);
                userServiceWrapper.get().inviteFriends(
                        currentUserId.toUserBaseKey(), inviteFormDTO, new RequestCallback());
            }
        }
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSellToTimeline;
        weChatDTO.title = show;
        ((SocialSharerImpl) socialSharerLazy.get()).share(weChatDTO, getActivity());
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
            onFinish();
        }

        @Override public void onDTOReceived(
                @NotNull NewsItemListKey key,
                @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(key, value);
            onFinish();
        }

        @Override public void onErrorThrown(
                @NotNull NewsItemListKey key,
                @NotNull Throwable error)
        {
            onFinish();
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

        onFinish();
    }

    @Override public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error)
    {
        Timber.d(error.getMessage());
        onFinish();
    }

    public void onFinish()
    {
        progressBar.stopLoading();
        betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTimeLine);
        listTimeLine.onRefreshComplete();
    }

    private class RequestCallback implements retrofit.Callback
    {

        @Override
        public void success(Object o, Response response)
        {

        }

        @Override
        public void failure(RetrofitError retrofitError)
        {

        }
    }
}
