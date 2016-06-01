package com.ayondo.academy.fragments.updatecenter.messageNew;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.widget.swipe.adapters.BaseSwipeAdapter;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.MessageHeaderDTO;
import java.util.ArrayList;
import java.util.List;
import org.ocpsoft.prettytime.PrettyTime;

public class MessageListViewAdapter extends BaseSwipeAdapter
{
    @NonNull private Picasso picasso;
    @NonNull private PrettyTime prettyTime;
    @NonNull private Context mContext;
    @NonNull private Transformation userPhotoTransformation;
    @Nullable private OnMessageItemClicked onMessageItemClicked;

    private List<MessageHeaderDTO> messageHeaderDTOs;

    //<editor-fold desc="Constructors">
    public MessageListViewAdapter(
            @NonNull Context mContext,
            @NonNull PrettyTime prettyTime,
            @NonNull Picasso picasso,
            @NonNull Transformation userPhotoTransformation)
    {
        this.mContext = mContext;
        this.picasso = picasso;
        this.prettyTime = prettyTime;
        this.userPhotoTransformation = userPhotoTransformation;
        messageHeaderDTOs = new ArrayList<>();
    }
    //</editor-fold>

    public void setOnMessageItemClicked(@Nullable OnMessageItemClicked messageItemClicked)
    {
        this.onMessageItemClicked = messageItemClicked;
    }

    @Override
    public int getSwipeLayoutResourceId(int position)
    {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent)
    {
        return LayoutInflater.from(mContext).inflate(R.layout.message_center_listview_item, null);
    }

    @Override
    public void fillValues(final int position, View convertView)
    {
        final MessageHeaderDTO messageHeaderDTO = getItem(position);
        if (messageHeaderDTO != null)
        {
            final ImageView imgIcon = (ImageView) convertView.findViewById(R.id.message_item_icon);
            final TextView tvDelete = (TextView) convertView.findViewById(R.id.delete);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.message_item_title);
            TextView tvSubTitle = (TextView) convertView.findViewById(R.id.message_item_sub_title);
            TextView tvContent = (TextView) convertView.findViewById(R.id.message_item_content);
            TextView tvDate = (TextView) convertView.findViewById(R.id.message_item_date);
            ImageView imgUnread = (ImageView) convertView.findViewById(R.id.message_unread_flag);

            tvTitle.setText(messageHeaderDTO.title);
            tvSubTitle.setText(messageHeaderDTO.subTitle);
            tvContent.setText(messageHeaderDTO.latestMessage);
            tvDate.setText(prettyTime.format(messageHeaderDTO.latestMessageAtUtc));
            imgUnread.setVisibility(messageHeaderDTO.unread ? View.VISIBLE : View.GONE);


            picasso.load(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(imgIcon);


            if (messageHeaderDTO.imageUrl != null && imgIcon != null)
            {

                picasso.load(messageHeaderDTO.imageUrl)
                        .transform(userPhotoTransformation)
                        .into(imgIcon, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                picasso.cancelRequest(imgIcon);
                                picasso.load(R.drawable.superman_facebook)
                                        .transform(userPhotoTransformation)
                                        .into(imgIcon);
                            }
                        });
            }

            tvDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    if (onMessageItemClicked != null)
                    {
                        onMessageItemClicked.clickedItemDelete(position);
                    }
                }
            });

            if (imgIcon != null)
            {
                imgIcon.setOnClickListener(new View.OnClickListener()
                {
                    @Override public void onClick(View v)
                    {
                        if (onMessageItemClicked != null)
                        {
                            onMessageItemClicked.clickedItemUser(position);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getCount()
    {
        return messageHeaderDTOs == null ? 0 : messageHeaderDTOs.size();
    }

    @Override
    public MessageHeaderDTO getItem(int position)
    {
        if (messageHeaderDTOs != null && position < messageHeaderDTOs.size())
        {
            return messageHeaderDTOs.get(position);
        }
        else
        {
            return null;
        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void appendTail(List<MessageHeaderDTO> messageHeaderDTOs)
    {
        this.messageHeaderDTOs.addAll(messageHeaderDTOs);
    }

    public void setListData(List<MessageHeaderDTO> messageHeaderDTOs)
    {
        this.messageHeaderDTOs.clear();
        this.messageHeaderDTOs.addAll(messageHeaderDTOs);
    }

    public void resetListData()
    {
        this.messageHeaderDTOs.clear();
    }

    public void remove(MessageHeaderDTO messageHeaderDTO)
    {
        this.messageHeaderDTOs.remove(messageHeaderDTO);
    }

    public interface OnMessageItemClicked
    {
        void clickedItemUser(int position);

        void clickedItemDelete(int position);
    }

}
