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

    public MessageItemViewWrapper(Context context)
    {
        super(context);
    }

    public MessageItemViewWrapper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(MessageHeaderId dto)
    {
        if (messageItemView != null)
        {
            messageItemView.display(dto);
        }
    }
}
