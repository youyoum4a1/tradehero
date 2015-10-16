package com.tradehero.chinabuild.fragment.stockRecommend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MarkdownTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class StockRecommendFragment extends DashboardFragment {

    @InjectView(R.id.userIcon)
    RoundedImageView userIcon;
    @InjectView(R.id.roi)
    TextView roi;
    @InjectView(R.id.userSignature)
    TextView userSignature;
    @InjectView(R.id.userName)
    TextView userName;
    @InjectView(R.id.articleTitle)
    MarkdownTextView articleTitle;
    @InjectView(R.id.articleContent)
    MarkdownTextView articleContent;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_recommend_content, container, false);
        ButterKnife.inject(this, view);

        articleTitle.setText("我推荐[$SHA:600086](tradehero://security/57961_SHA_600086) ");
        articleContent.setText("[$SHA:600086](tradehero://security/57961_SHA_600086) ");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.userClickableArea)
    public void openUserProfile(View view) {
        // Goto user homepage
    }

    @OnClick(R.id.userPositionClickableArea)
    public void openUserPosition(View view) {
        // Goto user position page
    }
}
