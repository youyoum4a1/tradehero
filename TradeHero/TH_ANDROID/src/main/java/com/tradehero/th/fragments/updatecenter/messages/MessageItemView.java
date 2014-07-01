package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Callback;
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

public class MessageItemView extends LinearLayout
        implements DTOView<MessageHeaderId>
{
    @Inject MessageHeaderCache messageHeaderCache;
    @Inject Picasso picasso;
    @Inject PrettyTime prettyTime;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;

    @InjectView(R.id.message_item_icon) ImageView mMessageIcon;
    @InjectView(R.id.message_item_title) TextView mMessageTitle;
    @InjectView(R.id.message_item_sub_title) TextView mMessageSubTitle;
    @InjectView(R.id.message_item_date) TextView mMessageDate;
    @InjectView(R.id.message_item_content) TextView mMessageContent;
    @InjectView(R.id.message_unread_flag) View mUnreadFlag;

    private MessageHeaderId messageHeaderId;
    private MessageHeaderDTO messageHeaderDTO;
    private OnElementClickedListener elementClickedListener;

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

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        setElementClickedListener(null);
        resetMessageIcon();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(MessageHeaderId dto)
    {
        this.messageHeaderId = dto;
        this.messageHeaderDTO = messageHeaderCache.get(dto);
        if (messageHeaderDTO != null)
        {
            setBackgroundColor(getResources().getColor(R.color.private_message_item_bg));
        }
        display();
    }

    private void display()
    {
        if (messageHeaderDTO != null)
        {
            mMessageTitle.setText(messageHeaderDTO.title);
            mMessageSubTitle.setText(messageHeaderDTO.subTitle);
            mMessageContent.setText(messageHeaderDTO.latestMessage);
            mMessageDate.setText(prettyTime.format(messageHeaderDTO.latestMessageAtUtc));
            mUnreadFlag.setVisibility(messageHeaderDTO.unread ? View.VISIBLE : View.GONE);

            resetMessageIcon();
            if (messageHeaderDTO.imageUrl != null && mMessageIcon != null)
            {
                picasso.load(messageHeaderDTO.imageUrl)
                        .transform(userPhotoTransformation)
                        .into(mMessageIcon, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                resetMessageIcon();
                            }
                        });
            }
        }
    }

    private void resetMessageIcon()
    {
        ImageView iconViewCopy = mMessageIcon;
        if (iconViewCopy != null)
        {
            picasso.cancelRequest(iconViewCopy);
            picasso.load(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(iconViewCopy);
        }
    }

    public void setElementClickedListener(OnElementClickedListener elementClickedListener)
    {
        this.elementClickedListener = elementClickedListener;
    }

    @OnClick({R.id.message_item_icon, R.id.message_item_title})
    protected void handleUserClicked()
    {
        notifyUserClicked();
    }

    protected void notifyUserClicked()
    {
        OnElementClickedListener userClickedListenerCopy = elementClickedListener;
        if (userClickedListenerCopy != null)
        {
            userClickedListenerCopy.onUserClicked(messageHeaderId);
        }
    }

    public static interface OnElementClickedListener
    {
        void onUserClicked(MessageHeaderId messageHeaderId);
    }
}
