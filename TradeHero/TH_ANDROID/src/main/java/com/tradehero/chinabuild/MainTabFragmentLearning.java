package com.tradehero.chinabuild;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.th.R;

/**
 * Stock Learning
 *
 * Created by palmer on 15/3/25.
 */
public class MainTabFragmentLearning extends AbsBaseFragment {

    @InjectView(R.id.rlCustomHeadView) RelativeLayout rlCustomHeadLayout;
    @InjectView(R.id.tvHeadLeft) TextView tvHeadLeft;
    @InjectView(R.id.tvHeadMiddleMain) TextView tvHeadTitle;
    @InjectView(R.id.tvHeadRight0) TextView tvHeadRight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_tab_fragment_learning_layout, container, false);
        ButterKnife.inject(this, view);

        initView();

        return view;
    }

    @Override public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void initView() {
        rlCustomHeadLayout.setVisibility(View.VISIBLE);
        tvHeadLeft.setVisibility(View.GONE);
        tvHeadRight.setVisibility(View.GONE);
        tvHeadTitle.setVisibility(View.VISIBLE);
        tvHeadTitle.setText(R.string.stock_learning);
    }
}
