package com.tradehero.chinabuild.fragment.discovery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Discovery Square fragment
 *
 * Created by palmer on 15/1/26.
 */
public class DiscoverySquareFragment extends DashboardFragment implements View.OnClickListener{

    //Square Reward Views
    private LinearLayout rewardLL;

    //Square Recent Views
    private LinearLayout recentLL;

    //Square Favorite Views
    private LinearLayout favoriteLL;

    //Square Novice Views
    private LinearLayout noviceLL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery_square, container, false);
        ButterKnife.inject(this, view);
        recentLL = (LinearLayout)view.findViewById(R.id.linearlayout_square_recent);
        rewardLL = (LinearLayout)view.findViewById(R.id.linearlayout_square_reward);
        favoriteLL = (LinearLayout)view.findViewById(R.id.linearlayout_square_favorite);
        noviceLL = (LinearLayout)view.findViewById(R.id.linearlayout_square_learning);
        recentLL.setOnClickListener(this);
        rewardLL.setOnClickListener(this);
        favoriteLL.setOnClickListener(this);
        noviceLL.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch(viewId){
            case R.id.linearlayout_square_recent:
                gotoDashboard(DiscoveryRecentNewsFragment.class, new Bundle());
                break;
            case R.id.linearlayout_square_reward:
                gotoDashboard(DiscoveryRewardFragment.class, new Bundle());
                break;
            case R.id.linearlayout_square_favorite:
                break;
            case R.id.linearlayout_square_learning:
                break;
        }
    }
}
