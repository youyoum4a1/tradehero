package com.tradehero.th.adapters;

import android.view.LayoutInflater;

import android.support.v4.app.FragmentActivity;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.fedorvlasov.lazylist.ImageLoader;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.models.TradeOfWeek;
import java.util.ArrayList;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.application.App;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserTimelineAdapter extends BaseAdapter
{
    private final TimelineDTO timelineDTO;

    UserProfileDTO mprofile;
    private LayoutInflater l_Inflater;
    ImageLoader mLoader;

    public UserTimelineAdapter(Context context, TimelineDTO timelineDTO)
    {
        this.timelineDTO = timelineDTO;
        l_Inflater = LayoutInflater.from(context);
        mprofile = THUser.getCurrentUser();
        mLoader = new ImageLoader(context);
    }

    public int getCount()
    {
        return timelineDTO.enhancedItems.size();
    }

    public Object getItem(int position)
    {
        return timelineDTO.enhancedItems.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }



    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = l_Inflater.inflate(R.layout.profile_item_list_screen, null);
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

        holder.txt_username.setText(mprofile.displayName);
        holder.txt_usercontent.setText(timelineDTO.enhancedItems.get(position).text);
        mLoader.displayImage(mprofile.picture, holder.user_img);

        // TODO uncomment next line
        //mLoader.DisplayRoundImage(mprofile.picture, holder.user_img);
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
