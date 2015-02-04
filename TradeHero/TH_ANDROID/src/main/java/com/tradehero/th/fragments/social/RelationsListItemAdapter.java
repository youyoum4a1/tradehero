package com.tradehero.th.fragments.social;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.models.social.FollowRequest;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class RelationsListItemAdapter extends ArrayDTOAdapterNew<AllowableRecipientDTO, RelationsListItemView>
{
    @NonNull private BehaviorSubject<FollowRequest> followRequestBehavior;

    //<editor-fold desc="Constructors">
    public RelationsListItemAdapter(@NonNull Context context, @LayoutRes int layoutResId)
    {
        super(context, layoutResId);
        this.followRequestBehavior = BehaviorSubject.create();
    }
    //</editor-fold>

    @NonNull public Observable<FollowRequest> getFollowRequestObservable()
    {
        return followRequestBehavior.asObservable();
    }

    @Override public RelationsListItemView getView(int position, View convertView, ViewGroup viewGroup)
    {
        RelationsListItemView prepared = super.getView(position, convertView, viewGroup);
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
