package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.fragments.news.NewsViewLinear;
import com.tradehero.th.rx.ToastOnErrorAction;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import timber.log.Timber;

public class NewsDiscussionFragment extends AbstractDiscussionFragment
{
    private static final String BUNDLE_KEY_TITLE_BACKGROUND_RES = NewsDiscussionFragment.class.getName() + ".title_bg";
    private static final String BUNDLE_KEY_SECURITY_SYMBOL = NewsDiscussionFragment.class.getName() + ".security_symbol";
    private static final String BUNDLE_KEY_IS_RETURNING = NewsDiscussionFragment.class.getName() + ".isReturning";

    @SuppressWarnings("unused") @Inject Context doNotRemoveOrItFails;

    @InjectView(R.id.news_view_linear) NewsViewLinear newsView;

    public static void putBackgroundResId(@NonNull Bundle args, @DrawableRes int resId)
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

    @NonNull @Override protected DiscussionSetAdapter createDiscussionListAdapter()
    {
        return new SingleViewDiscussionSetAdapter(getActivity(), R.layout.timeline_discussion_comment_item);
    }

    @Nullable @Override protected View inflateTopicView()
    {
        return LayoutInflater.from(getActivity()).inflate(R.layout.news_detail_view_header, null, false);
    }

    @Override protected void displayTopic(@NonNull final AbstractDiscussionCompactDTO discussionDTO)
    {
        super.displayTopic(discussionDTO);
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                viewDTOFactory.createAbstractDiscussionCompactItemViewLinearDTO(discussionDTO))
                .subscribe(
                        new Action1<AbstractDiscussionCompactItemViewLinear.DTO>()
                        {
                            @Override public void call(AbstractDiscussionCompactItemViewLinear.DTO viewDTO)
                            {
                                ((AbstractDiscussionCompactItemViewLinear) topicView).display(viewDTO);
                            }
                        },
                        new ToastOnErrorAction()));
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
