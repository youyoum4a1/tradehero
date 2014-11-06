package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.StringUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class LeaderboardListAdapter extends BaseAdapter
{

    @Inject Lazy<Picasso> picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    private Context context;
    private LayoutInflater inflater;
    private LeaderboardUserDTOList leaderboardUserDTOs;

    public int leaderboardType = LeaderboardDefKeyKnowledge.DAYS_30;

    public static final int RANK_RES[] = {
            R.drawable.icon_rank_first_place,
            R.drawable.icon_rank_second_place,
            R.drawable.icon_rank_third_place
    };

    public LeaderboardListAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListData(LeaderboardUserDTOList list)
    {
        this.leaderboardUserDTOs = list;
    }

    public void setLeaderboardType(int leaderboardType)
    {
        this.leaderboardType = leaderboardType;
    }

    public void addItems(LeaderboardUserDTOList listAdd)
    {
        this.leaderboardUserDTOs.addAll(listAdd);
    }

    @Override public int getCount()
    {
        return leaderboardUserDTOs == null ? 0 : leaderboardUserDTOs.size();
    }

    @Override public Object getItem(int i)
    {
        return leaderboardUserDTOs == null ? null : leaderboardUserDTOs.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        LeaderboardUserDTO item = (LeaderboardUserDTO) getItem(position);
        if (item != null)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                if (leaderboardType == LeaderboardDefKeyKnowledge.COMPETITION ||leaderboardType == LeaderboardDefKeyKnowledge.COMPETITION_FOR_SCHOOL)
                {
                    convertView = inflater.inflate(R.layout.leaderboard_user_list_item_for_shool, viewGroup, false);
                }
                else
                {
                    convertView = inflater.inflate(R.layout.leaderboard_user_list_item, viewGroup, false);
                }

                holder = new ViewHolder();
                holder.tvUserRank = (TextView) convertView.findViewById(R.id.tvUserRank);
                holder.imgUserHead = (ImageView) convertView.findViewById(R.id.imgUserHead);
                holder.imgUserName = (TextView) convertView.findViewById(R.id.tvUserName);
                holder.tvUserExtraTitle = (TextView) convertView.findViewById(R.id.tvUserExtraTitle);
                holder.tvUserExtraValue = (TextView) convertView.findViewById(R.id.tvUserExtraValue);
                if (leaderboardType == LeaderboardDefKeyKnowledge.COMPETITION || leaderboardType == LeaderboardDefKeyKnowledge.COMPETITION_FOR_SCHOOL)
                {
                    holder.tvSchool = (TextView) convertView.findViewById(R.id.tvSchool);
                }
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position < 3)
            {
                holder.tvUserRank.setText("");
                holder.tvUserRank.setBackgroundResource(RANK_RES[position]);
            }
            else
            {
                holder.tvUserRank.setBackgroundDrawable(null);
                holder.tvUserRank.setText(String.valueOf(position + 1));
            }

            picasso.get()
                    .load(item.picture)
                    .placeholder(R.drawable.superman_facebook)
                    .error(R.drawable.superman_facebook)
                    .into(holder.imgUserHead);

            holder.imgUserName.setText(item.getDisplayName());

            if (leaderboardType == LeaderboardDefKeyKnowledge.DAYS_ROI)
            {//显示 PerROI
                //推荐榜
                holder.tvUserExtraTitle.setText(context.getString(R.string.user_tatal_roi_day_30));
                THSignedNumber roi = THSignedPercentage.builder(item.perfRoi * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                holder.tvUserExtraValue.setText(roi.toString());
                holder.tvUserExtraValue.setTextColor(context.getResources().getColor(roi.getColorResId()));
            }
            else if (leaderboardType == LeaderboardDefKeyKnowledge.POPULAR)
            {//显示 粉丝数
                //人气榜
                holder.tvUserExtraTitle.setText(context.getString(R.string.user_tatal_fans));
                holder.tvUserExtraValue.setText(String.valueOf(item.followerCount));
            }
            else if (leaderboardType == LeaderboardDefKeyKnowledge.WEALTH)
            {//显示 总资产
                //土豪榜
                holder.tvUserExtraTitle.setText(context.getString(R.string.user_tatal_wealth));
                holder.tvUserExtraValue.setText(NumberDisplayUtils.getString(item.totalWealth));
            }
            else
            {//显示 ROI
                holder.tvUserExtraTitle.setText(context.getString(R.string.user_tatal_roi_day_30));
                THSignedNumber roi = THSignedPercentage.builder(item.roiInPeriod * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                holder.tvUserExtraValue.setText(roi.toString());
                holder.tvUserExtraValue.setTextColor(context.getResources().getColor(roi.getColorResId()));
            }

            if (leaderboardType == LeaderboardDefKeyKnowledge.COMPETITION || leaderboardType == LeaderboardDefKeyKnowledge.COMPETITION_FOR_SCHOOL)
            {
                if (StringUtils.isNullOrEmpty(item.school) || leaderboardType == LeaderboardDefKeyKnowledge.COMPETITION)
                {
                    holder.tvSchool.setVisibility(View.GONE);
                }
                else
                {
                    holder.tvSchool.setText(item.school);
                    holder.tvSchool.setVisibility(View.VISIBLE);
                }
                holder.tvUserExtraTitle.setText("");
            }
        }
        return convertView;
    }

    static class ViewHolder
    {
        //public ImageView localImageView = null;
        public TextView tvUserRank = null;
        public ImageView imgUserHead = null;
        public TextView imgUserName = null;
        public TextView tvUserExtraTitle = null;
        public TextView tvUserExtraValue = null;
        public TextView tvSchool = null;
    }
}
