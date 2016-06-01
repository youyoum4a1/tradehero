package com.ayondo.academy.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import com.ayondo.academy.adapters.ViewDTOSetAdapter;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.models.discussion.UserDiscussionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

abstract public class DiscussionSetAdapter
        extends ViewDTOSetAdapter<
        AbstractDiscussionCompactItemViewLinear.DTO,
        AbstractDiscussionCompactItemViewLinear>
{
    @NonNull private final PublishSubject<UserDiscussionAction> userDiscussionActionSubject;

    //<editor-fold desc="Constructors">
    public DiscussionSetAdapter(@NonNull Context context)
    {
        super(context, new AbstractDiscussionCompactItemViewDTODateComparator());
        userDiscussionActionSubject = PublishSubject.create();
    }

    public DiscussionSetAdapter(@NonNull Context context,
            @Nullable Comparator<AbstractDiscussionCompactItemViewLinear.DTO> comparator)
    {
        super(context, comparator);
        userDiscussionActionSubject = PublishSubject.create();
    }

    public DiscussionSetAdapter(
            @NonNull Context context,
            @Nullable Collection<AbstractDiscussionCompactItemViewLinear.DTO> objects)
    {
        super(context, new AbstractDiscussionCompactItemViewDTODateComparator(), objects);
        userDiscussionActionSubject = PublishSubject.create();
    }

    public DiscussionSetAdapter(@NonNull Context context,
            @Nullable Comparator<AbstractDiscussionCompactItemViewLinear.DTO> comparator,
            @Nullable Collection<AbstractDiscussionCompactItemViewLinear.DTO> objects)
    {
        super(context, comparator, objects);
        userDiscussionActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return userDiscussionActionSubject.asObservable();
    }

    @Override protected AbstractDiscussionCompactItemViewLinear inflate(int position, ViewGroup parent)
    {
        AbstractDiscussionCompactItemViewLinear view = super.inflate(position, parent);
        view.getUserActionObservable().retry().subscribe(userDiscussionActionSubject);
        return view;
    }

    public void appendTail(@Nullable AbstractDiscussionCompactItemViewLinear.DTO newElement)
    {
        if (newElement != null)
        {
            if (newElement.viewHolderDTO.discussionDTO instanceof DiscussionDTO)
            {
                DiscussionDTO discussionDTO = (DiscussionDTO) newElement.viewHolderDTO.discussionDTO;
                if (discussionDTO.stubKey != null)
                {
                    AbstractDiscussionCompactItemViewLinear.DTO toRemove = null;
                    for (AbstractDiscussionCompactItemViewLinear.DTO item : set)
                    {
                        if (item.viewHolderDTO.discussionDTO.id == discussionDTO.stubKey.id)
                        {
                            toRemove = item;
                        }
                    }
                    if (toRemove != null)
                    {
                        set.remove(toRemove);
                    }
                }
            }
            List<AbstractDiscussionCompactItemViewLinear.DTO> toAdd = new ArrayList<>();
            toAdd.add(newElement);
            appendTail(toAdd);
        }
    }
}
