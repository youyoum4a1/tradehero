package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.fragments.news.NewsViewLinear;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import com.tradehero.th.persistence.news.NewsItemCache;
import com.tradehero.th.persistence.translation.TranslationCache;
import com.tradehero.th.persistence.translation.TranslationKey;
import javax.inject.Inject;
import timber.log.Timber;

public class NewsDiscussionFragment extends AbstractDiscussionFragment
{
    public static final String BUNDLE_KEY_TITLE_BACKGROUND_RES =
            NewsDiscussionFragment.class.getName() + ".title_bg";

    public static final String BUNDLE_KEY_SECURITY_SYMBOL =
            NewsDiscussionFragment.class.getName() + ".security_symbol";

    private static final String BUNDLE_KEY_IS_RETURNING = NewsDiscussionFragment.class.getName() + ".isReturning";

    private NewsItemDTO mDetailNewsItemDTO;

    @Inject NewsItemCache newsItemCache;
    @Inject TranslationCache translationCache;
    @Inject SocialShareTranslationHelper socialShareTranslationHelper;

    @InjectView(R.id.discussion_view) NewsDiscussionView newsDiscussionView;
    @InjectView(R.id.news_view_linear) NewsViewLinear newsView;

    @OnClick({
            R.id.news_start_new_discussion,
            R.id.discussion_action_button_comment_count,
    })
    void onStartNewDiscussion()
    {
        Bundle bundle = new Bundle();
        if (newsItemDTOKey != null)
        {
            bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                    newsItemDTOKey.getArgs());
        }
        getNavigator().pushFragment(DiscussionEditPostFragment.class, bundle);
    }

    private NewsItemDTOKey newsItemDTOKey;

    private DTOCache.GetOrFetchTask<NewsItemDTOKey, NewsItemDTO> newsFetchTask;
    private DTOCache.GetOrFetchTask<TranslationKey, TranslationResult> translationTask;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_discussion, container, false);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        Bundle bundle = getArguments();
        String title = bundle.getString(NewsDiscussionFragment.BUNDLE_KEY_SECURITY_SYMBOL);
        //bundle.putString(NewsDiscussionFragment.BUNDLE_KEY_SECURITY_SYMBOL, securityId.securitySymbol);
        actionBar.setTitle(title);
        Timber.d("onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null && args.getBoolean(BUNDLE_KEY_IS_RETURNING, false))
        {
            // TODO review here as the cache should have been updated or invalidated.
            newsDiscussionView.refresh();
        }
    }

    @Override public void onDestroyView()
    {
        detachNewsFetchTask();
        detachTranslationTask();
        Bundle args = getArguments();
        if (args != null)
        {
            args.putBoolean(BUNDLE_KEY_IS_RETURNING, true);
        }
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        super.linkWith(discussionKey, andDisplay);

        if (discussionKey instanceof NewsItemDTOKey)
        {
            linkWith((NewsItemDTOKey) discussionKey, true);
        }
    }

    private void linkWith(NewsItemDTOKey newsItemDTOKey, boolean andDisplay)
    {
        this.newsItemDTOKey = newsItemDTOKey;

        if (newsItemDTOKey != null)
        {
            NewsItemDTO cachedNews = newsItemCache.get(newsItemDTOKey);

            //linkWith(cachedNews, andDisplay);

            //fetchNewsDetail(true);
            newsView.display(newsItemDTOKey);
            setRandomBackground();
        }
        else
        {
            resetViews();
        }
    }

    private void setRandomBackground()
    {
        // TODO have to remove this hack, please!
        int bgRes = getArguments().getInt(BUNDLE_KEY_TITLE_BACKGROUND_RES, 0);
        if (bgRes != 0)
        {
            newsView.setTitleBackground(bgRes);
        }
    }

    private void resetViews()
    {
        // TODO
    }

    // TODO remove
    private void fetchNewsDetail(boolean force)
    {
        detachNewsFetchTask();
        newsFetchTask = newsItemCache.getOrFetch(newsItemDTOKey, force, createNewsFetchListener());
        newsFetchTask.execute();
    }

    private void detachNewsFetchTask()
    {
        if (newsFetchTask != null)
        {
            newsFetchTask.setListener(null);
        }
        newsFetchTask = null;
    }

    private void detachTranslationTask()
    {
        if (translationTask != null)
        {
            translationTask.setListener(null);
        }
        translationTask = null;
    }

    private void linkWith(NewsItemDTO newsItemDTO, boolean andDisplay)
    {
        mDetailNewsItemDTO = newsItemDTO;

        if (andDisplay)
        {
            newsView.linkWith(mDetailNewsItemDTO, true);
        }
    }

    protected void pushSettingsForConnect(SocialShareFormDTO socialShareFormDTO)
    {
        Bundle args = new Bundle();
        SettingsFragment.putSocialNetworkToConnect(args, socialShareFormDTO);
        getDashboardNavigator().pushFragment(SettingsFragment.class, args);
    }

    @Override
    public boolean isTabBarVisible()
    {
        return false;
    }

    private DTOCache.Listener<NewsItemDTOKey, NewsItemDTO> createNewsFetchListener()
    {
        return new NewsFetchListener();
    }

    private class NewsFetchListener implements DTOCache.Listener<NewsItemDTOKey, NewsItemDTO>
    {
        @Override
        public void onDTOReceived(NewsItemDTOKey key, NewsItemDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(NewsItemDTOKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
