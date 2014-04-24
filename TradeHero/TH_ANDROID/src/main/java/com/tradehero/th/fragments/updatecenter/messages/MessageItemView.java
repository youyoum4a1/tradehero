package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class MessageItemView extends LinearLayout implements DTOView<MessageHeaderId>
{
    @Inject MessageHeaderCache messageHeaderCache;
    @Inject Picasso picasso;
    @Inject PrettyTime prettyTime;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;

    @InjectView(R.id.message_item_icon) ImageView iconView;
    @InjectView(R.id.message_item_title) TextView titleView;
    @InjectView(R.id.message_item_sub_title) TextView subTitleView;
    @InjectView(R.id.message_item_date) TextView dateView;
    @InjectView(R.id.message_item_content) TextView contentView;

    private MessageHeaderDTO messageHeaderDTO;

    //<editor-fold desc="Constructors">
    public MessageItemView(Context context)
    {
        super(context);
    }

    public MessageItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        ButterKnife.reset(this);
    }

    @Override public void display(MessageHeaderId dto)
    {
        this.messageHeaderDTO = messageHeaderCache.get(dto);
        if(messageHeaderDTO != null)
        {
            if(messageHeaderDTO.discussionType == DiscussionType.BROADCAST_MESSAGE)
            {
                setBackgroundColor(getResources().getColor(R.color.broadcast_message_item_bg));
            }
            else if (messageHeaderDTO.discussionType == DiscussionType.PRIVATE_MESSAGE)
            {
                setBackgroundColor(getResources().getColor(R.color.private_message_item_bg));
            }

        }
        displayData();
    }

    private void displayData()
    {
        if (messageHeaderDTO != null)
        {
            titleView.setText(messageHeaderDTO.title);
            subTitleView.setText(messageHeaderDTO.subTitle);
            contentView.setText(messageHeaderDTO.message);
            dateView.setText(prettyTime.format(messageHeaderDTO.createdAtUtc));
            if (messageHeaderDTO.imageUrl != null && iconView != null)
            {
                picasso.load(messageHeaderDTO.imageUrl)
                        .transform(userPhotoTransformation)
                        .into(iconView, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                setDefaultIcon();
                            }
                        });
            }
            else
            {
                setDefaultIcon();
            }
        }
    }

    private void setDefaultIcon()
    {
        ImageView iconViewCopy = iconView;
        if (iconViewCopy != null)
        {
            picasso.cancelRequest(iconViewCopy);
            picasso.load(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(iconViewCopy);
        }
    }
}
