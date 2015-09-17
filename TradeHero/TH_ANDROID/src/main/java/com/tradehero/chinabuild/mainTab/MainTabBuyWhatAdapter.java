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

public class MainTabBuyWhatAdapter extends BaseAdapter {
    private LeaderboardUserDTOList mDtoList = new LeaderboardUserDTOList();
    private LayoutInflater inflater;

    @Inject public Lazy<PrettyTime> prettyTime;

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
        winRate.setText(item.winRatio*100+"%");
        TextView monthlyRate = (TextView) convertView.findViewById(R.id.monthly_rate);
        monthlyRate.setText(String.valueOf(item.monthlyRoi*100).substring(0,5)+"%");
        TextView securityName = (TextView) convertView.findViewById(R.id.security_name);
        securityName.setText(item.securityName);
        TextView tradePrice = (TextView) convertView.findViewById(R.id.trade_price);
        tradePrice.setText("成交价格 "+item.price);
        TextView tradeTime = (TextView) convertView.findViewById(R.id.trade_time);
        tradeTime.setText(prettyTime.get().formatUnrounded(item.dateTimeUtc));

        return convertView;
    }

    public void setItems(LeaderboardUserDTOList list) {
        mDtoList = list;
    }

    public void addItems(LeaderboardUserDTOList listData) {
        mDtoList.addAll(listData);
    }
}
