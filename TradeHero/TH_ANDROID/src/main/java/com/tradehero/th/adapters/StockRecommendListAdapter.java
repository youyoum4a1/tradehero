package com.tradehero.th.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.chinabuild.fragment.stockRecommend.StockRecommendDetailFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.TradeHeroMainActivity;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.stockRecommend.StockRecommendDTOList;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MarkdownTextView;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class StockRecommendListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private StockRecommendDTOList dtoList;
    private int mCount = 0;

    @Inject
    public PrettyTime prettyTime;

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
        if (mCount > 0) {//for buy what main page
            return mCount;
        }
        return dtoList.getSize();
    }

    public void setShowCount(int mCount) {
        this.mCount = mCount;
    }

    @Override
    public Object getItem(int position) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.getItem(0);
//        return dtoList.getItem(position);
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

        ViewHolder holder = new ViewHolder(convertView);

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

        // Article
        holder.articleTitle.setText(timelineItemDTO.header);
        holder.articleContent.setText(timelineItemDTO.text);
        holder.articleTitle.setMaxLines(1);
        holder.articleContent.setMaxLines(2);

        // Hide the attachment image
        holder.attachmentImage.setVisibility(View.GONE);

        // Time
        holder.createTime.setText(prettyTime.formatUnrounded(timelineItemDTO.createdAtUtc));

        // View count
        holder.numberRead.setText(String.valueOf(timelineItemDTO.viewCount));
        holder.numberPraised.setText(String.valueOf(timelineItemDTO.upvoteCount));
        holder.numberComment.setText(String.valueOf(timelineItemDTO.commentCount));

        // Item separator
        if (position == getCount() - 1) {
            holder.itemSeparator.setVisibility(View.GONE);
        }

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
        holder.articleClickableArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Bundle discussBundle = new Bundle();
                discussBundle.putString(TimelineItemDTOKey.BUNDLE_KEY_TYPE, DiscussionType.TIMELINE_ITEM.name());
                discussBundle.putInt(TimelineItemDTOKey.BUNDLE_KEY_ID, timelineItemDTO.id);
                bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, discussBundle);
                pushFragment(StockRecommendDetailFragment.class, bundle);
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
        @InjectView(R.id.articleContent)
        public MarkdownTextView articleContent;
        @InjectView(R.id.articleClickableArea)
        public LinearLayout articleClickableArea;
        @InjectView(R.id.attachmentImage)
        public ImageView attachmentImage;
        @InjectView(R.id.btnTLPraise)
        public TextView btnTLPraise;
        @InjectView(R.id.numberRead)
        public TextView numberRead;
        @InjectView(R.id.buttonRead)
        public LinearLayout buttonRead;
        @InjectView(R.id.btnTLPraiseDown)
        public TextView btnTLPraiseDown;
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
