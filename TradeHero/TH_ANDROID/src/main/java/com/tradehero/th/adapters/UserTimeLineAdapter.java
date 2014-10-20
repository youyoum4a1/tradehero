package com.tradehero.th.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
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
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class UserTimeLineAdapter extends TimeLineBaseAdapter
{
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    private MiddleCallback<DiscussionDTO> voteCallback;

    @Inject Picasso picasso;
    private List<TimelineItemDTO> listData;

    private List<UserProfileCompactDTO> users = new ArrayList<>();
    private List<SecurityCompactDTO> securities = new ArrayList<>();
    private List<TimelineItemDTO> enhancedItems = new ArrayList<>();
    private List<DiscussionDTO> comments = new ArrayList<>();
    private List<TradeDTO> trades = new ArrayList<>();

    public boolean isShowHeadAndName = false;

    public boolean isMySelf = false;

    public UserTimeLineAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public UserTimeLineAdapter(Context context, boolean isMySelf)
    {
        this(context);
        this.isMySelf = isMySelf;
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
        if(timelineDTO.getComments()!=null)
        {
            comments.addAll(timelineDTO.getComments());
        }
        if(timelineDTO.getTrades()!=null)
        {
            trades.addAll(timelineDTO.getTrades());
        }
        parseEnhancedItemsUserInfo();
    }

    public void parseEnhancedItemsUserInfo()
    {
        for (int i = 0; i < enhancedItems.size(); i++)
        {
            int userid = enhancedItems.get(i).userId;
            enhancedItems.get(i).setUser(getUserFromUsers(userid));
        }
    }

    public UserProfileCompactDTO getUserFromUsers(int userId)
    {
        for (int i = 0; i < users.size(); i++)
        {
            if (users.get(i).id == userId)
            {
                return users.get(i);
            }
        }
        return null;
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
        if (enhancedItems != null && enhancedItems.size() > 0)
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

    public String getItemString(int i)
    {
        String strShare = "";
        TimelineItemDTO dto = enhancedItems.get(i);
        if (dto != null)
        {
            strShare = dto.text;
            boolean isTrade = dto.hasTrader();
            if (isTrade)
            {
                TradeDTO tradeDTO = getTradeDTO(dto.tradeId);
                if (tradeDTO != null)
                {
                    String securityName = dto.getMedias().get(0).displaySecurityName();
                    StringBuffer sb = new StringBuffer();
                    sb.append("我以 ")
                            .append(tradeDTO.getUnitPriceCurrency())
                            .append(" 每股的价格，购买了 ")
                            .append(tradeDTO.displayTradeQuantity())
                            .append(" 股 ")
                            .append(
                                    securityName);
                    return sb.toString();
                }
            }
        }

        return strShare;
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

                holder.imgUserTLUserHeader = (ImageView) convertView.findViewById(R.id.imgUserTLUserHeader);
                holder.tvUserTLName = (TextView) convertView.findViewById(R.id.tvUserTLName);
                holder.tvUserTLTimeStamp2 = (TextView) convertView.findViewById(R.id.tvUserTLTimeStamp2);

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

            holder.llTLShare.setVisibility((!isMySelf && isTrade) ? View.GONE : View.VISIBLE);
            holder.rlUserTLTrade.setVisibility(isTrade ? View.VISIBLE : View.GONE);
            holder.llUserTLNoTrade.setVisibility(isTrade ? View.GONE : View.VISIBLE);
            holder.tvUserTLTimeStamp.setVisibility(isShowHeadAndName ? View.GONE : View.VISIBLE);
            holder.tvUserTLTimeStamp2.setVisibility(isShowHeadAndName ? View.VISIBLE : View.GONE);
            holder.tvUserTLName.setVisibility(isShowHeadAndName ? View.VISIBLE : View.GONE);
            holder.imgUserTLUserHeader.setVisibility(isShowHeadAndName ? View.VISIBLE : View.GONE);

            if (item.createdAtUtc != null)
            {
                holder.tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(item.createdAtUtc));
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
                    holder.tvTradePrice.setText(tradeDTO.getCurrencyDisplay() + tradeDTO.getUnitPriceCurrency());
                    holder.tvTradeCount.setText(tradeDTO.displayTradeQuantity());
                    holder.tvTradeMoney.setText(tradeDTO.getCurrencyDisplay() + tradeDTO.displayTradeMoney());
                    holder.tvTradeCost.setText(tradeDTO.getCurrencyDisplay() + tradeDTO.transactionCost);
                    holder.title0.setText(tradeDTO.isBuy() ? "买入股票：" : "卖出股票：");
                    holder.title1.setText(tradeDTO.isBuy() ? "买入价格：" : "卖出价格：");
                    holder.title2.setText(tradeDTO.isBuy() ? "买入数量：" : "卖出数量：");
                }
            }
            else
            {
                holder.tvUserTLContent.setText("" + item.text);
            }

            if (isShowHeadAndName)
            {
                if (item.getUser() != null)
                {
                    holder.tvUserTLName.setText(item.getUser().displayName);
                    picasso.load(item.getUser().picture)
                            .placeholder(R.drawable.superman_facebook)
                            .error(R.drawable.superman_facebook)
                            .into(holder.imgUserTLUserHeader);

                    holder.tvUserTLName.setOnClickListener(new View.OnClickListener()
                    {
                        @Override public void onClick(View view)
                        {
                            openUserProfile(item.getUser().id);
                        }
                    });

                    holder.imgUserTLUserHeader.setOnClickListener(new View.OnClickListener()
                    {
                        @Override public void onClick(View view)
                        {
                            openUserProfile(item.getUser().id);
                        }
                    });
                }
                holder.tvUserTLTimeStamp2.setText(prettyTime.get().formatUnrounded(item.createdAtUtc));
            }

            holder.tvTLPraise.setText(item.getVoteString());
            holder.tvTLComment.setText("" + item.commentCount);

            holder.llItemAll.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLineItemClicked(position);
                }
            });
            holder.tvUserTLContent.setOnClickListener(new View.OnClickListener()
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
                    clickedPraise(position);
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
        if (getNavigator() != null)
        {
            getNavigator().pushFragment(SecurityDetailFragment.class, bundle);
        }
        else
        {
            gotoDashboard(SecurityDetailFragment.class.getName(), bundle);
        }
    }

    private void openUserProfile(int userId)
    {
        if (userId >= 0)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
            if (getNavigator() != null)
            {
                getNavigator().pushFragment(UserMainPage.class, bundle);
            }
            else
            {
                gotoDashboard(UserMainPage.class.getName(), bundle);
            }
        }
    }

    public void gotoDashboard(String strFragment, Bundle bundle)
    {
        Bundle args = new Bundle();
        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, strFragment);
        args.putAll(bundle);
        ActivityHelper.launchDashboard((Activity) this.context, args);
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

        public ImageView imgUserTLUserHeader = null;
        public TextView tvUserTLTimeStamp2 = null;
        public TextView tvUserTLName = null;

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

    public void clickedPraise(int position)
    {
        TimelineItemDTO item = (TimelineItemDTO) getItem(position);

        updateVoting((item.voteDirection == 0) ? VoteDirection.UpVote : VoteDirection.UnVote, item);

        if (item.voteDirection == 0)
        {
            item.voteDirection = 1;
            item.upvoteCount += 1;
        }
        else
        {
            item.voteDirection = 0;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
        }
        notifyDataSetChanged();
    }

    protected void detachVoteMiddleCallback()
    {
        if (voteCallback != null)
        {
            voteCallback.setPrimaryCallback(null);
        }
        voteCallback = null;
    }

    private void updateVoting(VoteDirection voteDirection, AbstractDiscussionCompactDTO discussionDTO)
    {
        if (discussionDTO == null)
        {
            return;
        }
        DiscussionType discussionType = getDiscussionType(discussionDTO);

        DiscussionVoteKey discussionVoteKey = new DiscussionVoteKey(
                discussionType,
                discussionDTO.id,
                voteDirection);
        detachVoteMiddleCallback();
        voteCallback = discussionServiceWrapper.get().vote(discussionVoteKey, new VoteCallback(voteDirection));
    }

    private DiscussionType getDiscussionType(AbstractDiscussionCompactDTO discussionDTO)
    {
        if (discussionDTO != null && discussionDTO.getDiscussionKey() != null)
        {
            return discussionDTO.getDiscussionKey().getType();
        }

        throw new IllegalStateException("Unknown discussion type");
    }

    protected class VoteCallback implements Callback<DiscussionDTO>
    {
        //<editor-fold desc="Constructors">
        public VoteCallback(VoteDirection voteDirection)
        {
        }
        //</editor-fold>

        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            Timber.d("VoteCallback success");
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.d("VoteCallback failed :" + error.toString());
        }
    }
}
