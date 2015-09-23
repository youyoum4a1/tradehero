package com.tradehero.th.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.cache.NoticeNewsCache;
import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryUtils;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.livetrade.DataUtils;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.SecurityOptActivity;
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
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.MarkdownTextView;
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

    private List<UserProfileCompactDTO> users = new ArrayList<>();
    private List<SecurityCompactDTO> securities = new ArrayList<>();
    private List<TimelineItemDTO> enhancedItems = new ArrayList<>();
    private List<DiscussionDTO> comments = new ArrayList<>();
    private List<TradeDTO> trades = new ArrayList<>();

    public boolean isShowHeadAndName = false;//是否显示头像和名字
    public boolean isMySelf = false;
    public boolean isShowFollowBuy = false;

    @Inject Analytics analytics;
    public Animation animation;
    public Animation praiseAnimation;

    private static int timeLineItemDeleted = -1;
    private static int timeLineItemAnswered = -1;

    private String fromWhere = "";

    public UserTimeLineAdapter(Activity activity)
    {
        DaggerUtils.inject(this);
        this.context = activity;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        animation = AnimationUtils.loadAnimation(context, R.anim.vote_ani);
        praiseAnimation = AnimationUtils.loadAnimation(context, R.anim.vote_praise);
    }

    public UserTimeLineAdapter(Activity activity, String fromWhere)
    {
        DaggerUtils.inject(this);
        this.context = activity;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        animation = AnimationUtils.loadAnimation(context, R.anim.vote_ani);
        praiseAnimation = AnimationUtils.loadAnimation(context, R.anim.vote_praise);
        this.fromWhere = fromWhere;
    }

    public UserTimeLineAdapter(Activity activity, boolean isMySelf)
    {
        this(activity);
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
        if(timelineDTO.getUsers()!=null) {
            users.addAll(timelineDTO.getUsers());
        }
        if(timelineDTO.getSecurities()!=null) {
            securities.addAll(timelineDTO.getSecurities());
        }
        if(timelineDTO.getEnhancedItems()!=null) {
            enhancedItems.addAll(timelineDTO.getEnhancedItems());
        }
        if (timelineDTO.getComments() != null)
        {
            comments.addAll(timelineDTO.getComments());
        }
        if (timelineDTO.getTrades() != null)
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
        if(showNoticeNews() && NoticeNewsCache.getInstance().getTimelineDTO()!=null){
            addItems(NoticeNewsCache.getInstance().getTimelineDTO());
        }
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
                if (tradeDTO == null)
                {
                    return "";
                }
                if (tradeDTO.isBuy())
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
                else
                {
                    String securityName = dto.getMedias().get(0).displaySecurityName();
                    StringBuffer sb = new StringBuffer();
                    sb.append("我以 ")
                            .append(tradeDTO.getUnitPriceCurrency())
                            .append(" 每股的价格，卖出了 ")
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
            ViewHolder holder;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.user_time_line_item, viewGroup, false);

                holder = new ViewHolder();

                holder.headerV = convertView.findViewById(R.id.view_header);
                holder.footerV = convertView.findViewById(R.id.view_footer);
                holder.dividerHighLightV = convertView.findViewById(R.id.view_divider_highlight);

                holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItemAll);
                holder.llNormalAll = (LinearLayout) convertView.findViewById(R.id.llNormalAll);
                holder.llSimpleAll = (LinearLayout) convertView.findViewById(R.id.llSimpleAll);

                holder.tvReward = (TextView) convertView.findViewById(R.id.tvIsReward);
                holder.tvEssential = (TextView)convertView.findViewById(R.id.tvIsFavorite);
                holder.tvTop = (TextView)convertView.findViewById(R.id.tvIsTop);
                holder.tvUserTLTimeStamp = (TextView) convertView.findViewById(R.id.tvUserTLTimeStamp);

                //不是股票交易
                holder.llUserTLNoTrade = (LinearLayout) convertView.findViewById(R.id.linearlayout_usertlnotrade);
                holder.tvUserTLTitle = (TextView) convertView.findViewById(R.id.tvUserTLTitle);
                holder.tvUserTLContent = (TextView) convertView.findViewById(R.id.tvUserTLContent);

                holder.imgUserTLUserHeader = (ImageView) convertView.findViewById(R.id.imgUserTLUserHeader);
                holder.tvUserTLName = (TextView) convertView.findViewById(R.id.tvUserTLName);

                //是股票交易
                holder.rlUserTLTrade = (RelativeLayout) convertView.findViewById(R.id.rlUserTLTrade);
                holder.tvTradeName = (TextView) convertView.findViewById(R.id.tvTradeName);
                holder.tvTradePrice = (TextView) convertView.findViewById(R.id.tvTradePrice);
                holder.tvTradeCount = (TextView) convertView.findViewById(R.id.tvTradeCount);
                holder.tvTradeMoney = (TextView) convertView.findViewById(R.id.tvTradeMoney);
                holder.title0 = (TextView) convertView.findViewById(R.id.title0);
                holder.title1 = (TextView) convertView.findViewById(R.id.title1);
                holder.title2 = (TextView) convertView.findViewById(R.id.title2);

                //赞,踩，评论，分享
                holder.llTLPraise = (LinearLayout) convertView.findViewById(R.id.llTLPraise);
                holder.llTLPraiseDown = (LinearLayout) convertView.findViewById(R.id.llTLPraiseDown);
                holder.llTLComment = (LinearLayout) convertView.findViewById(R.id.llTLComment);
                holder.llTLBuy = (LinearLayout) convertView.findViewById(R.id.llTLBuy);
                holder.btnTLBuy = (TextView) convertView.findViewById(R.id.btnTLBuy);
                holder.tvTLBuy = (TextView) convertView.findViewById(R.id.tvTLBuy);
                holder.btnTLPraise = (TextView) convertView.findViewById(R.id.btnTLPraise);
                holder.tvTLPraise = (TextView) convertView.findViewById(R.id.tvTLPraise);
                holder.btnTLPraiseDown = (TextView) convertView.findViewById(R.id.btnTLPraiseDown);
                holder.tvTLPraiseDown = (TextView) convertView.findViewById(R.id.tvTLPraiseDown);
                holder.btnTLComment = (TextView) convertView.findViewById(R.id.btnTLComment);
                holder.tvTLComment = (TextView) convertView.findViewById(R.id.tvTLComment);


                holder.tvTipInTopSimple = (TextView) convertView.findViewById(R.id.tvTipInTopSimple);
                holder.tvUserTLTitleSimple = (TextView) convertView.findViewById(R.id.tvUserTLTitleSimple);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            final ViewHolder copyHolder = holder;

            boolean isTrade = item.hasTrader();

            //item.isHighlight
            holder.llNormalAll.setVisibility(item.isNotice?View.GONE:View.VISIBLE);
            holder.llSimpleAll.setVisibility(item.isNotice?View.VISIBLE:View.GONE);

            holder.tvReward.setVisibility(item.isQuestionItem ? View.VISIBLE : View.GONE);
            holder.rlUserTLTrade.setVisibility(isTrade ? View.VISIBLE : View.GONE);
            holder.llUserTLNoTrade.setVisibility(isTrade ? View.GONE : View.VISIBLE);
            holder.tvUserTLTimeStamp.setVisibility(View.VISIBLE);
            holder.tvUserTLName.setVisibility(isShowHeadAndName ? View.VISIBLE : View.GONE);
            holder.imgUserTLUserHeader.setVisibility(isShowHeadAndName ? View.VISIBLE : View.GONE);
            holder.tvUserTLTitle.setVisibility(StringUtils.isNullOrEmpty(item.header) ? View.GONE : View.VISIBLE);


            if(position==0){
                holder.headerV.setVisibility(View.VISIBLE);
                if(position == (getCount()-1)){
                    holder.footerV.setVisibility(View.VISIBLE);
                }else{
                    holder.footerV.setVisibility(View.GONE);
                }
            }else{
                if(item.isNotice){
                    holder.headerV.setVisibility(View.GONE);
                    holder.footerV.setVisibility(View.GONE);
                }else{
                    holder.headerV.setVisibility(View.VISIBLE);
                    if(position == (getCount()-1)){
                        holder.footerV.setVisibility(View.VISIBLE);
                    }else{
                        holder.footerV.setVisibility(View.GONE);
                    }
                }
            }

            //item.isHighlight
            if(item.isNotice){
                if( position != 0) {
                    holder.dividerHighLightV.setVisibility(View.VISIBLE);
                }else{
                    holder.dividerHighLightV.setVisibility(View.GONE);
                }
            }
            if(item.isEssential){
                holder.tvEssential.setVisibility(View.VISIBLE);
            }else{
                holder.tvEssential.setVisibility(View.GONE);
            }

            if(showIsTop(item.stickType, fromWhere)){
                holder.tvTop.setVisibility(View.VISIBLE);
            }else{
                holder.tvTop.setVisibility(View.GONE);
            }

            if (item.createdAtUtc != null) {
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
                //有跟买就不要出现comment选项
                holder.llTLBuy.setVisibility((!isMySelf && isTrade && tradeDTO.isBuy() && isShowFollowBuy) ? View.VISIBLE : View.GONE);
                holder.llTLComment.setVisibility((!isMySelf && isTrade && tradeDTO.isBuy() && isShowFollowBuy) ? View.GONE : View.VISIBLE);

                if (tradeDTO != null)
                {
                    holder.tvTradePrice.setText(tradeDTO.getCurrencyDisplay() + DataUtils.keepTwoDecimal(tradeDTO.getUnitPriceCurrency()));
                    holder.tvTradeCount.setText(tradeDTO.displayTradeQuantity());
                    holder.tvTradeMoney.setText(tradeDTO.getCurrencyDisplay() + tradeDTO.displayTradeMoney());
                    holder.title0.setText(tradeDTO.isBuy() ? "买入股票：" : "卖出股票：");
                    holder.title1.setText(tradeDTO.isBuy() ? "买入价格：" : "卖出价格：");
                    holder.title2.setText(tradeDTO.isBuy() ? "买入数量：" : "卖出数量：");
                }
            }
            else
            {
                holder.tvUserTLTitle.setText("" + item.header);
                holder.tvUserTLContent.setText("" + item.text);
                holder.tvReward.setText(item.getRewardString());
                holder.llTLBuy.setVisibility(View.GONE);


                holder.tvUserTLTitleSimple.setText(StringUtils.isNullOrEmpty(item.header)?item.text:item.header);
            }

            if (isShowHeadAndName)
            {

                if (item.getUser() != null)
                {
                    holder.tvUserTLName.setText(item.getUser().getDisplayName());
                    ImageLoader.getInstance()
                            .displayImage(item.getUser().picture,
                                    holder.imgUserTLUserHeader,
                                    UniversalImageLoader.getAvatarImageLoaderOptions(false));

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

                if (item.createdAtUtc != null)
                {
                    holder.tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(item.createdAtUtc));
                }
            }

            holder.btnTLPraise.setBackgroundResource(item.voteDirection == 1 ? R.drawable.icon_praise_active : R.drawable.icon_praise_normal);
            holder.btnTLPraiseDown.setBackgroundResource(
                    item.voteDirection == -1 ? R.drawable.icon_praise_down_active : R.drawable.icon_praise_down_normal);

            holder.tvTLPraise.setText(Html.fromHtml(item.getVoteUpString()));
            holder.tvTLPraiseDown.setText(Html.fromHtml(item.getVoteDownString()));
            holder.tvTLComment.setText("" + item.commentCount);

            holder.llItemAll.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLineItemClicked(position);
                }
            });

            holder.tvUserTLTitleSimple.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (view instanceof MarkdownTextView)
                    {
                        if (!((MarkdownTextView) view).isClicked)
                        {
                            timeLineOperater.OnTimeLineItemClicked(position);
                        }
                        ((MarkdownTextView) view).isClicked = false;
                    }
                }
            });

            holder.tvUserTLTitle.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (view instanceof MarkdownTextView)
                    {
                        if (!((MarkdownTextView) view).isClicked)
                        {
                            timeLineOperater.OnTimeLineItemClicked(position);
                        }
                        ((MarkdownTextView) view).isClicked = false;
                    }
                }
            });

            holder.tvUserTLContent.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (view instanceof MarkdownTextView)
                    {
                        if (!((MarkdownTextView) view).isClicked)
                        {
                            timeLineOperater.OnTimeLineItemClicked(position);
                        }
                        ((MarkdownTextView) view).isClicked = false;
                    }
                }
            });

            holder.llTLBuy.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLineBuyClicked(position);
                    clickedBuy(position);
                }
            });
            holder.llTLPraise.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLinePraiseClicked(position);
                    clickedPraise(position);
                    if(item.voteDirection != 0) {
                        copyHolder.btnTLPraise.startAnimation(praiseAnimation);
                    }
                }
            });
            holder.llTLPraiseDown.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLinePraiseDownClicked(position);
                    clickedPraiseDown(position);
                    if(item.voteDirection != 0) {
                        copyHolder.btnTLPraiseDown.startAnimation(animation);
                    }
                }
            });

            holder.llTLComment.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    timeLineOperater.OnTimeLineItemClicked(position);
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
        if (context instanceof DashboardNavigatorActivity && getNavigator() != null)
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
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_ITEM_PERSON));
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



    public void clickedBuy(int position){
        SecurityId securityId = ((TimelineItemDTO) getItem(position)).getMedias().get(0).createSecurityId();

        Bundle bundle = new Bundle();
        bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_SELL);
        bundle.putString(SecurityOptActivity.KEY_SECURITY_EXCHANGE, securityId.getExchange());
        bundle.putString(SecurityOptActivity.KEY_SECURITY_SYMBOL, securityId.getSecuritySymbol());
        bundle.putInt(CompetitionSecuritySearchFragment.BUNDLE_COMPETITION_ID, 0);
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityId.getDisplayName());
        Intent intent = new Intent(context, SecurityOptActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
        if(context instanceof Activity) {
            ((Activity)context).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    }

    private void enterFragment(Class fragmentClass, Bundle args)
    {
        if (getNavigator() != null)
        {
            getNavigator().pushFragment(fragmentClass, args);
        }
        else
        {
            gotoDashboard(fragmentClass.getName(), args);
        }
    }

    public void clickedPraise(int position)
    {
        TimelineItemDTO item = (TimelineItemDTO) getItem(position);

        if (item.voteDirection == 1)
        {
            item.voteDirection = 0;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
            updateVoting(VoteDirection.UnVote, item);
        }
        else if (item.voteDirection == 0)
        {
            item.voteDirection = 1;
            item.upvoteCount += 1;
            updateVoting(VoteDirection.UpVote, item);
        }
        else if (item.voteDirection == -1)
        {
            item.voteDirection = 1;
            item.upvoteCount += 1;
            item.downvoteCount = item.downvoteCount > 0 ? (item.downvoteCount - 1) : 0;
            updateVoting(VoteDirection.UpVote, item);
        }

        notifyDataSetChanged();
    }

    public void clickedPraiseDown(int position)
    {
        TimelineItemDTO item = (TimelineItemDTO) getItem(position);

        if (item.voteDirection == 1)
        {
            item.voteDirection = -1;
            item.downvoteCount += 1;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
            updateVoting(VoteDirection.DownVote, item);
        }
        else if (item.voteDirection == 0)
        {
            item.voteDirection = -1;
            item.downvoteCount += 1;
            updateVoting(VoteDirection.DownVote, item);
        }
        else if (item.voteDirection == -1)
        {
            item.voteDirection = 0;
            item.downvoteCount = item.downvoteCount > 0 ? (item.downvoteCount - 1) : 0;
            updateVoting(VoteDirection.UnVote, item);
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

    public static void setTimeLineItemDeleted(int itemDeleted)
    {
        timeLineItemDeleted = itemDeleted;
    }

    public static void setTimeLineItemAnswered(int itemAnswered)
    {
        timeLineItemAnswered = itemAnswered;
    }

    //UI主动调用Adapter方法 去除已经删除的帖子
    public void OnResumeDataAction()
    {
        deleteTimeLineItemDeleted();
        updateTimeLineItemAnswerd();
    }

    public void updateTimeLineItemAnswerd()
    {
        //悬赏贴已经采纳更新状态
        if (timeLineItemAnswered != -1 && enhancedItems != null && enhancedItems.size() > 0)
        {
            for (int i = 0; i < enhancedItems.size(); i++)
            {
                TimelineItemDTO dto = enhancedItems.get(i);
                if (dto.id == timeLineItemAnswered)
                {
                    enhancedItems.get(i).isAnswered = true;
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    public void deleteTimeLineItemDeleted()
    {
        //删除已删除的帖子
        if (timeLineItemDeleted != -1 && enhancedItems != null && enhancedItems.size() > 0)
        {
            for (int i = 0; i < enhancedItems.size(); i++)
            {
                TimelineItemDTO dto = enhancedItems.get(i);
                if (dto.id == timeLineItemDeleted)
                {
                    enhancedItems.remove(i);
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    public static boolean showIsTop(int stickType, String fromWhere){
        if(stickType==0 || fromWhere.equals("")){
            return false;
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_FAVORITE)){
            return DiscoveryUtils.isOne(2, stickType);
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_LEARNING)){
            return DiscoveryUtils.isOne(1, stickType);
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_RECENT)){
            return DiscoveryUtils.isOne(0, stickType);
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_REWARD)){
            return DiscoveryUtils.isOne(3, stickType);
        }
        return false;
    }

    public static int toOne(int stickType, String fromWhere){
        if(fromWhere.equals("")){
            return 0;
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_RECENT)){
            return DiscoveryUtils.toOne(0, stickType);
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_LEARNING)){
            return DiscoveryUtils.toOne(1, stickType);
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_FAVORITE)){
            return DiscoveryUtils.toOne(2, stickType);
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_REWARD)){
            return DiscoveryUtils.toOne(3, stickType);
        }
        return 0;
    }

    public static int toZero(int stickType, String fromWhere){
        if(fromWhere.equals("")){
            return 0;
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_RECENT)){
            return DiscoveryUtils.toZero(0, stickType);
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_LEARNING)){
            return DiscoveryUtils.toZero(1, stickType);
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_FAVORITE)){
            return DiscoveryUtils.toZero(2, stickType);
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_REWARD)){
            return DiscoveryUtils.toZero(3, stickType);
        }
        return 0;
    }

    private boolean showNoticeNews(){
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_RECENT)){
            return true;
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_LEARNING)){
            return true;
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_FAVORITE)){
            return true;
        }
        if(fromWhere.equals(TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_REWARD)){
            return true;
        }
        return false;
    }


    static class ViewHolder
    {
        public LinearLayout llItemAll = null;
        public LinearLayout llNormalAll = null;
        public LinearLayout llSimpleAll = null;

        public TextView tvUserTLTimeStamp = null;
        public TextView tvUserTLTitle = null;
        public TextView tvUserTLContent = null;

        public ImageView imgUserTLUserHeader = null;
        public TextView tvReward = null;
        public TextView tvEssential = null;
        public TextView tvTop = null;
        public TextView tvUserTLName = null;

        //不是一个交易相关
        public LinearLayout llUserTLNoTrade = null;
        public LinearLayout llTLBuy = null;
        public LinearLayout llTLPraise = null;
        public LinearLayout llTLPraiseDown = null;
        public LinearLayout llTLComment = null;
        public TextView btnTLBuy = null;
        public TextView tvTLBuy = null;
        public TextView btnTLPraise = null;
        public TextView tvTLPraise = null;
        public TextView btnTLPraiseDown = null;
        public TextView tvTLPraiseDown = null;
        public TextView btnTLComment = null;
        public TextView tvTLComment = null;

        //是一个交易相关的
        public RelativeLayout rlUserTLTrade;
        public TextView tvTradeName;
        public TextView tvTradePrice;
        public TextView tvTradeCount;
        public TextView tvTradeMoney;
        public TextView title0;//买入股票
        public TextView title1;//买入价格
        public TextView title2;//买入数量

        public TextView tvUserTLTitleSimple = null;
        public TextView tvTipInTopSimple = null;

        public View headerV;
        public View footerV;
        public View dividerHighLightV;
    }
}
