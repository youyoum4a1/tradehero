package com.tradehero.chinabuild.fragment.stockRecommend;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.th.R;
import com.tradehero.th.adapters.StockRecommendListAdapter.ViewHolder;
import com.tradehero.th.api.timeline.TimelineItemDTO;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class StockRecommendDetailFragment extends TimeLineItemDetailFragment {

    @Override
    public LinearLayout getHeaderView(LayoutInflater inflater) {
        return (LinearLayout) inflater.inflate(R.layout.fragment_stock_recommend_content, null);
    }

    public void initRoot(View view) {
        ViewHolder holder = new ViewHolder(view);
        llDisscurssOrNews = holder.llItemAll;
        imgSecurityTLUserHeader = holder.userIcon;
        tvUserTLTimeStamp = holder.createTime;
        tvUserTLContent = holder.articleContent;
        tvUserTLName = holder.userName;
    }

    @Override
    public void displayDiscussOrNewsDTO() {
        TimelineItemDTO dto = (TimelineItemDTO) getAbstractDiscussionCompactDTO();

    }
}
