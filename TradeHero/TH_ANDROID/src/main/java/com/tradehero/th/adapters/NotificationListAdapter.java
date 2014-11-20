package com.tradehero.th.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import org.ocpsoft.prettytime.PrettyTime;

import javax.inject.Inject;
import java.util.ArrayList;

public class NotificationListAdapter extends BaseAdapter
{
    @Inject Lazy<Picasso> picasso;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NotificationDTO> dataList;
    @Inject Lazy<PrettyTime> prettyTime;

    private NotificationClickListener listener;

    private int readColor;
    private int unreadColor;

    public NotificationListAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        readColor = context.getResources().getColor(R.color.notification_item_read);
        unreadColor = context.getResources().getColor(R.color.notification_item_unread);
    }

    public void setNotificationLister(NotificationClickListener listener)
    {
        this.listener = listener;
    }

    public void setListData(ArrayList<NotificationDTO> list)
    {
        this.dataList = list;
        notifyDataSetChanged();
    }

    public void addListData(ArrayList<NotificationDTO> list)
    {
        if (dataList == null) dataList = new ArrayList<>();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void setHasRead(int pushId)
    {
        if (dataList == null) return;
        for (int i = 0; i < dataList.size(); i++)
        {
            if (dataList.get(i).pushId == pushId)
            {
                dataList.get(i).unread = false;
            }
        }

        notifyDataSetChanged();
    }

    public void setAllRead()
    {
        if (dataList == null) return;
        for (int i = 0; i < dataList.size(); i++)
        {
            if (dataList.get(i).unread = false) ;
        }
        notifyDataSetChanged();
    }

    @Override public int getCount()
    {
        return dataList == null ? 0 : dataList.size();
    }

    @Override public Object getItem(int i)
    {
        return dataList == null ? null : dataList.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public View getView(final int position, View convertView, ViewGroup viewGroup)
    {
        NotificationDTO item = (NotificationDTO) getItem(position);
        if (item != null)
        {
            ViewHolder holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.notification_list_item, viewGroup, false);
            holder.llNotificationItem = (LinearLayout) convertView.findViewById(R.id.llNotificationItem);
            holder.imgNotificationHeader = (ImageView) convertView.findViewById(R.id.imgNotificationHeader);
            holder.tvNotificationTimer = (TextView) convertView.findViewById(R.id.tvNotificationTimer);
            holder.tvNotificationContent = (MarkdownTextView) convertView.findViewById(R.id.tvNotificationContent);
            holder.tvNotificationUser = (TextView) convertView.findViewById(R.id.tvNotificationUser);
            convertView.setTag(holder);
            holder = (ViewHolder) convertView.getTag();
            if(item.useSysIcon){
                holder.imgNotificationHeader.setImageResource(R.drawable.offical_logo);
            }else {
                picasso.get()
                        .load(item.imageUrl)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(holder.imgNotificationHeader);
            }
            String text = item.text;
            if(!TextUtils.isEmpty(item.referencedUserName)){
                holder.tvNotificationUser.setText(item.referencedUserName);
                if(!TextUtils.isEmpty(item.text)){
                    text = text.replaceFirst(item.referencedUserName, "");
                }
            }
            holder.tvNotificationTimer.setText(prettyTime.get().formatUnrounded(item.createdAtUtc));
            if(!TextUtils.isEmpty(item.text)){
                if(text.length()>100){
                    text = text.substring(0, 96) + "...";
                }
                holder.tvNotificationContent.setText(text);
            }
            if(item.unread){
                holder.tvNotificationContent.setTextColor(unreadColor);
            }else{
                holder.tvNotificationContent.setTextColor(readColor);
            }

            holder.tvNotificationContent.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    listener.OnNotificationItemClicked(position);
                }
            });
            holder.llNotificationItem.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    listener.OnNotificationItemClicked(position);
                }
            });
        }
        return convertView;
    }

    static class ViewHolder
    {
        public LinearLayout llNotificationItem = null;
        public ImageView imgNotificationHeader = null;
        public TextView tvNotificationTimer = null;
        public MarkdownTextView tvNotificationContent = null;
        public TextView tvNotificationUser = null;
    }

    public static interface NotificationClickListener
    {
        void OnNotificationItemClicked(int position);
    }
}
