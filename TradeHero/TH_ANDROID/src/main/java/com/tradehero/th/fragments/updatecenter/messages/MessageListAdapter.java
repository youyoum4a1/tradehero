package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageListAdapter extends ViewDTOSetAdapter<MessageHeaderDTO, MessageItemViewWrapper>
{
    private final int layoutResourceId;
    private MessageItemViewWrapper.OnElementClickedListener elementClickedListener;
    @Nullable private Comparator<MessageHeaderDTO> comparator;

    //<editor-fold desc="Constructors">
    public MessageListAdapter(
            @NotNull Context context,
            @Nullable Collection<MessageHeaderDTO> objects,
            int layoutResourceId,
            @Nullable Comparator<MessageHeaderDTO> comparator)
    {
        super(context, objects);
        this.layoutResourceId = layoutResourceId;
        this.comparator = comparator;
    }
    //</editor-fold>

    @Override @NotNull protected Set<MessageHeaderDTO> createSet(@Nullable Collection<MessageHeaderDTO> objects)
    {
        Set<MessageHeaderDTO> set;
        if (comparator != null)
        {
            set = new TreeSet<>(comparator);
            if (objects != null)
            {
                set.addAll(objects);
            }
        }
        else
        {
            set = super.createSet(objects);
        }
        return set;
    }

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
