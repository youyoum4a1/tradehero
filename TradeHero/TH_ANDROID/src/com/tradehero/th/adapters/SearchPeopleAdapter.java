/**
 * SearchPeopleAdapter.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 10, 2013
 */
package com.tradehero.th.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import com.tradehero.th.R;
import com.tradehero.th.application.CircularImageView;
import com.tradehero.th.cache.ImageLoader;
import com.tradehero.th.models.User;
import com.tradehero.th.utills.DateUtils;
import com.tradehero.th.utills.YUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class SearchPeopleAdapter extends ArrayAdapter<User>
{

    //private final static String TAG = SearchPeopleAdapter.class.getSimpleName();
    private ImageLoader mImageLoader;

    public SearchPeopleAdapter(Context context, List<User> userList)
    {
        super(context, 0, userList);
        mImageLoader = new ImageLoader(getContext());
    }

    public static class ViewHolder
    {

        TextView userName;
        TextView profitIndicator;
        TextView stockPercentage;
        TextView date;
        CircularImageView userImage;
        SmartImageView peopleBgImage;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.search_people_item, null);

            holder = new ViewHolder();

            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.profitIndicator = (TextView) convertView.findViewById(R.id.profit_indicator);
            holder.stockPercentage = (TextView) convertView.findViewById(R.id.stock_percentage);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.userImage = (CircularImageView) convertView.findViewById(R.id.user_image);
            holder.peopleBgImage = (SmartImageView) convertView.findViewById(R.id.people_bg_image);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = getItem(position);

        holder.userName.setText(user.getUserthDisplayName());

        if (user.getUserMarkingAsOfUtc() != null && user.getUserMarkingAsOfUtc().length() > 0)
        {
            holder.date.setText(DateUtils.getFormatedTrendDate(user.getUserMarkingAsOfUtc()));
            holder.date.setTextColor(Color.BLACK);
        }
        else
        {
            holder.date.setText("N/A");
            holder.date.setTextColor(Color.GRAY);
        }

        if (user.getUserRoiSinceInception() != null && user.getUserRoiSinceInception().length() > 0)
        {
            double roi = YUtils.parseQuoteValue(user.getUserRoiSinceInception());
            if (!Double.isNaN(roi))
            {
                holder.profitIndicator.setVisibility(View.VISIBLE);
                roi = roi * 100;

                if (roi >= 1)
                {
                    holder.profitIndicator
                            .setText(getContext().getString(R.string.positive_prefix));
                    holder.profitIndicator.setTextColor(Color.GREEN);
                    holder.stockPercentage.setText(String.format("%.2f", roi) + "%");
                    holder.stockPercentage.setTextColor(Color.GREEN);
                }
                else
                {
                    holder.profitIndicator
                            .setText(getContext().getString(R.string.negetive_prefix));
                    holder.profitIndicator.setTextColor(Color.RED);
                    roi = Math.abs(roi);
                    holder.stockPercentage.setText(String.format("%.2f", roi) + "%");
                    holder.stockPercentage.setTextColor(Color.RED);
                }
            }
            else
            {
                holder.profitIndicator.setVisibility(View.GONE);
                holder.stockPercentage.setText("N/A");
                holder.stockPercentage.setTextColor(Color.RED);
            }
        }
        else
        {
            holder.profitIndicator.setVisibility(View.GONE);
            holder.stockPercentage.setText("N/A");
            holder.stockPercentage.setTextColor(Color.RED);
        }

        if (user.getUserPicture() != null && user.getUserPicture().length() > 0)
        {
            mImageLoader.DisplayImage(user.getUserPicture(), holder.userImage);
        }

        return convertView;
    }
}