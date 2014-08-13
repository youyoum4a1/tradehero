package com.tradehero.th.fragments.social;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.models.social.OnPremiumFollowRequestedListener;
import org.jetbrains.annotations.NotNull;

public class RelationsListItemAdapter extends ArrayDTOAdapterNew<AllowableRecipientDTO, RelationsListItemView>
{
    private OnPremiumFollowRequestedListener premiumFollowRequestedListener;

    //<editor-fold desc="Constructors">
    public RelationsListItemAdapter(Context context, int layoutResId)
    {
        super(context, layoutResId);
    }
    //</editor-fold>

    @Override public RelationsListItemView getView(int position, View convertView, ViewGroup viewGroup)
    {
        RelationsListItemView prepared = super.getView(position, convertView, viewGroup);
        prepared.setPremiumFollowRequestedListener(createFollowRequestedListener());
        return prepared;
    }

    public void setPremiumFollowRequestedListener(
            OnPremiumFollowRequestedListener premiumFollowRequestedListener)
    {
        this.premiumFollowRequestedListener = premiumFollowRequestedListener;
    }

    protected void notifyFollowRequested(@NotNull UserBaseKey userBaseKey)
    {
        OnPremiumFollowRequestedListener listener = premiumFollowRequestedListener;
        if (listener != null)
        {
            listener.premiumFollowRequested(userBaseKey);
        }
    }

    protected OnPremiumFollowRequestedListener createFollowRequestedListener()
    {
        return new RelationsListItemAdapterFollowRequestedListener();
    }

    protected class RelationsListItemAdapterFollowRequestedListener implements
            OnPremiumFollowRequestedListener
    {
        @Override public void premiumFollowRequested(@NotNull UserBaseKey userBaseKey)
        {
            notifyFollowRequested(userBaseKey);
        }
    }

    public void updateItem(
            @NotNull UserBaseKey relationId,
            @NotNull UserMessagingRelationshipDTO relationshipDTO)
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
