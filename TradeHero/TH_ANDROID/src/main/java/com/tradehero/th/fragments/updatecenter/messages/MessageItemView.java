package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.MessageDTO;
import com.tradehero.th.api.discussion.key.MessageId;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.message.MessageItemCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by wangliang on 14-4-4.
 */
public class MessageItemView extends LinearLayout implements DTOView<MessageId>
{
    @Inject MessageItemCache messageItemCache;
    @Inject Picasso picasso;
    @Inject PrettyTime prettyTime;

    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    @InjectView(R.id.message_item_icon) ImageView iconView;
    @InjectView(R.id.message_item_title) TextView titleView;
    @InjectView(R.id.message_item_date) TextView dateView;

    private MessageDTO messageDTO;

    public MessageItemView(Context context)
    {
        super(context);
    }

    public MessageItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override public void display(MessageId dto)
    {
        this.messageDTO = messageItemCache.get(dto);
        displayData();
    }

    private void displayData()
    {
        if (messageDTO != null)
        {
            titleView.setText(messageDTO.title);

            dateView.setText(prettyTime.format(messageDTO.createdAtUtc));
            if (messageDTO.imageUrl != null)
            {
                picasso.load(messageDTO.imageUrl).transform(userPhotoTransformation).into(iconView);
            }
            else
            {
                //TODO
                //iconView.setImageResource();
            }
        }
    }
}
