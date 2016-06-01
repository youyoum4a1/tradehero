package com.ayondo.academy.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.timeline.TimelineItemDTO;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;

public class TimelineDiscussionFragment extends AbstractDiscussionFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.timeline_discussion, container, false);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.discussion);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fragmentElements.getMovableBottom().animateHide();
        postCommentView.animate().translationYBy(fragmentElements.getMovableBottom().getHeight()).start();
    }

    @Override public void onDestroyView()
    {
        fragmentElements.getMovableBottom().animateShow();
        super.onDestroyView();
    }

    @NonNull @Override protected DiscussionSetAdapter createDiscussionListAdapter()
    {
        return new SingleViewDiscussionSetAdapter(getActivity(), R.layout.timeline_discussion_comment_item);
    }

    @Nullable @Override protected View inflateTopicView(@NonNull AbstractDiscussionCompactDTO topicDiscussion)
    {
        return LayoutInflater.from(getActivity()).inflate(R.layout.timeline_item_view, null, false);
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
