package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.key.MessageHeaderId;

public class MessageItemViewWrapper extends FrameLayout implements DTOView<MessageHeaderId>
{
    @InjectView(R.id.message_item_front) MessageItemView messageItemView;
    @InjectView(R.id.message_item_back) View messageItemBackView;

    private MessageHeaderId messageHeaderId;
    private OnElementClickedListener elementClickedListener;

    //<editor-fold desc="Constructors">
    public MessageItemViewWrapper(Context context)
    {
        super(context);
    }

    public MessageItemViewWrapper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MessageItemViewWrapper(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initView();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        initView();
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        setElementClickedListener(null);
        super.onDetachedFromWindow();
    }

    protected void initView()
    {
        ButterKnife.inject(this);
        messageItemView.setElementClickedListener(createMessageItemViewUserClickedListener());
    }

    @Override public void display(MessageHeaderId dto)
    {
        this.messageHeaderId = dto;
        if (messageItemView != null)
        {
            messageItemView.display(dto);
        }
    }

    public void setElementClickedListener(OnElementClickedListener elementClickedListener)
    {
        this.elementClickedListener = elementClickedListener;
    }

    protected void notifyUserClicked(MessageHeaderId messageHeaderId)
    {
        OnElementClickedListener elementClickedListenerCopy = elementClickedListener;
        if (elementClickedListenerCopy != null)
        {
            elementClickedListenerCopy.onUserClicked(messageHeaderId);
        }
    }

    protected void notifyDeleteClicked(MessageHeaderId messageHeaderId)
    {
        OnElementClickedListener elementClickedListenerCopy = elementClickedListener;
        if (elementClickedListenerCopy != null)
        {
            elementClickedListenerCopy.onDeleteClicked(messageHeaderId);
        }
    }

    protected MessageItemView.OnElementClickedListener createMessageItemViewUserClickedListener()
    {
        return new MessageItemWrapperElementClickedListener();
    }

    public class MessageItemWrapperElementClickedListener implements MessageItemView.OnElementClickedListener
    {
        @Override public void onUserClicked(MessageHeaderId messageHeaderId)
        {
            notifyUserClicked(messageHeaderId);
        }
    }

    public static interface OnElementClickedListener extends MessageItemView.OnElementClickedListener
    {
        void onDeleteClicked(MessageHeaderId messageHeaderId);
    }
}
