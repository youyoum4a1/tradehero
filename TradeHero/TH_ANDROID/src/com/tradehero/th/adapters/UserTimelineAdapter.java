package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fedorvlasov.lazylist.ImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;

public class UserTimelineAdapter extends BaseAdapter
{
    private final TimelineDTO timelineDTO;

    private final UserProfileDTO profile;
    private final LayoutInflater inflater;
    private final ImageLoader mLoader;

    public UserTimelineAdapter(Context context, TimelineDTO timelineDTO)
    {
        this.timelineDTO = timelineDTO;
        this.inflater = LayoutInflater.from(context);
        this.profile = THUser.getCurrentUser();
        mLoader = new ImageLoader(context);
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
            convertView = inflater.inflate(R.layout.profile_item_list_screen, parent);
            holder = new ViewHolder();
            holder.txt_username = (TextView) convertView.findViewById(R.id.txt_user_name);
            holder.txt_usercontent =
                    (TextView) convertView.findViewById(R.id.txt_user_content_name);
            holder.user_img = (ImageView) convertView.findViewById(R.id.img_user);
            holder.vendr_image = (ImageView) convertView.findViewById(R.id.img_vender);
            //holder.txt_code = (TextView) convertView.findViewById(R.id.txt_dlrcode);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt_username.setText(profile.displayName);
        holder.txt_usercontent.setText(timelineDTO.enhancedItems.get(position).text);
        mLoader.displayImage(profile.picture, holder.user_img);

        // TODO uncomment next line
        //mLoader.DisplayRoundImage(profile.picture, holder.user_img);
        /*if(tradeofweeklist.get(position).getMedias().getUrl()!=null)
		{
			mLoader.DisplayImage( tradeofweeklist.get(position).getMedias().getUrl(), holder.vendr_image);
		}
		*/
        return convertView;
    }

    static class ViewHolder
    {
        TextView txt_username;
        TextView txt_usercontent;
        TextView txt_time;
        ImageView user_img;
        ImageView vendr_image;
    }
}
