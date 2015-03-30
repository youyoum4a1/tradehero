package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;
import com.tradehero.th.fragments.discussion.DiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.SingleViewDiscussionSetAdapter;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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

    @Nullable @Override protected View inflateTopicView()
    {
        return LayoutInflater.from(getActivity()).inflate(R.layout.security_discussion_item_view, null, false);
    }

    @Override protected void displayTopic(@NonNull final AbstractDiscussionCompactDTO discussionDTO)
    {
        super.displayTopic(discussionDTO);
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                viewDTOFactory.createDiscussionItemViewLinearDTO((DiscussionDTO) discussionDTO))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<AbstractDiscussionCompactItemViewLinear.DTO>()
                        {
                            @Override public void call(AbstractDiscussionCompactItemViewLinear.DTO dto)
                            {
                                ((SecurityDiscussionItemViewLinear) topicView).display(dto);
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to load SecurityDiscussion topic")));
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
