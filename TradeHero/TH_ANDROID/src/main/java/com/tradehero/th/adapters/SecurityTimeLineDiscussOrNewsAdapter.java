package com.tradehero.th.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class SecurityTimeLineDiscussOrNewsAdapter extends TimeLineBaseAdapter
{
    @Inject Lazy<Picasso> picasso;
    private List<AbstractDiscussionCompactDTO> listData;

    public SecurityTimeLineDiscussOrNewsAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListData(List<AbstractDiscussionCompactDTO> listCompactDTO)
    {
        listData = listCompactDTO;
        notifyDataSetChanged();
    }

    public void addListData(List<AbstractDiscussionCompactDTO> listCompactDTO)
    {
        listData.addAll(listCompactDTO);
        notifyDataSetChanged();
    }

    public void setTimeLineOperater(TimeLineOperater timeLineOperater)
    {
        this.timeLineOperater = timeLineOperater;
    }

    @Override public int getCount()
    {
        return listData == null ? 0 : listData.size();
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public AbstractDiscussionCompactDTO getItem(int i)
    {
        return listData.get(i);
    }

    @Override public View getView(final int position, View convertView, ViewGroup viewGroup)
    {
        final AbstractDiscussionCompactDTO item = (AbstractDiscussionCompactDTO) getItem(position);
        if (item != null)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.security_time_line_item, viewGroup, false);
                holder = new ViewHolder();
                holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItemAll);
                holder.tvUserTLTimeStamp = (TextView) convertView.findViewById(R.id.tvUserTLTimeStamp);

                //不是股票交易
                holder.llUserTLNoTrade = (LinearLayout) convertView.findViewById(R.id.llUserTLNoTrade);
                holder.tvUserTLContent = (TextView) convertView.findViewById(R.id.tvUserTLContent);

                holder.imgSecurityTLUserHeader = (ImageView) convertView.findViewById(R.id.imgSecurityTLUserHeader);
                holder.tvUserTLName = (TextView) convertView.findViewById(R.id.tvUserTLName);
                ////是股票交易
                //holder.rlUserTLTrade = (RelativeLayout) convertView.findViewById(R.id.rlUserTLTrade);
                //holder.tvTradeName = (TextView) convertView.findViewById(R.id.tvTradeName);
                //holder.tvTradePrice = (TextView) convertView.findViewById(R.id.tvTradePrice);
                //holder.tvTradeCount = (TextView) convertView.findViewById(R.id.tvTradeCount);
                //holder.tvTradeMoney = (TextView) convertView.findViewById(R.id.tvTradeMoney);
                //holder.tvTradeCost = (TextView) convertView.findViewById(R.id.tvTradeCost);
                //holder.title0 = (TextView) convertView.findViewById(R.id.title0);
                //holder.title1 = (TextView) convertView.findViewById(R.id.title1);
                //holder.title2 = (TextView) convertView.findViewById(R.id.title2);

                //赞，评论，分享
                holder.llTLPraise = (LinearLayout) convertView.findViewById(R.id.llTLPraise);
                holder.llTLComment = (LinearLayout) convertView.findViewById(R.id.llTLComment);
                holder.llTLShare = (LinearLayout) convertView.findViewById(R.id.llTLShare);
                holder.btnTLPraise = (TextView) convertView.findViewById(R.id.btnTLPraise);
                holder.tvTLPraise = (TextView) convertView.findViewById(R.id.tvTLPraise);
                holder.btnTLComment = (TextView) convertView.findViewById(R.id.btnTLComment);
                holder.tvTLComment = (TextView) convertView.findViewById(R.id.tvTLComment);
                holder.btnTLShare = (TextView) convertView.findViewById(R.id.btnTLShare);
                holder.tvTLShare = (TextView) convertView.findViewById(R.id.tvTLShare);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(item.createdAtUtc));

            if (item instanceof NewsItemCompactDTO)
            {
                holder.tvUserTLContent.setText(((NewsItemCompactDTO) item).description);
                holder.imgSecurityTLUserHeader.setVisibility(View.GONE);
                holder.tvUserTLName.setVisibility(View.GONE);
            }
            else if (item instanceof DiscussionDTO)
            {
                holder.tvUserTLContent.setText(((DiscussionDTO) item).text);
                holder.tvUserTLName.setText(((DiscussionDTO) item).user.displayName);
                holder.imgSecurityTLUserHeader.setVisibility(View.VISIBLE);
                picasso.get()
                        .load(((DiscussionDTO) item).user.picture)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(holder.imgSecurityTLUserHeader);
                holder.imgSecurityTLUserHeader.setOnClickListener(new View.OnClickListener()
                {
                    @Override public void onClick(View view)
                    {
                        openUserProfile(((DiscussionDTO) item).user.id);
                    }
                });
            }

            holder.tvTLPraise.setText("" + item.upvoteCount);
            holder.tvTLComment.setText("" + item.commentCount);

            holder.llItemAll.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLineItemClicked(position);
                }
            });
            holder.llTLPraise.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLinePraiseClicked(position);
                }
            });

            holder.llTLComment.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLineCommentsClicked(position);
                }
            });

            holder.llTLShare.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLineShareClied(position);
                }
            });
        }

        return convertView;
    }

    static class ViewHolder
    {
        public LinearLayout llItemAll = null;

        public ImageView imgSecurityTLUserHeader = null;
        public TextView tvUserTLName = null;

        public TextView tvUserTLTimeStamp = null;
        public TextView tvUserTLContent = null;

        //不是一个交易相关
        public LinearLayout llUserTLNoTrade = null;
        public LinearLayout llTLPraise = null;
        public LinearLayout llTLComment = null;
        public LinearLayout llTLShare = null;
        public TextView btnTLPraise = null;
        public TextView tvTLPraise = null;
        public TextView btnTLComment = null;
        public TextView tvTLComment = null;
        public TextView btnTLShare = null;
        public TextView tvTLShare = null;
    }

    private DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) context).getDashboardNavigator();
    }

    private void openUserProfile(int userId)
    {
        if (userId >= 0)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
            getNavigator().pushFragment(UserMainPage.class, bundle);
        }
    }
}
