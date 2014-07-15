package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.MessageHeaderDTO;

public class MessageItemViewWrapper extends FrameLayout implements DTOView<MessageHeaderDTO>
{
    @InjectView(R.id.swipelist_frontview) MessageItemView messageItemView;
    @InjectView(R.id.swipelist_backview) View messageItemBackView;

    private MessageHeaderDTO messageHeaderDTO;
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

    @Override public void display(MessageHeaderDTO dto)
    {
        this.messageHeaderDTO = dto;
        if (messageItemView != null)
        {
            messageItemView.display(dto);
        }
    }

    public void setElementClickedListener(OnElementClickedListener elementClickedListener)
    {
        this.elementClickedListener = elementClickedListener;
    }

    protected void notifyUserClicked(MessageHeaderDTO messageHeaderDTO)
    {
        OnElementClickedListener elementClickedListenerCopy = elementClickedListener;
        if (elementClickedListenerCopy != null)
        {
            elementClickedListenerCopy.onUserClicked(messageHeaderDTO);
        }
    }

    protected void notifyDeleteClicked(MessageHeaderDTO messageHeaderDTO)
    {
        OnElementClickedListener elementClickedListenerCopy = elementClickedListener;
        if (elementClickedListenerCopy != null)
        {
            elementClickedListenerCopy.onDeleteClicked(messageHeaderDTO);
        }
    }

    protected MessageItemView.OnElementClickedListener createMessageItemViewUserClickedListener()
    {
        return new MessageItemWrapperElementClickedListener();
    }

    public class MessageItemWrapperElementClickedListener implements MessageItemView.OnElementClickedListener
    {
        @Override public void onUserClicked(MessageHeaderDTO messageHeaderDTO)
        {
            notifyUserClicked(messageHeaderDTO);
        }
    }

    public static interface OnElementClickedListener extends MessageItemView.OnElementClickedListener
    {
        void onDeleteClicked(MessageHeaderDTO messageHeaderDTO);
    }
}
