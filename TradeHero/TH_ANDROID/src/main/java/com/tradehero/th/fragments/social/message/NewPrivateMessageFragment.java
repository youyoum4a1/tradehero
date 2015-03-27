package com.tradehero.th.fragments.social.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.message.MessageThreadHeaderCacheRx;
import javax.inject.Inject;
import retrofit.RetrofitError;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import timber.log.Timber;

public class NewPrivateMessageFragment extends AbstractPrivateMessageFragment
{
    protected boolean isFresh = true;

    @Inject protected MessageThreadHeaderCacheRx messageThreadHeaderCache;
    @Nullable protected Subscription messageThreadHeaderFetchSubscription;

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

    @Nullable @Override protected View inflateTopicView()
    {
        return null; // TODO better?
    }

    @Override protected void displayTopic(@NonNull AbstractDiscussionCompactDTO discussionDTO)
    {
        super.displayTopic(discussionDTO);
        // TODO Nothing else to do?
    }

    protected void fetchMessageThreadHeader()
    {
        unsubscribe(messageThreadHeaderFetchSubscription);
        messageThreadHeaderFetchSubscription = AppObservable.bindFragment(
                this,
                messageThreadHeaderCache.get(correspondentId))
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
