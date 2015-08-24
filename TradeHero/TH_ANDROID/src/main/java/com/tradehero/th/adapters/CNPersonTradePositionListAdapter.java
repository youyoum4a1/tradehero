package com.tradehero.th.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tradehero.chinabuild.data.SecurityPositionItem;
import com.tradehero.chinabuild.data.WatchPositionItem;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.prefs.ShareDialogKey;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by palmer on 14/11/17.
 */
public class CNPersonTradePositionListAdapter extends BaseExpandableListAdapter {

    private String[] generalsTypes;

    private Activity activity;
    private LayoutInflater inflater;

    public static int GROUP_SECURITY_POSITION = 0;
    public static int GROUP_WATCH_POSITION = 1;
    public static int GROUP_SECURITY_POSITION_CLOSED = 2;
    public static int GROUP_DATA_LOADING = 3;
    public static int GROUP_EMPTY = 4;

    private ArrayList<SecurityPositionItem> securityPositionList = new ArrayList<>();//持仓（open）
    private ArrayList<SecurityPositionItem> securityPositionListClosed = new ArrayList<>();//平仓（Close）
    private ArrayList<WatchPositionItem> watchPositionList = new ArrayList<>();//自选股

    private static int pendingInitDataSetNum = 2;

    @Inject @ShareDialogKey BooleanPreference mShareDialogKeyPreference;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    public CNPersonTradePositionListAdapter(Activity activity) {
        DaggerUtils.inject(this);
        this.activity = activity;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initGeneralTypes();
    }

    @Override
    public int getGroupCount() {
        return generalsTypes.length;
    }

    @Override
    public int getChildrenCount(int i) {
        if (i == GROUP_SECURITY_POSITION) {
            return getSecurityPositionCount();
        }
        if (i == GROUP_WATCH_POSITION) {
            return getWatchPositionCount();
        }
        if (i == GROUP_SECURITY_POSITION_CLOSED) {
            return getSecurityPositionClosedCount();
        }
        return 0;
    }

    public int getTotalCount() {
        return getSecurityPositionCount() + getSecurityPositionClosedCount() + getWatchPositionCount();
    }

    @Override
    public Object getGroup(int i) {
        return generalsTypes[i];
    }

    @Override
    public Object getChild(int i, int i2) {
        if (i == GROUP_SECURITY_POSITION) {
            return securityPositionList.get(i2);
        }
        if (i == GROUP_WATCH_POSITION) {
            return watchPositionList.get(i2);
        }
        if (i == GROUP_SECURITY_POSITION_CLOSED) {
            return securityPositionListClosed.get(i2);
        }
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public synchronized View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if(groupPosition == GROUP_EMPTY){
            convertView = inflater.inflate(R.layout.empty_layout, parent, false);
            LinearLayout llEmpty = (LinearLayout)convertView.findViewById(R.id.llEmpty);

            if(getTotalCount() != 0 || pendingInitDataSetNum > 0){
                llEmpty.setVisibility(View.GONE);
            }else{
                llEmpty.setVisibility(View.VISIBLE);
                Button actionBtn = (Button)convertView.findViewById(R.id.btnEmptyAction);
                actionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle args = new Bundle();
                        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, SearchUnitFragment.class.getName());
                        ActivityHelper.launchDashboard(activity, args);
                    }
                });
            }
        } else if (groupPosition == GROUP_DATA_LOADING) {
            convertView = inflater.inflate(R.layout.dataloading, parent, false);

            if(pendingInitDataSetNum <= 0){
                convertView.setVisibility(View.GONE);
            }else{
                convertView.setVisibility(View.VISIBLE);
            }

        } else {
            convertView = inflater.inflate(R.layout.position_head_plus_item, parent, false);
            TextView tvHead = (TextView) convertView.findViewById(R.id.tvPositionHead);
            tvHead.setText(generalsTypes[groupPosition]);
            ImageView ivHead = (ImageView) convertView.findViewById(R.id.ivPositionHead);
            if (isExpanded) {
                ivHead.setBackgroundResource(R.drawable.icon_arrow_down_gray);
            } else {
                ivHead.setBackgroundResource(R.drawable.icon_arrow_up_gray);
            }
            int childAccount = getChildrenCount(groupPosition);
            if (childAccount <= 0) {
                tvHead.setVisibility(View.GONE);
                ivHead.setVisibility(View.GONE);
            } else {
                tvHead.setVisibility(View.VISIBLE);
                ivHead.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if(groupPosition == GROUP_DATA_LOADING || groupPosition == GROUP_EMPTY){
            return null;
        }
        convertView = inflater.inflate(R.layout.position_security_watch_item, parent, false);
        TextView tvSecurityName = (TextView) convertView.findViewById(R.id.tvSecurityName);
        TextView tvSecurityRate = (TextView) convertView.findViewById(R.id.tvSecurityRate);
        TextView tvSecurityPrice = (TextView) convertView.findViewById(R.id.tvSecurityPrice);
        TextView tvSecurityCurrency = (TextView) convertView.findViewById(R.id.tvSecurityCurrency);
        TextView tvSecurityExtraInfo = (TextView) convertView.findViewById(R.id.tvSecurityExtraInfo);
        if (groupPosition == GROUP_SECURITY_POSITION || groupPosition == GROUP_SECURITY_POSITION_CLOSED) {
            SecurityPositionItem item = null;
            if (groupPosition == GROUP_SECURITY_POSITION) {
                item = securityPositionList.get(childPosition);
            }
            if (groupPosition == GROUP_SECURITY_POSITION_CLOSED) {
                item = securityPositionListClosed.get(childPosition);
            }
            //name
            tvSecurityName.setText(item.security.name);
            //roi
            if (item.position.getROISinceInception() != null) {
                THSignedNumber roi = THSignedPercentage.builder(item.position.getROISinceInception() * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                tvSecurityRate.setText(roi.toString());
                tvSecurityRate.setTextColor(activity.getResources().getColor(roi.getColorResId()));
            }
            //price
            if (item.security.lastPrice != null) {
                tvSecurityPrice.setText(SecurityCompactDTO.getShortValue(item.security.lastPrice));
            }else if(item.security.previousClose != null){
                tvSecurityPrice.setText(SecurityCompactDTO.getShortValue(item.security.previousClose));
            }
            //currency
            tvSecurityCurrency.setText(item.security.getCurrencyDisplay());
            //extro
            //显示总盈亏
            Double pl = item.position.getTotalScoreOfTrade();
            if (pl == null) {
                pl = 0.0;
            }
            THSignedNumber thPlSinceInception = THSignedMoney.builder(pl)
                    .withSign()
                    .signTypePlusMinusAlways()
                    .currency("$")
                    .build();
            tvSecurityExtraInfo.setText(thPlSinceInception.toString());
            tvSecurityExtraInfo.setTextColor(activity.getResources().getColor(
                    ColorUtils.getColorResourceIdForNumber(pl)));
            tvSecurityExtraInfo.setVisibility(View.VISIBLE);
        }
        if (groupPosition == GROUP_WATCH_POSITION) {
            WatchPositionItem item = watchPositionList.get(childPosition);
            tvSecurityName.setText(item.watchlistPosition.securityDTO.name);

            //roi
            if (item.watchlistPosition.securityDTO.risePercent != null) {
                THSignedNumber roi = THSignedPercentage.builder(item.watchlistPosition.securityDTO.risePercent * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                tvSecurityRate.setText(roi.toString());
                tvSecurityRate.setTextColor(activity.getResources().getColor(roi.getColorResId()));
            }

            //price
            if (item.watchlistPosition.securityDTO.lastPrice != null) {
                tvSecurityPrice.setText(SecurityCompactDTO.getShortValue(item.watchlistPosition.securityDTO.lastPrice));
            }else if(item.watchlistPosition.securityDTO.previousClose != null){
                tvSecurityPrice.setText(SecurityCompactDTO.getShortValue(item.watchlistPosition.securityDTO.previousClose));
            }

            //currency
            tvSecurityCurrency.setText(item.watchlistPosition.securityDTO.getCurrencyDisplay());

            tvSecurityExtraInfo.setVisibility(View.GONE);
            tvSecurityExtraInfo.setText("xxx人关注");
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public void setSecurityPositionListClosed(ArrayList<SecurityPositionItem> list) {
        if (list == null) {
            securityPositionListClosed = new ArrayList<>();
        } else {
            securityPositionListClosed = list;
        }
        initGeneralTypes();
        notifyDataSetChanged();
    }

    public void setSecurityPositionList(ArrayList<SecurityPositionItem> list) {
        if (list == null) {
            securityPositionList = new ArrayList<>();
        } else {
            securityPositionList = list;
        }
        initGeneralTypes();
        notifyDataSetChanged();
    }

    public void setWatchPositionList(ArrayList<WatchPositionItem> list) {
        if (list == null) {
            watchPositionList = new ArrayList<>();
        } else {
            watchPositionList = list;
        }
        initGeneralTypes();
        notifyDataSetChanged();
    }

    public int getWatchPositionCount() {
        return watchPositionList == null ? 0 : watchPositionList.size();
    }

    public int getSecurityPositionClosedCount() {
        return securityPositionListClosed == null ? 0 : securityPositionListClosed.size();
    }

    public int getSecurityPositionCount() {
        return securityPositionList == null ? 0 : securityPositionList.size();
    }

    private void initGeneralTypes() {
        generalsTypes = new String[]{
                activity.getResources().getString(R.string.security_position, getSecurityPositionCount()),
                activity.getResources().getString(R.string.watch_position, getWatchPositionCount()),
                activity.getResources().getString(R.string.security_position_closed, getSecurityPositionClosedCount()),
                activity.getResources().getString(R.string.trading_hint_a),
                activity.getResources().getString(R.string.goto_choose_stock, getSecurityPositionClosedCount())
        };
    }

    public synchronized void setWatchlistDataInitDone() {
        pendingInitDataSetNum --;
    }

    public synchronized void setPositionDataInitDone() {
        pendingInitDataSetNum --;
    }
}
