package com.tradehero.chinabuild.fragment.userCenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.ButterKnife;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserFriendsListAdapter;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.widget.TradeHeroProgressBar;

/**
 * Created by palmer on 15/2/25.
 */
public class UserFansListFragment extends DashboardFragment {

    private TradeHeroProgressBar tradeheroprogressbar_users;
    private PullToRefreshListView fansLV;
    private ImageView emptyIV;

    private UserFriendsListAdapter adapter;

    private UserBaseKey showUserBaseKey;

    private int typeFriends = UserFriendsListFragment.TYPE_FRIENDS_HERO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fans_list_fragment, container, false);
        ButterKnife.inject(this, view);
        tradeheroprogressbar_users = (TradeHeroProgressBar) view.findViewById(R.id.tradeheroprogressbar_users);
        fansLV = (PullToRefreshListView) view.findViewById(R.id.listFriends);
        emptyIV = (ImageView) view.findViewById(R.id.imgEmpty);

        initArgument();

        return view;
    }

    private void showProgressDlg() {
        if (tradeheroprogressbar_users != null) {
            tradeheroprogressbar_users.setVisibility(View.VISIBLE);
            tradeheroprogressbar_users.startLoading();
        }
    }

    private void hideProgressDlg() {
        if (tradeheroprogressbar_users != null) {
            tradeheroprogressbar_users.stopLoading();
            tradeheroprogressbar_users.setVisibility(View.GONE);
        }
    }

    private void initArgument(){
        Bundle bundle = getArguments();
        typeFriends = bundle.getInt(UserFriendsListFragment.BUNDLE_SHOW_FRIENDS_TYPE, 0);
        int userId = bundle.getInt(UserFriendsListFragment.BUNDLE_SHOW_USER_ID, 0);
        if (userId != 0) {
            showUserBaseKey = new UserBaseKey(userId);
        }
    }
}
