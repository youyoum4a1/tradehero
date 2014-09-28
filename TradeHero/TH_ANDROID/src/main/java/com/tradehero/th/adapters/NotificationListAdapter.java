package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.ArrayList;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class NotificationListAdapter extends BaseAdapter
{
    @Inject Lazy<Picasso> picasso;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NotificationDTO> dataList;
    @Inject Lazy<PrettyTime> prettyTime;

    private NotificationClickListener listener;

    public NotificationListAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.notification_list_item, viewGroup, false);
                holder = new ViewHolder();
                holder.rlNotificationItem = (RelativeLayout) convertView.findViewById(R.id.rlNotificationItem);
                holder.imgNotificationHeader = (ImageView) convertView.findViewById(R.id.imgNotificationHeader);
                holder.tvNotificationTimer = (TextView) convertView.findViewById(R.id.tvNotificationTimer);
                holder.imgNotificationIsRead = (ImageView) convertView.findViewById(R.id.imgNotificationIsRead);
                holder.tvNotificationContent = (TextView) convertView.findViewById(R.id.tvNotificationContent);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            picasso.get()
                    .load(item.imageUrl)
                    .placeholder(R.drawable.superman_facebook)
                    .error(R.drawable.superman_facebook)
                    .into(holder.imgNotificationHeader);

            holder.tvNotificationTimer.setText(prettyTime.get().formatUnrounded(item.createdAtUtc));
            holder.tvNotificationContent.setText(item.text);
            holder.imgNotificationIsRead.setVisibility(item.unread ? View.VISIBLE : View.GONE);

            holder.tvNotificationContent.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    listener.OnNotificationItemClicked(position);
                }
            });
            holder.rlNotificationItem.setOnClickListener(new View.OnClickListener()
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
        public RelativeLayout rlNotificationItem = null;
        public ImageView imgNotificationHeader = null;
        public TextView tvNotificationTimer = null;
        public ImageView imgNotificationIsRead = null;
        public TextView tvNotificationContent = null;
    }

    public static interface NotificationClickListener
    {
        void OnNotificationItemClicked(int position);
    }
}
