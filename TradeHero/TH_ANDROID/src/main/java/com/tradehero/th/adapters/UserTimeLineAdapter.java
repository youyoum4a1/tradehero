package com.tradehero.th.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th2.R;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class UserTimeLineAdapter extends TimeLineBaseAdapter
{

    //public TimeLineOperater timeLineOperater;
    //@Inject protected Lazy<PrettyTime> prettyTime;
    //private Context context;
    //private LayoutInflater inflater;

    private List<TimelineItemDTO> listData;

    private List<UserProfileCompactDTO> users = new ArrayList<>();
    private List<SecurityCompactDTO> securities = new ArrayList<>();
    private List<TimelineItemDTO> enhancedItems = new ArrayList<>();
    private List<DiscussionDTO> comments = new ArrayList<>();
    private List<TradeDTO> trades = new ArrayList<>();

    public UserTimeLineAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListData(TimelineDTO timelineDTO)
    {
        parseTimeLineDTO(timelineDTO);
    }

    public void setTimeLineOperater(TimeLineOperater timeLineOperater)
    {
        this.timeLineOperater = timeLineOperater;
    }

    public void addItems(TimelineDTO timelineDTO)
    {
        users.addAll(timelineDTO.getUsers());
        securities.addAll(timelineDTO.getSecurities());
        enhancedItems.addAll(timelineDTO.getEnhancedItems());
        comments.addAll(timelineDTO.getComments());
        trades.addAll(timelineDTO.getTrades());
    }

    public void parseTimeLineDTO(TimelineDTO timelineDTO)
    {
        clearAll();
        addItems(timelineDTO);
    }

    public void clearAll()
    {
        users.clear();
        securities.clear();
        enhancedItems.clear();
        enhancedItems.clear();
        trades.clear();
    }

    public int getMaxID()
    {
        if (enhancedItems != null)
        {
            return enhancedItems.get(enhancedItems.size() - 1).id - 1;//为了解决返回本条ID的bug
        }
        return -1;
    }

    @Override public int getCount()
    {
        return enhancedItems == null ? 0 : enhancedItems.size();
    }

    @Override public Object getItem(int i)
    {
        return enhancedItems == null ? null : enhancedItems.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public View getView(final int position, View convertView, ViewGroup viewGroup)
    {
        final TimelineItemDTO item = (TimelineItemDTO) getItem(position);
        if (item != null)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.user_time_line_item, viewGroup, false);

                holder = new ViewHolder();

                holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItemAll);

                holder.tvUserTLTimeStamp = (TextView) convertView.findViewById(R.id.tvUserTLTimeStamp);

                //不是股票交易
                holder.llUserTLNoTrade = (LinearLayout) convertView.findViewById(R.id.llUserTLNoTrade);
                holder.tvUserTLContent = (TextView) convertView.findViewById(R.id.tvUserTLContent);

                //是股票交易
                holder.rlUserTLTrade = (RelativeLayout) convertView.findViewById(R.id.rlUserTLTrade);
                holder.tvTradeName = (TextView) convertView.findViewById(R.id.tvTradeName);
                holder.tvTradePrice = (TextView) convertView.findViewById(R.id.tvTradePrice);
                holder.tvTradeCount = (TextView) convertView.findViewById(R.id.tvTradeCount);
                holder.tvTradeMoney = (TextView) convertView.findViewById(R.id.tvTradeMoney);
                holder.tvTradeCost = (TextView) convertView.findViewById(R.id.tvTradeCost);
                holder.title0 = (TextView) convertView.findViewById(R.id.title0);
                holder.title1 = (TextView) convertView.findViewById(R.id.title1);
                holder.title2 = (TextView) convertView.findViewById(R.id.title2);

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

            boolean isTrade = item.hasTrader();

            holder.rlUserTLTrade.setVisibility(isTrade ? View.VISIBLE : View.GONE);
            holder.llUserTLNoTrade.setVisibility(isTrade ? View.GONE : View.VISIBLE);

            if (item.userViewedAtUtc != null)
            {
                holder.tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(item.userViewedAtUtc));
            }
            if (isTrade)
            {
                try
                {
                    holder.tvTradeName.setText(item.getMedias().get(0).displaySecurityName());
                    holder.tvTradeName.setOnClickListener(new View.OnClickListener()
                    {
                        @Override public void onClick(View view)
                        {
                            openSecurityProfile(item.getMedias().get(0).createSecurityId());
                        }
                    });
                } catch (Exception e)
                {
                    Timber.d(e.getMessage());
                }

                TradeDTO tradeDTO = getTradeDTO(item.tradeId);
                if (tradeDTO != null)
                {
                    holder.tvTradePrice.setText("" + tradeDTO.unitPriceRefCcy);
                    holder.tvTradeCount.setText(tradeDTO.displayTradeQuantity());
                    holder.tvTradeMoney.setText(tradeDTO.displayTradeMoney());
                    holder.tvTradeCost.setText("" + tradeDTO.transactionCost);
                    holder.title0.setText(tradeDTO.isBuy() ? "买入股票：" : "卖出股票：");
                    holder.title1.setText(tradeDTO.isBuy() ? "买入价格：" : "卖出价格：");
                    holder.title2.setText(tradeDTO.isBuy() ? "买入数量：" : "卖出数量：");
                }
            }
            else
            {
                holder.tvUserTLContent.setText("" + item.text);
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

    private DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) context).getDashboardNavigator();
    }

    private void openSecurityProfile(SecurityId securityId)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityId.getDisplayName());
        if(getNavigator()!=null)
        {
            getNavigator().pushFragment(SecurityDetailFragment.class, bundle);
        }
        else
        {
            gotoDashboard(SecurityDetailFragment.class.getName(), bundle);
        }
    }

    public void gotoDashboard(String strFragment, Bundle bundle)
    {
        Bundle args = new Bundle();
        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, strFragment);
        args.putAll(bundle);
        ActivityHelper.launchDashboard((Activity)this.context, args);
    }

    public TradeDTO getTradeDTO(int tradeId)
    {
        for (TradeDTO dto : trades)
        {
            if (dto.id == tradeId)
            {
                return dto;
            }
        }
        return null;
    }

    public SecurityCompactDTO getSecurityDTO(int securityId)
    {
        for (SecurityCompactDTO dto : securities)
        {
            if (dto.id == securityId)
            {
                return dto;
            }
        }
        return null;
    }

    static class ViewHolder
    {
        public LinearLayout llItemAll = null;

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

        //是一个交易相关的
        public RelativeLayout rlUserTLTrade;
        public TextView tvTradeName;
        public TextView tvTradePrice;
        public TextView tvTradeCount;
        public TextView tvTradeMoney;
        public TextView tvTradeCost;
        public TextView title0;//买入股票
        public TextView title1;//买入价格
        public TextView title2;//买入数量
    }


}
