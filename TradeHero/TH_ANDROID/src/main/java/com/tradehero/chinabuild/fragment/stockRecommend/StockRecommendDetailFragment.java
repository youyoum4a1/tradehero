package com.tradehero.chinabuild.fragment.stockRecommend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.activities.DisplayLargeImageActivity;
import com.tradehero.th.adapters.StockRecommendListAdapter.ViewHolder;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.models.number.THSignedPercentage;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class StockRecommendDetailFragment extends TimeLineItemDetailFragment {

    protected ViewHolder viewHolder;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.stock_recommend);
    }

    @Override
    public LinearLayout getHeaderView(LayoutInflater inflater) {
        return (LinearLayout) inflater.inflate(R.layout.fragment_stock_recommend_content, null);
    }

    @Override
    public void initRoot(View view) {
        viewHolder = new ViewHolder(view);
        llDisscurssOrNews = viewHolder.llItemAll;
        imgSecurityTLUserHeader = viewHolder.userIcon;
        tvUserTLTimeStamp = viewHolder.createTime;
        tvUserTLContent = viewHolder.articleContent;
        tvUserTLName = viewHolder.userName;
        btnTLPraise = viewHolder.btnTLPraise;
    }

    @Override
    public void displayDiscussOrNewsDTO() {
        final TimelineItemDTO timelineItemDTO = (TimelineItemDTO) getAbstractDiscussionCompactDTO();
        llDisscurssOrNews.setVisibility(timelineItemDTO == null ? View.INVISIBLE : View.VISIBLE);
        if (timelineItemDTO != null) {
            // User Image
            ImageLoader.getInstance().displayImage(timelineItemDTO.user.picture, viewHolder.userIcon, UniversalImageLoader.getAvatarImageLoaderOptions());
            // User name
            viewHolder.userName.setText(timelineItemDTO.user.getDisplayName());
            // User signature
            if (timelineItemDTO.user.signature == null) {
                viewHolder.userSignature.setVisibility(View.GONE);
            } else {
                viewHolder.userSignature.setText(timelineItemDTO.user.signature);
            }

            // User ROI
            THSignedPercentage roi = THSignedPercentage.builder(timelineItemDTO.user.roiSinceInception * 100).build();
            viewHolder.roi.setText(roi.toString());
            viewHolder.roi.setTextColor(getActivity().getResources().getColor(roi.getColorResId()));

            // Article
            viewHolder.articleTitle.setText(timelineItemDTO.header);
            viewHolder.articleContent.setText(timelineItemDTO.text);

            // Attachment image
            if (timelineItemDTO.picUrl == null) {
                viewHolder.attachmentImage.setVisibility(View.GONE);
            } else {
                ImageLoader.getInstance().displayImage(timelineItemDTO.picUrl, viewHolder.attachmentImage, UniversalImageLoader.getDisplayLargeImageOptions());
            }

            // Time
            viewHolder.createTime.setText(prettyTime.get().formatUnrounded(timelineItemDTO.createdAtUtc));

            // View count
            viewHolder.numberRead.setText(String.valueOf(timelineItemDTO.viewCount));
            viewHolder.numberPraised.setText(String.valueOf(timelineItemDTO.upvoteCount));
            viewHolder.numberComment.setText(String.valueOf(timelineItemDTO.commentCount));
            viewHolder.btnTLPraise.setBackgroundResource(timelineItemDTO.voteDirection == 1 ? R.drawable.like_selected : R.drawable.like);

            // Listeners
            viewHolder.userClickableArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, timelineItemDTO.user.id);
                    bundle.putBoolean(UserMainPage.BUNDLE_NEED_SHOW_PROFILE, false);
                    pushFragment(UserMainPage.class, bundle);
                }
            });
            viewHolder.userPositionClickableArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID, timelineItemDTO.user.id);
                    pushFragment(PortfolioFragment.class, bundle);
                }
            });
            viewHolder.buttonPraised.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPraise();
                }
            });
            viewHolder.buttonComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comments(getAbstractDiscussionCompactDTO());
                }
            });
            viewHolder.attachmentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DisplayLargeImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(DisplayLargeImageActivity.KEY_LARGE_IMAGE_URL, timelineItemDTO.picUrl);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void setTimelineOperaterLLVisibility(int visible) {
        super.setTimelineOperaterLLVisibility(View.GONE);
    }
}
