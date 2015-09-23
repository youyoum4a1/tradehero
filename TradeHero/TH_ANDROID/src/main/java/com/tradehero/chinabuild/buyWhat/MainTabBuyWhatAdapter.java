package com.tradehero.chinabuild.buyWhat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.livetrade.DataUtils;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.activities.TradeHeroMainActivity;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class MainTabBuyWhatAdapter extends BaseAdapter implements View.OnClickListener {
    private LeaderboardUserDTOList mDtoList = new LeaderboardUserDTOList();
    private LayoutInflater inflater;
    private Context mContext;

    @Inject
    public Lazy<PrettyTime> prettyTime;

    public MainTabBuyWhatAdapter(Context context) {
        DaggerUtils.inject(this);
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDtoList.size();
    }

    @Override
    public LeaderboardUserDTO getItem(int i) {
        return mDtoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.main_tab_buy_what_item, null);
        }
        LeaderboardUserDTO item = getItem(i);

        ImageView choiceImageView = (ImageView) convertView.findViewById(R.id.icon);
        ImageLoader.getInstance().displayImage(item.picture, choiceImageView, UniversalImageLoader.getAvatarImageLoaderOptions());

        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        userName.setText(item.displayName);

        TextView winRate = (TextView) convertView.findViewById(R.id.win_rate);
        String winRateString = String.valueOf(item.winRatio * 100);
        int pointPosition = winRateString.indexOf(".");
        int end = pointPosition + 3 > winRateString.length() ? winRateString.length() : pointPosition + 3;
        winRate.setText(winRateString.substring(0, end) + "%");

        TextView monthlyRate = (TextView) convertView.findViewById(R.id.monthly_rate);
        String monthlyRateString = String.valueOf(item.monthlyRoi * 100);
        int pointPosition2 = monthlyRateString.indexOf(".");
        int end2 = pointPosition2 + 3 > monthlyRateString.length() ? monthlyRateString.length() : pointPosition2 + 3;
        monthlyRate.setText(monthlyRateString.substring(0, end2) + "%");

        TextView securityName = (TextView) convertView.findViewById(R.id.security_name);
        securityName.setText(item.securityName + "(" + item.symbol + ")");

        TextView tradePrice = (TextView) convertView.findViewById(R.id.trade_price);
        tradePrice.setText("成交价格 " + DataUtils.keepTwoDecimal(item.price));

        TextView tradeTime = (TextView) convertView.findViewById(R.id.trade_time);
        tradeTime.setText(prettyTime.get().formatUnrounded(item.dateTimeUtc));

        TextView percent = (TextView) convertView.findViewById(R.id.percent);
        String percentString = String.valueOf(item.percent * 100);
        if (item.percent < 0.0001) {
            percentString = "0.01";
        }
        int pointPosition3 = percentString.indexOf(".");
        int end3 = pointPosition3 + 3 > percentString.length() ? percentString.length() : pointPosition3 + 3;
        percent.setText("仓位 " + percentString.substring(0, end3) + "%");

        TextView buyButton = (TextView) convertView.findViewById(R.id.follow_buy_button);
        buyButton.setOnClickListener(this);
        buyButton.setTag(i);

        RelativeLayout upLayout = (RelativeLayout) convertView.findViewById(R.id.up_layout);
        upLayout.setOnClickListener(this);
        upLayout.setTag(i);

        RelativeLayout downLayout = (RelativeLayout) convertView.findViewById(R.id.down_layout);
        downLayout.setOnClickListener(this);
        downLayout.setTag(i);

        return convertView;
    }

    public void setItems(LeaderboardUserDTOList list) {
        mDtoList = list;
    }

    public void addItems(LeaderboardUserDTOList listData) {
        mDtoList.addAll(listData);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up_layout:
                goToUser(getItem((int) v.getTag()));
                break;
            case R.id.down_layout:
                goToSecurityDetail(getItem((int) v.getTag()));
                break;
            case R.id.follow_buy_button:
                goToMockTrade(getItem((int) v.getTag()));
                break;
        }
    }

    private void goToUser(LeaderboardUserDTO userDTO) {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userDTO.id);
        bundle.putBoolean(UserMainPage.BUNDLE_NEED_SHOW_PROFILE, false);
        pushFragment(UserMainPage.class, bundle);
    }

    private void goToSecurityDetail(LeaderboardUserDTO userDTO) {
        Bundle bundle = new Bundle();
        SecurityId id = new SecurityId(userDTO.exchange, userDTO.symbol);
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, id.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, userDTO.securityName);
        pushFragment(SecurityDetailFragment.class, bundle);
    }

    private void goToMockTrade(LeaderboardUserDTO dto) {
        Bundle bundle = new Bundle();
        bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
        bundle.putString(SecurityOptActivity.KEY_SECURITY_EXCHANGE, dto.exchange);
        bundle.putString(SecurityOptActivity.KEY_SECURITY_SYMBOL, dto.symbol);
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, dto.securityName);
        Intent intent = new Intent(mContext, SecurityOptActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
        ((FragmentActivity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    private void pushFragment(Class fragment, Bundle bundle) {
        if (mContext instanceof DashboardNavigatorActivity) {
            ((DashboardNavigatorActivity) mContext).getDashboardNavigator().pushFragment(fragment, bundle);
        } else if (mContext instanceof TradeHeroMainActivity) {
            bundle.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, fragment.getName());
            ActivityHelper.launchDashboard((TradeHeroMainActivity)mContext, bundle);
        }
    }
}
