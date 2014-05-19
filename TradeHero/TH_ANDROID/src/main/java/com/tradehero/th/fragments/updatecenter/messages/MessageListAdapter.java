package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import java.util.Collection;

public class MessageListAdapter extends ViewDTOSetAdapter<MessageHeaderId, MessageItemViewWrapper>
{
    private int layoutResourceId;
    private MessageItemView.OnUserClickedListener userClickedListener;

    //<editor-fold desc="Constructors">
    public MessageListAdapter(Context context, Collection<MessageHeaderId> objects, int layoutResourceId)
    {
        super(context, objects);
        this.layoutResourceId = layoutResourceId;
    }
    //</editor-fold>

    @Override public MessageItemViewWrapper getView(int position, View convertView, ViewGroup parent)
    {
        MessageItemViewWrapper view = super.getView(position, convertView, parent);
        // TODO too cowboy here?
        view.messageItemView.setUserClickedListener(createUserClickedListener());
        return view;
    }

    @Override protected int getViewResId(int position)
    {
        return layoutResourceId;
    }

    public void setUserClickedListener(MessageItemView.OnUserClickedListener userClickedListener)
    {
        this.userClickedListener = userClickedListener;
    }

    protected void handleUserClicked(MessageHeaderId messageHeaderId)
    {
        notifyUserClicked(messageHeaderId);
    }

    protected void notifyUserClicked(MessageHeaderId messageHeaderId)
    {
        MessageItemView.OnUserClickedListener userClickedListenerCopy = userClickedListener;
        if (userClickedListenerCopy != null)
        {
            userClickedListenerCopy.onUserClicked(messageHeaderId);
        }
    }

    protected MessageItemView.OnUserClickedListener createUserClickedListener()
    {
        return new MessageListAdapterOnUserClickedListener();
    }

    protected class MessageListAdapterOnUserClickedListener implements MessageItemView.OnUserClickedListener
    {
        @Override public void onUserClicked(MessageHeaderId messageHeaderId)
        {
            handleUserClicked(messageHeaderId);
        }
    }
}
