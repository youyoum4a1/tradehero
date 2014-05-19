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
    private MessageItemViewWrapper.OnElementClickedListener elementClickedListener;

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
        view.setElementClickedListener(createUserClickedListener());
        return view;
    }

    @Override protected int getViewResId(int position)
    {
        return layoutResourceId;
    }

    public void setElementClickedListener(
            MessageItemViewWrapper.OnElementClickedListener elementClickedListener)
    {
        this.elementClickedListener = elementClickedListener;
    }

    protected void handleUserClicked(MessageHeaderId messageHeaderId)
    {
        notifyUserClicked(messageHeaderId);
    }

    protected void handleDeleteClicked(MessageHeaderId messageHeaderId)
    {
        notifyDeleteClicked(messageHeaderId);
    }

    protected void notifyUserClicked(MessageHeaderId messageHeaderId)
    {
        MessageItemViewWrapper.OnElementClickedListener elementClickedListenerCopy =
                elementClickedListener;
        if (elementClickedListenerCopy != null)
        {
            elementClickedListenerCopy.onUserClicked(messageHeaderId);
        }
    }

    protected void notifyDeleteClicked(MessageHeaderId messageHeaderId)
    {
        MessageItemViewWrapper.OnElementClickedListener elementClickedListener =
                this.elementClickedListener;
        if (elementClickedListener != null)
        {
            elementClickedListener.onDeleteClicked(messageHeaderId);
        }
    }

    protected MessageItemViewWrapper.OnElementClickedListener createUserClickedListener()
    {
        return new MessageListAdapterOnElementClickedListener();
    }

    protected class MessageListAdapterOnElementClickedListener implements MessageItemViewWrapper.OnElementClickedListener
    {
        @Override public void onUserClicked(MessageHeaderId messageHeaderId)
        {
            handleUserClicked(messageHeaderId);
        }

        @Override public void onDeleteClicked(MessageHeaderId messageHeaderId)
        {
            handleDeleteClicked(messageHeaderId);
        }
    }
}
