package com.tradehero.th.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.chinabuild.fragment.stockRecommend.StockRecommendDetailFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.TradeHeroMainActivity;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.stockRecommend.StockRecommendDTOList;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class StockRecommendListAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater inflater;
    private StockRecommendDTOList dtoList;
    private int mCount = 0;

    @Inject PrettyTime prettyTime;
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;

    public StockRecommendListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        DaggerUtils.inject(this);
    }

    public void setItems(StockRecommendDTOList list) {
        dtoList = list;
    }

    @Override
    public int getCount() {
        if (dtoList == null) {
            return 0;
        }
        return dtoList.getSize();
    }

    @Override
    public Object getItem(int position) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TimelineItemDTO timelineItemDTO = (TimelineItemDTO) getItem(position);

        if (timelineItemDTO == null) {
            return convertView;
        }

        final UserBaseDTO autherDTO = dtoList.getUserById(timelineItemDTO.userId);
        if (autherDTO == null) {
            return convertView;
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_stock_recommend_content, null);
        }

        final ViewHolder holder = new ViewHolder(convertView);

        // User Image
        ImageLoader.getInstance().displayImage(autherDTO.picture, holder.userIcon, UniversalImageLoader.getAvatarImageLoaderOptions());
        // User name
        holder.userName.setText(autherDTO.getDisplayName());
        // User signature
        if (autherDTO.signature == null) {
            holder.userSignature.setVisibility(View.GONE);
        } else {
            holder.userSignature.setText(autherDTO.signature);
        }

        // User ROI
        if (autherDTO.roiSinceInception == null) {
            autherDTO.roiSinceInception = 0.0;
        }
        THSignedPercentage roi = THSignedPercentage.builder(autherDTO.roiSinceInception * 100).build();
        holder.roi.setText(roi.toString());
        holder.roi.setTextColor(context.getResources().getColor(roi.getColorResId()));

        // Article
        holder.articleTitle.setText(timelineItemDTO.header);
        holder.articleContentLine1.setText(timelineItemDTO.text);
        holder.articleTitle.setMaxLines(1);
        holder.articleContentLine1.setMaxLines(1);
        holder.articleContentLine1.setOnMeasureListener(new MarkdownTextView.OnMeasureListener() {
            @Override
            public void onMeasure() {
                if (holder.articleContentLine1.getLayout() != null) {
                    int restStartPos = holder.articleContentLine1.getLayout().getLineStart(1) + 1;
                    if (restStartPos < timelineItemDTO.text.length()) {
                        holder.articleContentRest.setSingleLine();
                        holder.articleContentRest.setEllipsize(TextUtils.TruncateAt.END);
                        holder.articleContentRest.setText(timelineItemDTO.text.substring(restStartPos));
                    } else {
                        holder.articleContentRest.setVisibility(View.GONE);
                    }
                }
            }
        });

        // Hide the attachment image
        holder.attachmentImage.setVisibility(View.GONE);

        // Time
        holder.createTime.setText(prettyTime.formatUnrounded(timelineItemDTO.createdAtUtc));

        // View count
        holder.numberRead.setText(String.valueOf(timelineItemDTO.viewCount));
        holder.numberPraised.setText(String.valueOf(timelineItemDTO.upvoteCount));
        holder.numberComment.setText(String.valueOf(timelineItemDTO.commentCount));
        holder.btnTLPraise.setBackgroundResource(timelineItemDTO.voteDirection == 1 ? R.drawable.like_selected : R.drawable.like);

        // Listeners
        holder.userClickableArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, autherDTO.id);
                bundle.putBoolean(UserMainPage.BUNDLE_NEED_SHOW_PROFILE, false);
                pushFragment(UserMainPage.class, bundle);
            }
        });
        holder.userPositionClickableArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID, autherDTO.id);
                pushFragment(PortfolioFragment.class, bundle);
            }
        });

        View.OnClickListener articleClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof MarkdownTextView) {
                    if (((MarkdownTextView) v).isClicked) {
                        return;
                    }
                }
                Bundle bundle = new Bundle();
                Bundle discussBundle = new Bundle();
                discussBundle.putString(TimelineItemDTOKey.BUNDLE_KEY_TYPE, DiscussionType.TIMELINE_ITEM.name());
                discussBundle.putInt(TimelineItemDTOKey.BUNDLE_KEY_ID, timelineItemDTO.id);
                bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, discussBundle);
                pushFragment(StockRecommendDetailFragment.class, bundle);
            }
        };
        holder.articleClickableArea.setOnClickListener(articleClickListener);
        holder.articleTitle.setOnClickListener(articleClickListener);
        holder.articleContentLine1.setOnClickListener(articleClickListener);
        holder.articleContentRest.setOnClickListener(articleClickListener);
        holder.buttonPraised.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoteDirection voteDirection;
                if (timelineItemDTO.voteDirection == 1) {
                    timelineItemDTO.voteDirection = 0;
                    timelineItemDTO.upvoteCount = timelineItemDTO.upvoteCount > 0 ? (timelineItemDTO.upvoteCount - 1) : 0;
                    voteDirection = VoteDirection.UnVote;
                    holder.btnTLPraise.setBackgroundResource(R.drawable.like);
                } else {
                    timelineItemDTO.voteDirection = 1;
                    timelineItemDTO.upvoteCount += 1;
                    voteDirection = VoteDirection.UpVote;
                    holder.btnTLPraise.setBackgroundResource(R.drawable.like_selected);
                    holder.btnTLPraise.startAnimation(AnimationUtils.loadAnimation(context, R.anim.vote_praise));
                }
                holder.numberPraised.setText(String.valueOf(timelineItemDTO.upvoteCount));

                DiscussionVoteKey discussionVoteKey = new DiscussionVoteKey(
                        DiscussionType.TIMELINE_ITEM,
                        timelineItemDTO.id,
                        voteDirection);
                discussionServiceWrapper.get().vote(discussionVoteKey, new Callback<DiscussionDTO>() {
                    @Override
                    public void success(DiscussionDTO discussionDTO, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
        holder.buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscussionKey discussionKey = timelineItemDTO.getDiscussionKey();
                Bundle bundle = new Bundle();
                bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                        discussionKey.getArgs());
                pushFragment(DiscussSendFragment.class, bundle);
            }
        });

        return convertView;
    }

    private void pushFragment(Class fragment, Bundle bundle) {
        if (context instanceof DashboardNavigatorActivity) {
            ((DashboardNavigatorActivity) context).getDashboardNavigator().pushFragment(fragment, bundle);
        } else if (context instanceof TradeHeroMainActivity) {
            bundle.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, fragment.getName());
            ActivityHelper.launchDashboard((TradeHeroMainActivity) context, bundle);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'fragment_stock_recommend_content.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    public static class ViewHolder {
        @InjectView(R.id.llItemAll)
        public LinearLayout llItemAll;
        @InjectView(R.id.userIcon)
        public RoundedImageView userIcon;
        @InjectView(R.id.userName)
        public TextView userName;
        @InjectView(R.id.userSignature)
        public TextView userSignature;
        @InjectView(R.id.roi)
        public TextView roi;
        @InjectView(R.id.userPositionClickableArea)
        public LinearLayout userPositionClickableArea;
        @InjectView(R.id.userClickableArea)
        public RelativeLayout userClickableArea;
        @InjectView(R.id.articleTitle)
        public MarkdownTextView articleTitle;
        @InjectView(R.id.articleContentLine1)
        public MarkdownTextView articleContentLine1;
        @InjectView(R.id.articleContentRest)
        public MarkdownTextView articleContentRest;
        @InjectView(R.id.articleClickableArea)
        public LinearLayout articleClickableArea;
        @InjectView(R.id.attachmentImage)
        public ImageView attachmentImage;
        @InjectView(R.id.btnTLViewCount)
        public TextView btnTLViewCount;
        @InjectView(R.id.numberRead)
        public TextView numberRead;
        @InjectView(R.id.buttonRead)
        public LinearLayout buttonRead;
        @InjectView(R.id.btnTLPraise)
        public TextView btnTLPraise;
        @InjectView(R.id.numberPraised)
        public TextView numberPraised;
        @InjectView(R.id.buttonPraised)
        public LinearLayout buttonPraised;
        @InjectView(R.id.numberComment)
        public TextView numberComment;
        @InjectView(R.id.buttonComment)
        public LinearLayout buttonComment;
        @InjectView(R.id.itemSeparator)
        public View itemSeparator;
        @InjectView(R.id.createTime)
        public TextView createTime;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
