package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by wangliang on 14-4-4.
 */
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

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        ButterKnife.reset(this);

    }

    @Override public void display(MessageHeaderId dto)
    {
        if (messageItemView != null)
        {
            messageItemView.display(dto);
        }
    }



}
