package com.ayondo.academy.fragments.social.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.MessageHeaderDTO;
import com.ayondo.academy.api.discussion.key.DiscussionListKey;
import com.ayondo.academy.api.discussion.key.DiscussionListKeyFactory;
import com.ayondo.academy.api.discussion.key.MessageHeaderId;
import com.ayondo.academy.api.discussion.key.MessageHeaderUserId;
import com.ayondo.academy.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.ayondo.academy.persistence.message.MessageHeaderCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;

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

    @Nullable @Override protected View inflateTopicView(@NonNull AbstractDiscussionCompactDTO topicDiscussion)
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

    @NonNull @Override protected Observable<View> getTopicViewObservable()
    {
        return Observable.combineLatest(
                super.getTopicViewObservable()
                        .observeOn(AndroidSchedulers.mainThread()),
                getTopicObservable()
                        .flatMap(new Func1<AbstractDiscussionCompactDTO, Observable<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override
                            public Observable<AbstractDiscussionCompactItemViewLinear.DTO> call(@NonNull AbstractDiscussionCompactDTO topicDiscussion)
                            {
                                return createViewDTO(topicDiscussion);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread()),
                new Func2<View, AbstractDiscussionCompactItemViewLinear.DTO, View>()
                {
                    @Override public View call(@NonNull View topicView, @NonNull AbstractDiscussionCompactItemViewLinear.DTO dto)
                    {
                        ((AbstractDiscussionCompactItemViewLinear) topicView).display(dto);
                        return topicView;
                    }
                });
    }

    @NonNull @Override protected Observable<DiscussionListKey> createTopicDiscussionListKey()
    {
        return messageHeaderCache.getOne(new MessageHeaderUserId(discussionKey.id, correspondentId))
                .map(new Func1<Pair<MessageHeaderId, MessageHeaderDTO>, DiscussionListKey>()
                {
                    @Override public DiscussionListKey call(Pair<MessageHeaderId, MessageHeaderDTO> pair)
                    {
                        linkWith(discussionKey, true);
                        return DiscussionListKeyFactory.create(pair.second);
                    }
                });
    }
}
