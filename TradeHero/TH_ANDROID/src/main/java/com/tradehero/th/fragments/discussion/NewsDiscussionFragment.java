package com.ayondo.academy.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.news.NewsItemCompactDTO;
import com.ayondo.academy.api.news.NewsItemDTO;
import com.ayondo.academy.fragments.news.NewsViewLinear;
import com.ayondo.academy.models.discussion.UserDiscussionAction;
import com.ayondo.academy.rx.TimberAndToastOnErrorAction1;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
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

    @Nullable @Override protected NewsViewLinear inflateTopicView(@NonNull AbstractDiscussionCompactDTO topicDiscussion)
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
                        new TimberAndToastOnErrorAction1("Failed to register to topic's user action")));
        return topicView;
    }

    @NonNull @Override protected Observable<View> getTopicViewObservable()
    {
        return Observable.combineLatest(
                super.getTopicViewObservable()
                        .observeOn(AndroidSchedulers.mainThread()),
                super.getTopicObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<AbstractDiscussionCompactDTO, Observable<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public Observable<AbstractDiscussionCompactItemViewLinear.DTO> call(
                                    @NonNull AbstractDiscussionCompactDTO topicDiscussion)
                            {
                                setActionBarTitle(((NewsItemCompactDTO) topicDiscussion).title);
                                if (topicDiscussion instanceof NewsItemDTO)
                                {
                                    return viewDTOFactory.createNewsViewLinearDTO((NewsItemDTO) topicDiscussion);
                                }
                                return null;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread()),
                new Func2<View, AbstractDiscussionCompactItemViewLinear.DTO, View>()
                {
                    @Override public View call(View topicView, @Nullable AbstractDiscussionCompactItemViewLinear.DTO viewDTO)
                    {
                        if (viewDTO != null)
                        {
                            ((NewsViewLinear) topicView).display(viewDTO);
                        }
                        return topicView;
                    }
                })
                .share();
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        // Nothing to do
    }
}
