package com.tradehero.chinabuild.mainTab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import timber.log.Timber;

public class MainTabBuyWhatAdapter extends BaseAdapter implements View.OnClickListener {
    private LeaderboardUserDTOList mDtoList = new LeaderboardUserDTOList();
    private LayoutInflater inflater;

    @Inject
    public Lazy<PrettyTime> prettyTime;

    public MainTabBuyWhatAdapter(Context context) {
        DaggerUtils.inject(this);
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
        tradePrice.setText("成交价格 " + item.price);

        TextView tradeTime = (TextView) convertView.findViewById(R.id.trade_time);
        tradeTime.setText(prettyTime.get().formatUnrounded(item.dateTimeUtc));

        TextView percent = (TextView) convertView.findViewById(R.id.percent);
        String percentString = String.valueOf(item.percent * 100);
        int pointPosition3 = percentString.indexOf(".");
        int end3 = pointPosition3 + 3 > percentString.length() ? percentString.length() : pointPosition3 + 3;
        percent.setText(percentString.substring(0, end3) + "%");

//        RelativeLayout upLayout = (RelativeLayout)convertView.findViewById(R.id.up_layout);
//        upLayout.setOnClickListener(this);
//        RelativeLayout downLayout = (RelativeLayout)convertView.findViewById(R.id.down_layout);
//        downLayout.setOnClickListener(this);

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
        Timber.d("lyl onClick " + v.getId());
    }
}
