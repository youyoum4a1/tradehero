package com.tradehero.th.fragments.social.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionListKeyFactory;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageHeaderUserId;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.persistence.message.MessageHeaderCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class ReplyPrivateMessageFragment extends AbstractPrivateMessageFragment
{
    @Inject MessageHeaderCacheRx messageHeaderCache;

    @Override
    public void onResume()
    {
        super.onResume();
        //Timber.d("ReplyPrivateMessageFragment onResume ,so refresh() doing...");
        refresh();
    }

    @Nullable @Override protected View inflateTopicView()
    {
        if (topicDiscussion instanceof DiscussionDTO)
        {
            int layoutRes;
            if (((DiscussionDTO) topicDiscussion).getSenderKey().equals(currentUserId.toUserBaseKey()))
            {
                layoutRes = R.layout.private_message_bubble_mine;
            }
            else
            {
                layoutRes = R.layout.private_message_bubble_other;
            }
            return LayoutInflater.from(getActivity()).inflate(layoutRes, null, false);
        }
        return null;
    }

    @Override protected void displayTopic(@NonNull final AbstractDiscussionCompactDTO discussionDTO)
    {
        super.displayTopic(discussionDTO);
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                createViewDTO(discussionDTO))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<AbstractDiscussionCompactItemViewLinear.DTO>()
                        {
                            @Override public void call(AbstractDiscussionCompactItemViewLinear.DTO dto)
                            {
                                ((AbstractDiscussionCompactItemViewLinear) topicView).display(dto);
                            }
                        },
                        new ToastOnErrorAction()));
    }

    @NonNull @Override protected Observable<DiscussionListKey> createTopicDiscussionListKey()
    {
        return messageHeaderCache.getOne(new MessageHeaderUserId(discussionKey.id, correspondentId))
                .map(new Func1<Pair<MessageHeaderId, MessageHeaderDTO>, DiscussionListKey>()
                {
                    @Override public DiscussionListKey call(Pair<MessageHeaderId, MessageHeaderDTO> pair)
                    {
                        linkWith(discussionKey,true);
                        return DiscussionListKeyFactory.create(pair.second);
                    }
                });
    }
}
