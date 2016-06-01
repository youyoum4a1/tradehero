package com.ayondo.academy.fragments.social.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.MessageHeaderDTO;
import com.ayondo.academy.api.discussion.key.DiscussionKeyFactory;
import com.ayondo.academy.api.discussion.key.DiscussionListKey;
import com.ayondo.academy.api.discussion.key.DiscussionListKeyFactory;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.persistence.message.MessageThreadHeaderCacheRx;
import javax.inject.Inject;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class NewPrivateMessageFragment extends AbstractPrivateMessageFragment
{
    protected boolean isFresh = true;

    @Inject protected MessageThreadHeaderCacheRx messageThreadHeaderCache;
    @Nullable protected Subscription messageThreadHeaderFetchSubscription;

    private DiscussionListKey discussionListKey;

    @Override public void onResume()
    {
        super.onResume();
        fetchMessageThreadHeader();
    }

    @Override public void onDestroyView()
    {
        unsubscribe(messageThreadHeaderFetchSubscription);
        messageThreadHeaderFetchSubscription = null;
        super.onDestroyView();
    }

    @NonNull @Override protected Observable<AbstractDiscussionCompactDTO> getTopicObservable()
    {
        return Observable.empty();
    }

    @Nullable @Override protected View inflateTopicView(@NonNull AbstractDiscussionCompactDTO topicDiscussion)
    {
        return null; // TODO better?
    }

    protected void fetchMessageThreadHeader()
    {
        unsubscribe(messageThreadHeaderFetchSubscription);
        messageThreadHeaderFetchSubscription = AppObservable.bindSupportFragment(
                this,
                messageThreadHeaderCache.get(correspondentId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createMessageThreadHeaderCacheObserver());
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        super.handleCommentPosted(discussionDTO);
        isFresh = false;
        if (getDiscussionKey() == null)
        {
            // We do this in order to ensure the next message is not a new one.
            linkWith(discussionDTO.getDiscussionKey(), true);
        }
    }

    @NonNull @Override protected Observable<DiscussionListKey> createTopicDiscussionListKey()
    {
        if (discussionListKey != null)
        {
            return Observable.just(discussionListKey);
        }
        else
        {
            return Observable.empty();
        }

    }

    @NonNull protected Observer<Pair<UserBaseKey, MessageHeaderDTO>> createMessageThreadHeaderCacheObserver()
    {
        return new NewPrivateMessageFragmentThreadHeaderCacheObserver();
    }

    protected class NewPrivateMessageFragmentThreadHeaderCacheObserver
            implements Observer<Pair<UserBaseKey, MessageHeaderDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, MessageHeaderDTO> pair)
        {
            if (getDiscussionKey() == null)
            {
                linkWith(DiscussionKeyFactory.create(pair.second), true);
                discussionListKey = DiscussionListKeyFactory.create(pair.second);
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            if (!(e instanceof RetrofitError) ||
                    ((RetrofitError) e).getResponse() == null ||
                    ((RetrofitError) e).getResponse().getStatus() != 404)
            {
                THToast.show(R.string.error_fetch_message_thread_header);
                Timber.e(e, "Error while getting message thread");
            }
            // Otherwise there is just no existing thread
        }
    }
}
