package com.tradehero.th.adapters;

import android.content.Context;
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
import com.tradehero.chinabuild.data.EmptyDiscussionCompactDTO;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SecurityTimeLineDiscussOrNewsAdapter extends TimeLineBaseAdapter
{

    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    private MiddleCallback<DiscussionDTO> voteCallback;
    private List<AbstractDiscussionCompactDTO> listData;

    private Animation praiseAnimation;
    private Animation despiseAnimation;

    public SecurityTimeLineDiscussOrNewsAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        praiseAnimation = AnimationUtils.loadAnimation(context, R.anim.vote_praise);
        despiseAnimation = AnimationUtils.loadAnimation(context, R.anim.vote_ani);
    }

    public void setListData(List<AbstractDiscussionCompactDTO> listCompactDTO) {
        if (listCompactDTO != null && listCompactDTO.size() == 0) {
            listCompactDTO.add(new EmptyDiscussionCompactDTO());
        }
        listData = listCompactDTO;
        notifyDataSetChanged();
    }

    public void setListDataWithoutEmpty(List<AbstractDiscussionCompactDTO> listCompactDTO){
        if(listCompactDTO == null){
            return;
        }
        listData = listCompactDTO;
        notifyDataSetChanged();
    }

    public void addListData(List<AbstractDiscussionCompactDTO> listCompactDTO) {
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

        final AbstractDiscussionCompactDTO item = getItem(position);
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.security_time_line_item, viewGroup, false);
            holder = new ViewHolder();
            holder.llAllTimeLine = (LinearLayout) convertView.findViewById(R.id.llAllTimeLine);
            holder.llItemAll = (LinearLayout) convertView.findViewById(R.id.llItemAll);
            holder.tvUserTLTimeStamp = (TextView) convertView.findViewById(R.id.tvUserTLTimeStamp);

            //不是股票交易
            holder.llUserTLNoTrade = (RelativeLayout) convertView.findViewById(R.id.llUserTLNoTrade);
            holder.tvUserTLContent = (TextView) convertView.findViewById(R.id.tvUserTLContent);

            holder.imgSecurityTLUserHeader = (ImageView) convertView.findViewById(R.id.imgSecurityTLUserHeader);
            holder.tvUserTLName = (TextView) convertView.findViewById(R.id.tvUserTLName);
            //赞，评论，分享
            holder.llTLPraise = (LinearLayout) convertView.findViewById(R.id.llTLPraise);
            holder.llTLPraiseDown = (LinearLayout)convertView.findViewById(R.id.llTLPraiseDown);
            holder.llTLComment = (LinearLayout) convertView.findViewById(R.id.llTLComment);
            holder.btnTLPraise = (TextView) convertView.findViewById(R.id.btnTLViewCount);
            holder.tvTLPraise = (TextView) convertView.findViewById(R.id.tvTLPraise);
            holder.btnTLPraiseDown = (TextView) convertView.findViewById(R.id.btnTLPraise);
            holder.tvTLPraiseDown = (TextView) convertView.findViewById(R.id.tvTLPraiseDown);
            holder.btnTLComment = (TextView) convertView.findViewById(R.id.btnTLComment);
            holder.tvTLComment = (TextView) convertView.findViewById(R.id.tvTLComment);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ViewHolder copyHolder = holder;
        //fixed a bug ..
        if (item instanceof EmptyDiscussionCompactDTO)
        {
            holder.llAllTimeLine.setVisibility(View.GONE);
            return convertView;
        }
        else
        {
            holder.llAllTimeLine.setVisibility(View.VISIBLE);
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
            holder.tvUserTLName.setText(((DiscussionDTO) item).user.getDisplayName());
            holder.imgSecurityTLUserHeader.setVisibility(View.VISIBLE);
            ImageLoader.getInstance()
                    .displayImage(((DiscussionDTO) item).user.picture,
                            holder.imgSecurityTLUserHeader,
                            UniversalImageLoader.getAvatarImageLoaderOptions(false));
            holder.imgSecurityTLUserHeader.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    openUserProfile(((DiscussionDTO) item).user.id);
                }
            });
        }

        holder.btnTLPraise.setBackgroundResource(item.voteDirection==1?R.drawable.icon_praise_active:R.drawable.icon_praise_normal);
        holder.btnTLPraiseDown.setBackgroundResource(item.voteDirection==-1?R.drawable.icon_praise_down_active:R.drawable.icon_praise_down_normal);

        holder.tvTLPraise.setText(Html.fromHtml(item.getVoteUpString()));
        holder.tvTLPraiseDown.setText(Html.fromHtml(item.getVoteDownString()));
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

        holder.tvUserTLContent.setOnClickListener(new View.OnClickListener()
        {
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
                if(item.voteDirection!=0){
                    copyHolder.btnTLPraise.startAnimation(praiseAnimation);
                }
            }
        });

        holder.llTLPraiseDown.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                if (timeLineOperater != null)
                {
                    timeLineOperater.OnTimeLinePraiseDownClicked(position);
                }
                clickedPraiseDown(position);
                if(item.voteDirection!=0){
                    copyHolder.btnTLPraiseDown.startAnimation(despiseAnimation);
                }
            }
        });

        holder.llTLComment.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                if (timeLineOperater != null)
                {
                    timeLineOperater.OnTimeLineItemClicked(position);
                }
            }
        });
        return convertView;
    }

    public void clickedPraise(int position)
    {
        AbstractDiscussionCompactDTO item = getItem(position);

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
        AbstractDiscussionCompactDTO item = getItem(position);

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

    static class ViewHolder
    {
        public LinearLayout llAllTimeLine = null;

        public LinearLayout llItemAll = null;

        public ImageView imgSecurityTLUserHeader = null;
        public TextView tvUserTLName = null;

        public TextView tvUserTLTimeStamp = null;
        public TextView tvUserTLContent = null;

        //不是一个交易相关
        public RelativeLayout llUserTLNoTrade = null;
        public LinearLayout llTLPraise = null;
        public LinearLayout llTLPraiseDown = null;
        public LinearLayout llTLComment = null;
        //public LinearLayout llTLShare = null;
        public TextView btnTLPraise = null;
        public TextView tvTLPraise = null;
        public TextView btnTLPraiseDown = null;
        public TextView tvTLPraiseDown = null;
        public TextView btnTLComment = null;
        public TextView tvTLComment = null;
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
        }

        @Override public void failure(RetrofitError error)
        {
        }
    }
}
