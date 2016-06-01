package com.ayondo.academy.fragments.discussion.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.ayondo.academy.fragments.discussion.AbstractDiscussionFragment;
import com.ayondo.academy.fragments.discussion.DiscussionSetAdapter;
import com.ayondo.academy.fragments.discussion.SingleViewDiscussionSetAdapter;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;

public class SecurityDiscussionCommentFragment extends AbstractDiscussionFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.security_discussion_comment, container, false);
    }

    @NonNull @Override protected DiscussionSetAdapter createDiscussionListAdapter()
    {
        return new SingleViewDiscussionSetAdapter(getActivity(), R.layout.timeline_discussion_comment_item);
    }

    @Nullable @Override protected View inflateTopicView(@NonNull AbstractDiscussionCompactDTO topicDiscussion)
    {
        return LayoutInflater.from(getActivity()).inflate(R.layout.security_discussion_item_view, null, false);
    }

    @NonNull @Override protected Observable<View> getTopicViewObservable()
    {
        return Observable.combineLatest(
                super.getTopicViewObservable()
                        .observeOn(AndroidSchedulers.mainThread()),
                getTopicObservable()
                        .flatMap(new Func1<AbstractDiscussionCompactDTO, Observable<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public Observable<AbstractDiscussionCompactItemViewLinear.DTO> call(
                                    @NonNull AbstractDiscussionCompactDTO topicDiscussion)
                            {
                                return viewDTOFactory.createDiscussionItemViewLinearDTO((DiscussionDTO) topicDiscussion);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread()),
                new Func2<View, AbstractDiscussionCompactItemViewLinear.DTO, View>()
                {
                    @Override public View call(@NonNull View topicView, @NonNull AbstractDiscussionCompactItemViewLinear.DTO dto)
                    {
                        ((SecurityDiscussionItemViewLinear) topicView).display(dto);
                        return topicView;
                    }
                });
    }

    @NonNull @Override protected Observable<AbstractDiscussionCompactItemViewLinear.DTO> createViewDTO(
            @NonNull final AbstractDiscussionCompactDTO discussion)
    {
        return viewDTOFactory.createDiscussionItemViewLinearDTO((DiscussionDTO) discussion);
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        addComment(discussionDTO);
    }
}
