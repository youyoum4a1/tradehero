package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.rx.ToastOnErrorAction;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.functions.Action1;

public class TimelineDiscussionFragment extends AbstractDiscussionFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.timeline_discussion, container, false);
    }

    @NonNull @Override protected DiscussionSetAdapter createDiscussionListAdapter()
    {
        return new SingleViewDiscussionSetAdapter(getActivity(), R.layout.timeline_discussion_comment_item);
    }

    @Nullable @Override protected View inflateTopicView()
    {
        return LayoutInflater.from(getActivity()).inflate(R.layout.timeline_item_view, null, false);
    }

    @Override protected void displayTopic(@NonNull final AbstractDiscussionCompactDTO discussionDTO)
    {
        super.displayTopic(discussionDTO);
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                createViewDTO(discussionDTO))
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

    @NonNull @Override
    protected Observable<AbstractDiscussionCompactItemViewLinear.DTO> createViewDTO(@NonNull final AbstractDiscussionCompactDTO discussion)
    {
        if (discussion instanceof TimelineItemDTO)
        {
            return viewDTOFactory.createTimelineItemViewLinearDTO((TimelineItemDTO) discussion);
        }
        return viewDTOFactory.createDiscussionItemViewLinearDTO((DiscussionDTO) discussion);
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        addComment(discussionDTO);
    }

    @Override public <T extends Fragment> boolean allowNavigateTo(@NonNull Class<T> fragmentClass, Bundle args)
    {
        boolean basicCheck = !this.getClass().isAssignableFrom(fragmentClass) && super.allowNavigateTo(fragmentClass, args);
        boolean sameKeyCheck = getDiscussionKey() != TimelineDiscussionFragment.getDiscussionKey(args);
        return basicCheck && sameKeyCheck;
    }
}
