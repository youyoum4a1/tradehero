package com.tradehero.th.fragments.social;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.social.OnPremiumFollowRequestedListener;

public class RelationsListItemAdapter extends ArrayDTOAdapter<AllowableRecipientDTO, RelationsListItemView>
{
    private OnPremiumFollowRequestedListener premiumFollowRequestedListener;

    public RelationsListItemAdapter(Context context, LayoutInflater inflater, int layoutResId)
    {
        super(context, inflater, layoutResId);
    }

    @Override protected void fineTune(int position, AllowableRecipientDTO allowableRecipientDTO,
            RelationsListItemView relationsListItemView)
    {
        relationsListItemView.setPremiumFollowRequestedListener(createFollowRequestedListener());
    }

    public void setPremiumFollowRequestedListener(
            OnPremiumFollowRequestedListener premiumFollowRequestedListener)
    {
        this.premiumFollowRequestedListener = premiumFollowRequestedListener;
    }

    protected void notifyFollowRequested(UserBaseKey userBaseKey)
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
        @Override public void premiumFollowRequested(UserBaseKey userBaseKey)
        {
            notifyFollowRequested(userBaseKey);
        }
    }
}
