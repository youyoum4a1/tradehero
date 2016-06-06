package com.androidth.general.fragments.discovery;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import com.androidth.general.adapters.ArrayDTOAdapterNew;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.androidth.general.models.discussion.UserDiscussionAction;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

public class DiscussionArrayAdapter extends ArrayDTOAdapterNew<
        AbstractDiscussionCompactItemViewLinear.DTO,
        AbstractDiscussionCompactItemViewLinear>
{
    @NonNull private final PublishSubject<UserDiscussionAction> userDiscussionActionSubject;

    //<editor-fold desc="Constructors">
    public DiscussionArrayAdapter(@NonNull Context context,
            @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
        userDiscussionActionSubject = PublishSubject.create();
    }

    public DiscussionArrayAdapter(@NonNull Context context,
            @LayoutRes int layoutResourceId,
            @NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> objects)
    {
        super(context, layoutResourceId, objects);
        userDiscussionActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return userDiscussionActionSubject.asObservable();
    }

    @NonNull @Override protected AbstractDiscussionCompactItemViewLinear inflate(int position, ViewGroup viewGroup)
    {
        AbstractDiscussionCompactItemViewLinear view = super.inflate(position, viewGroup);
        view.getUserActionObservable().retry().subscribe(userDiscussionActionSubject);
        return view;
    }
}
