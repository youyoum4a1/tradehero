package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import java.util.Collection;
import java.util.Comparator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MessageListAdapter extends ViewDTOSetAdapter<MessageHeaderDTO, MessageItemViewWrapper>
{
    @LayoutRes private final int layoutResourceId;
    private MessageItemViewWrapper.OnElementClickedListener elementClickedListener;

    //<editor-fold desc="Constructors">
    public MessageListAdapter(
            @NonNull Context context,
            @Nullable Collection<MessageHeaderDTO> objects,
            @LayoutRes int layoutResourceId,
            @Nullable Comparator<MessageHeaderDTO> comparator)
    {
        super(context, comparator, objects);
        this.layoutResourceId = layoutResourceId;
    }
    //</editor-fold>

    @Override public MessageItemViewWrapper getView(int position, View convertView, ViewGroup parent)
    {
        MessageItemViewWrapper view = super.getView(position, convertView, parent);
        view.setElementClickedListener(createUserClickedListener());
        return view;
    }

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return layoutResourceId;
    }

    public void setElementClickedListener(
            MessageItemViewWrapper.OnElementClickedListener elementClickedListener)
    {
        this.elementClickedListener = elementClickedListener;
    }

    protected void handleUserClicked(MessageHeaderDTO messageHeaderDTO)
    {
        notifyUserClicked(messageHeaderDTO);
    }

    protected void handleDeleteClicked(MessageHeaderDTO messageHeaderDTO)
    {
        notifyDeleteClicked(messageHeaderDTO);
    }

    protected void notifyUserClicked(MessageHeaderDTO messageHeaderDTO)
    {
        MessageItemViewWrapper.OnElementClickedListener elementClickedListenerCopy =
                elementClickedListener;
        if (elementClickedListenerCopy != null)
        {
            elementClickedListenerCopy.onUserClicked(messageHeaderDTO);
        }
    }

    protected void notifyDeleteClicked(MessageHeaderDTO messageHeaderDTO)
    {
        MessageItemViewWrapper.OnElementClickedListener elementClickedListener =
                this.elementClickedListener;
        if (elementClickedListener != null)
        {
            elementClickedListener.onDeleteClicked(messageHeaderDTO);
        }
    }

    protected MessageItemViewWrapper.OnElementClickedListener createUserClickedListener()
    {
        return new MessageListAdapterOnElementClickedListener();
    }

    protected class MessageListAdapterOnElementClickedListener implements MessageItemViewWrapper.OnElementClickedListener
    {
        @Override public void onUserClicked(MessageHeaderDTO messageHeaderDTO)
        {
            handleUserClicked(messageHeaderDTO);
        }

        @Override public void onDeleteClicked(MessageHeaderDTO messageHeaderDTO)
        {
            handleDeleteClicked(messageHeaderDTO);
        }
    }
}
