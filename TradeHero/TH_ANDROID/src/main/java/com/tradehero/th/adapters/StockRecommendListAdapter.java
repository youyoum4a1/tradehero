package com.tradehero.th.adapters;

import android.content.Context;
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
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.stockRecommend.StockRecommendDTOList;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.UserBaseDTO;
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
        TimelineItemDTO timelineItemDTO = (TimelineItemDTO) getItem(position);

        if (timelineItemDTO == null) {
            return convertView;
        }

        UserBaseDTO autherDTO = dtoList.getUserById(timelineItemDTO.userId);
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

        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'fragment_stock_recommend_content.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.userIcon)
        RoundedImageView userIcon;
        @InjectView(R.id.userName)
        TextView userName;
        @InjectView(R.id.userSignature)
        TextView userSignature;
        @InjectView(R.id.roi)
        TextView roi;
        @InjectView(R.id.userPositionClickableArea)
        LinearLayout userPositionClickableArea;
        @InjectView(R.id.userClickableArea)
        RelativeLayout userClickableArea;
        @InjectView(R.id.articleTitle)
        MarkdownTextView articleTitle;
        @InjectView(R.id.articleContent)
        MarkdownTextView articleContent;
        @InjectView(R.id.articleClickableArea)
        LinearLayout articleClickableArea;
        @InjectView(R.id.attachmentImage)
        ImageView attachmentImage;
        @InjectView(R.id.btnTLPraise)
        TextView btnTLPraise;
        @InjectView(R.id.numberRead)
        TextView numberRead;
        @InjectView(R.id.buttonRead)
        LinearLayout buttonRead;
        @InjectView(R.id.btnTLPraiseDown)
        TextView btnTLPraiseDown;
        @InjectView(R.id.numberPraised)
        TextView numberPraised;
        @InjectView(R.id.buttonPraised)
        LinearLayout buttonPraised;
        @InjectView(R.id.numberComment)
        TextView numberComment;
        @InjectView(R.id.buttonComment)
        LinearLayout buttonComment;
        @InjectView(R.id.itemSeparator)
        View itemSeparator;
        @InjectView(R.id.createTime)
        TextView createTime;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
