package com.tradehero.th.fragments.social;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;

public class RelationsListItemAdapter extends ArrayDTOAdapter<AllowableRecipientDTO, RelationsListItemView>
{
    private RelationsListItemView.OnFollowRequestedListener followRequestedListener;

    public RelationsListItemAdapter(Context context, LayoutInflater inflater, int layoutResId)
    {
        super(context, inflater, layoutResId);
    }

    @Override protected void fineTune(int position, AllowableRecipientDTO allowableRecipientDTO,
            RelationsListItemView relationsListItemView)
    {
        relationsListItemView.setFollowRequestedListener(createFollowRequestedListener());
    }

    public void setFollowRequestedListener(
            RelationsListItemView.OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListener = followRequestedListener;
    }

    protected void notifyFollowRequested(UserBaseKey userBaseKey)
    {
        RelationsListItemView.OnFollowRequestedListener listener = followRequestedListener;
        if (listener != null)
        {
            listener.onFollowRequested(userBaseKey);
        }
    }

    protected RelationsListItemView.OnFollowRequestedListener createFollowRequestedListener()
    {
        return new RelationsListItemAdapterFollowRequestedListener();
    }

    protected class RelationsListItemAdapterFollowRequestedListener implements RelationsListItemView.OnFollowRequestedListener
    {
        @Override public void onFollowRequested(UserBaseKey userBaseKey)
        {
            notifyFollowRequested(userBaseKey);
        }
    }
}
