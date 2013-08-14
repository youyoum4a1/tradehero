package com.tradehero.th.adapters;

import com.tradehero.th.models.TradeOfWeek;
import java.util.ArrayList;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.application.App;
import com.tradehero.th.cache.ImageLoader;
import com.tradehero.th.models.ProfileDTO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TradeWeekAdapter extends BaseAdapter
{

    private static ArrayList<TradeOfWeek> tradeofweeklist;

    Context ctx;
    ProfileDTO mprofile;
    private LayoutInflater l_Inflater;
    ImageLoader mLoader;

    public TradeWeekAdapter(Context context, ArrayList<TradeOfWeek> results)
    {
        tradeofweeklist = results;
        l_Inflater = LayoutInflater.from(context);
        ctx = context;
        mprofile = ((App) ctx.getApplicationContext()).getProfileDTO();
        mLoader = new ImageLoader(ctx);
    }

    public int getCount()
    {
        return tradeofweeklist.size();
    }

    public Object getItem(int position)
    {
        return tradeofweeklist.get(position);
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

        holder.txt_username.setText(mprofile.getDisplayName());
        holder.txt_usercontent.setText(tradeofweeklist.get(position).getText());
        mLoader.DisplayRoundImage(mprofile.getPicture(), holder.user_img);
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
