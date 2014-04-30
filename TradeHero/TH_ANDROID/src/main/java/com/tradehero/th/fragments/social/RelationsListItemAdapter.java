package com.tradehero.th.fragments.social;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.social.PremiumFollowRequestedListener;

public class RelationsListItemAdapter extends ArrayDTOAdapter<AllowableRecipientDTO, RelationsListItemView>
{
    private PremiumFollowRequestedListener premiumFollowRequestedListener;

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
            PremiumFollowRequestedListener premiumFollowRequestedListener)
    {
        this.premiumFollowRequestedListener = premiumFollowRequestedListener;
    }

    protected void notifyFollowRequested(UserBaseKey userBaseKey)
    {
        PremiumFollowRequestedListener listener = premiumFollowRequestedListener;
        if (listener != null)
        {
            listener.premiumFollowRequested(userBaseKey);
        }
    }

    protected PremiumFollowRequestedListener createFollowRequestedListener()
    {
        return new RelationsListItemAdapterFollowRequestedListener();
    }

    protected class RelationsListItemAdapterFollowRequestedListener implements PremiumFollowRequestedListener
    {
        @Override public void premiumFollowRequested(UserBaseKey userBaseKey)
        {
            notifyFollowRequested(userBaseKey);
        }
    }
}
