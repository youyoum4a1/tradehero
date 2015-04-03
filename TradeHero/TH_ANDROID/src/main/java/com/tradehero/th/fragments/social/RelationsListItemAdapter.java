package com.tradehero.th.fragments.social;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.models.social.FollowRequest;
import rx.Observable;
import rx.subjects.PublishSubject;

public class RelationsListItemAdapter extends ArrayDTOAdapterNew<AllowableRecipientDTO, RelationsListItemView>
{
    @NonNull private PublishSubject<FollowRequest> followRequestBehavior;

    //<editor-fold desc="Constructors">
    public RelationsListItemAdapter(@NonNull Context context, @LayoutRes int layoutResId)
    {
        super(context, layoutResId);
        this.followRequestBehavior = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull public Observable<FollowRequest> getFollowRequestObservable()
    {
        return followRequestBehavior.asObservable();
    }

    @NonNull @Override protected RelationsListItemView inflate(int position, ViewGroup viewGroup)
    {
        RelationsListItemView prepared = super.inflate(position, viewGroup);
        prepared.getFollowRequestObservable().subscribe(followRequestBehavior);
        return prepared;
    }

    public void updateItem(
            @NonNull UserBaseKey relationId,
            @NonNull UserMessagingRelationshipDTO relationshipDTO)
    {
        AllowableRecipientDTO item;
        for (int position = 0, size = getCount(); position < size; position++)
        {
            item = getItem(position);
            if (item.user.getBaseKey().equals(relationId))
            {
                item.relationship = relationshipDTO;
            }
        }
    }
}
