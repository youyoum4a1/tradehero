package com.tradehero.th.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
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
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SecurityTimeLineDiscussOrNewsAdapter extends TimeLineBaseAdapter
{

    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    private MiddleCallback<DiscussionDTO> voteCallback;
    @Inject Lazy<Picasso> picasso;
    private List<AbstractDiscussionCompactDTO> listData;

    public boolean isSimpleModule = false;//是否支持 赞分享等

    public SecurityTimeLineDiscussOrNewsAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SecurityTimeLineDiscussOrNewsAdapter(Context context, boolean isSimpleModule)
    {
        this(context);
        this.isSimpleModule = isSimpleModule;
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

    public String getItemString(int i)
    {
        AbstractDiscussionCompactDTO dto = listData.get(i);
        if (dto == null) return "";
        if (dto instanceof NewsItemCompactDTO)
        {
            return ((NewsItemCompactDTO) dto).description;
        }
        else if (dto instanceof DiscussionDTO)
        {
            return ((DiscussionDTO) dto).text;
        }
        return "";
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

                holder.includeTLOperater = (LinearLayout) convertView.findViewById(R.id.includeTLOperater);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.includeTLOperater.setVisibility(isSimpleModule ? View.GONE : View.VISIBLE);

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

            holder.tvTLPraise.setText(Html.fromHtml(item.getVoteString()));
            holder.tvTLComment.setText("" + item.commentCount);

            holder.llItemAll.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (timeLineOperater != null)
                    {
                        timeLineOperater.OnTimeLineItemClicked(position);
                    }
                }
            });
            holder.tvUserTLContent.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View view)
                {
                    if (timeLineOperater != null)
                    {
                        timeLineOperater.OnTimeLineItemClicked(position);
                    }
                }
            });
            holder.llTLPraise.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (timeLineOperater != null)
                    {
                        timeLineOperater.OnTimeLinePraiseClicked(position);
                    }
                    clickedPraise(position);
                }
            });

            holder.llTLComment.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (timeLineOperater != null)
                    {
                        timeLineOperater.OnTimeLineCommentsClicked(position);
                    }
                }
            });

            holder.llTLShare.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    if (timeLineOperater != null)
                    {
                        timeLineOperater.OnTimeLineShareClied(position);
                    }
                }
            });
        }
        return convertView;
    }

    public void clickedPraise(int position)
    {
        AbstractDiscussionCompactDTO item = (AbstractDiscussionCompactDTO) getItem(position);
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

        public LinearLayout includeTLOperater;
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
