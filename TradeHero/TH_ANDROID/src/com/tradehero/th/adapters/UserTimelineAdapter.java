package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fedorvlasov.lazylist.ImageLoader;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.application.App;
import com.tradehero.th.base.THUser;

public class UserTimelineAdapter extends BaseAdapter
{
    private final TimelineDTO timelineDTO;
    private final UserProfileDTO profile;

    public UserTimelineAdapter(Context context, TimelineDTO timelineDTO)
    {
        this.timelineDTO = timelineDTO;
        this.profile = THUser.getCurrentUser();
    }

    @Override
    public int getCount()
    {
        return timelineDTO.enhancedItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return timelineDTO.enhancedItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) App.context().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.profile_item_list_screen, null);

            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.txt_user_name);
            holder.userContent = (TextView) convertView.findViewById(R.id.txt_user_content_name);
            holder.userAvatar = (ImageView) convertView.findViewById(R.id.img_user);
            holder.vendorImage = (ImageView) convertView.findViewById(R.id.img_vender);
            //holder.txt_code = (TextView) convertView.findViewById(R.id.txt_dlrcode);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.username.setText(profile.displayName);
        holder.userContent.setText(timelineDTO.enhancedItems.get(position).text);
        Picasso.with(App.context()).load(profile.picture).into(holder.userAvatar);

        /*if(tradeofweeklist.get(position).getMedias().getUrl()!=null)
		{
			mLoader.DisplayImage( tradeofweeklist.get(position).getMedias().getUrl(), holder.vendorImage);
		}
		*/
        return convertView;
    }

    static class ViewHolder
    {
        TextView username;
        TextView userContent;
        TextView time;
        ImageView userAvatar;
        ImageView vendorImage;
    }
}
