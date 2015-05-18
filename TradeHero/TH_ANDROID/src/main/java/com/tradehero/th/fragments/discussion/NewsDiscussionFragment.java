package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.fragments.news.NewsViewLinear;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

public class NewsDiscussionFragment extends AbstractDiscussionFragment
{
    private static final String BUNDLE_KEY_SECURITY_SYMBOL = NewsDiscussionFragment.class.getName() + ".security_symbol";
    private static final String BUNDLE_KEY_IS_RETURNING = NewsDiscussionFragment.class.getName() + ".isReturning";

    @SuppressWarnings("unused") @Inject Context doNotRemoveOrItFails;

    protected SubscriptionList onDestroyViewSubscriptions;

    public static void putSecuritySymbol(@NonNull Bundle args, @NonNull String symbol)
    {
        args.putString(NewsDiscussionFragment.BUNDLE_KEY_SECURITY_SYMBOL, symbol);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        onDestroyViewSubscriptions = new SubscriptionList();
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
        onDestroyViewSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @NonNull @Override protected DiscussionSetAdapter createDiscussionListAdapter()
    {
        return new SingleViewDiscussionSetAdapter(getActivity(), R.layout.timeline_discussion_comment_item);
    }

    @Nullable @Override protected NewsViewLinear inflateTopicView()
    {
        NewsViewLinear topicView = (NewsViewLinear) LayoutInflater.from(getActivity()).inflate(R.layout.news_detail_view_header, null, false);
        onDestroyViewSubscriptions.add(topicView.getUserActionObservable()
                .subscribe(
                        new Action1<UserDiscussionAction>()
                        {
                            @Override public void call(UserDiscussionAction userDiscussionAction)
                            {
                                discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to register to topic's user action")));
        return topicView;
    }

    @Override protected void displayTopic(@NonNull final AbstractDiscussionCompactDTO discussionDTO)
    {
        super.displayTopic(discussionDTO);
        // Because the cache saves both NewsItemDTO and NewsItemCompactDTO with a NewsItemDTOKey
        if (discussionDTO instanceof NewsItemDTO)
        {
            onStopSubscriptions.add(AppObservable.bindFragment(
                    this,
                    viewDTOFactory.createNewsViewLinearDTO((NewsItemDTO) discussionDTO))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<AbstractDiscussionCompactItemViewLinear.DTO>()
                            {
                                @Override public void call(AbstractDiscussionCompactItemViewLinear.DTO viewDTO)
                                {
                                    ((NewsViewLinear) topicView).display(viewDTO);
                                }
                            },
                            new ToastOnErrorAction()));
        }
        setActionBarTitle(((NewsItemCompactDTO) discussionDTO).title);
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        // Nothing to do
    }
}
