package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.fragments.news.NewsViewLinear;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import timber.log.Timber;

public class NewsDiscussionFragment extends AbstractDiscussionFragment
{
    private static final String BUNDLE_KEY_TITLE_BACKGROUND_RES =
            NewsDiscussionFragment.class.getName() + ".title_bg";

    private static final String BUNDLE_KEY_SECURITY_SYMBOL =
            NewsDiscussionFragment.class.getName() + ".security_symbol";

    private static final String BUNDLE_KEY_IS_RETURNING = NewsDiscussionFragment.class.getName() + ".isReturning";

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @InjectView(R.id.discussion_view) NewsDiscussionView newsDiscussionView;
    @InjectView(R.id.news_view_linear) NewsViewLinear newsView;

    private NewsItemDTOKey newsItemDTOKey;

    public static void putBackgroundResId(@NonNull Bundle args, int resId)
    {
        args.putInt(NewsDiscussionFragment.BUNDLE_KEY_TITLE_BACKGROUND_RES, resId);
    }

    public static void putSecuritySymbol(@NonNull Bundle args, @NonNull String symbol)
    {
        args.putString(NewsDiscussionFragment.BUNDLE_KEY_SECURITY_SYMBOL, symbol);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_news_discussion, container, false);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Bundle bundle = getArguments();
        String title = bundle.getString(NewsDiscussionFragment.BUNDLE_KEY_SECURITY_SYMBOL);
        setActionBarTitle(title);
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
        Bundle args = getArguments();
        if (args != null)
        {
            args.putBoolean(BUNDLE_KEY_IS_RETURNING, true);
        }
        super.onDestroyView();
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
            newsView.display(newsItemDTOKey);
            setRandomBackground();
        }
        else
        {
            resetViews();
        }
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        // Nothing to do
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
}
