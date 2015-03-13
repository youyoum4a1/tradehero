package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.chinabuild.data.*;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.prefs.ShareDialogKey;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;
import java.util.ArrayList;

/*
    我的交易持仓，平仓，自选股，列表
 */
public class MyTradePositionListAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<PositionInterface> listData;
    private ArrayList<SecurityPositionItem> securityPositionList = new ArrayList<>();//持仓（open）
    private ArrayList<SecurityPositionItem> securityPositionListClosed = new ArrayList<>();//平仓（Close）
    private ArrayList<WatchPositionItem> watchPositionList = new ArrayList<>();//自选股
    private boolean isLocked = false;
    @Inject @ShareDialogKey BooleanPreference mShareDialogKeyPreference;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    private int totalClosedAccount = 0;

    public MyTradePositionListAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSecurityPositionListLocked(boolean locked)
    {
        isLocked = locked;
        doRefreshData();
    }

    public void setSecurityPositionListClosed(ArrayList<SecurityPositionItem> list)
    {
        securityPositionListClosed = list;
        doRefreshData();
    }

    public void addSecurityPositionListClosed(ArrayList<SecurityPositionItem> list){
        if(list!=null && list.size() > 0){
            for(SecurityPositionItem item : list){
                if(!isContain(item)){
                    securityPositionListClosed.add(item);
                }
            }
            doRefreshData();
        }
    }

    private boolean isContain(SecurityPositionItem item){
        if(securityPositionListClosed == null || securityPositionListClosed.size() <=0){
            return false;
        }
        for(SecurityPositionItem oldItem :securityPositionListClosed ){
            if(oldItem.position == null || item.position == null){
                return false;
            }
            if(oldItem.position.id == item.position.id){
                return true;
            }
        }
        return false;
    }


    public void setSecurityPositionList(ArrayList<SecurityPositionItem> list)
    {
        securityPositionList = list;
        doRefreshData();
    }

    private void doRefreshData()
    {
        listData = new ArrayList<PositionInterface>();

        if (isLocked)
        {
            listData.add(new PositionHeadItem("持仓"));
            listData.add(new PositionLockedItem());
        }
        else
        {
            listData.add(new PositionHeadItem(getHeadStrOfSecurityPosition()));
            listData.addAll(securityPositionList);
        }

        listData.add(new PositionHeadItem(getHeadStrOfSecurityClosedPosition()));
        listData.addAll(securityPositionListClosed);

        if (getWatchPositionCount() > 0)
        {
            listData.add(new PositionHeadItem(getHeadStrOfWatchPosition()));
            listData.addAll(watchPositionList);
        }
        notifyDataSetChanged();
    }

    public String getHeadStrOfSecurityPosition()
    {
        return context.getResources().getString(R.string.security_position, getSecurityPositionCount());
    }

    public String getHeadStrOfSecurityClosedPosition()
    {
        return context.getResources().getString(R.string.security_position_closed, getSecurityPositionClosedCount());
    }

    public String getHeadStrOfWatchPosition()
    {
        return context.getResources().getString(R.string.watch_position, getWatchPositionCount());
    }

    public int getSecurityPositionClosedCount()
    {
        if(totalClosedAccount > 0){
            return totalClosedAccount;
        }
        return securityPositionListClosed == null ? 0 : securityPositionListClosed.size();
    }

    public int getSecurityPositionCount()
    {
        return securityPositionList == null ? 0 : securityPositionList.size();
    }

    public int getWatchPositionCount()
    {
        return watchPositionList == null ? 0 : watchPositionList.size();
    }

    @Override public int getCount()
    {
        return listData == null ? 0 : listData.size();
    }

    @Override public PositionInterface getItem(int i)
    {
        return listData.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return !(getItem(position) instanceof PositionHeadItem);
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        PositionInterface item = getItem(position);
        if (item instanceof PositionHeadItem)
        {
            convertView = inflater.inflate(R.layout.position_head_item, viewGroup, false);
            TextView tvHead = (TextView) convertView.findViewById(R.id.tvPositionHead);
            tvHead.setText(((PositionHeadItem) item).strHead);
        }
        else if (item instanceof PositionLockedItem)
        {
            convertView = inflater.inflate(R.layout.position_locked_item_new, viewGroup, false);
        }
        else
        {
            convertView = inflater.inflate(R.layout.position_security_watch_item, viewGroup, false);
            TextView tvSecurityName = (TextView) convertView.findViewById(R.id.tvSecurityName);
            TextView tvSecurityRate = (TextView) convertView.findViewById(R.id.tvSecurityRate);
            TextView tvSecurityPrice = (TextView) convertView.findViewById(R.id.tvSecurityPrice);
            TextView tvSecurityCurrency = (TextView) convertView.findViewById(R.id.tvSecurityCurrency);
            TextView tvSecurityExtraInfo = (TextView) convertView.findViewById(R.id.tvSecurityExtraInfo);

            if (item instanceof SecurityPositionItem)
            {
                //name
                tvSecurityName.setText(((SecurityPositionItem) item).security.name);
                //roi
                if(((SecurityPositionItem) item).position.getROISinceInception()!=null)
                {
                    THSignedNumber roi = THSignedPercentage.builder(((SecurityPositionItem) item).position.getROISinceInception() * 100)
                            .withSign()
                            .signTypeArrow()
                            .build();
                    tvSecurityRate.setText(roi.toString());
                    tvSecurityRate.setTextColor(context.getResources().getColor(roi.getColorResId()));
                }

                //price
                if((((SecurityPositionItem) item)).security.lastPrice!=null)
                {
                    tvSecurityPrice.setText(SecurityCompactDTO.getShortValue(((SecurityPositionItem) item).security.lastPrice));
                }


                //currency
                tvSecurityCurrency.setText(((SecurityPositionItem) item).security.getCurrencyDisplay());

                //extro
                //显示总盈亏
                Double pl = ((SecurityPositionItem) item).position.getTotalScoreOfTrade();

                if (pl == null)
                {
                    pl = 0.0;
                }

                THSignedNumber thPlSinceInception = THSignedMoney.builder(pl)
                        .withSign()
                        .signTypePlusMinusAlways()
                                //.currency(((SecurityPositionItem) item).security.getCurrencyDisplay())
                        .currency("$")
                        .build();
                tvSecurityExtraInfo.setText(thPlSinceInception.toString());
                tvSecurityExtraInfo.setTextColor(context.getResources().getColor(
                        ColorUtils.getColorResourceIdForNumber(pl)));
                tvSecurityExtraInfo.setVisibility(View.VISIBLE);
            }
            else if (item instanceof WatchPositionItem)
            {
                tvSecurityName.setText(((WatchPositionItem) item).watchlistPosition.securityDTO.name);

                //roi
                if(((WatchPositionItem) item).watchlistPosition.securityDTO.risePercent!=null)
                {
                    THSignedNumber roi = THSignedPercentage.builder(((WatchPositionItem) item).watchlistPosition.securityDTO.risePercent * 100)
                            .withSign()
                            .signTypeArrow()
                            .build();
                    tvSecurityRate.setText(roi.toString());
                    tvSecurityRate.setTextColor(context.getResources().getColor(roi.getColorResId()));
                }

                //price
                if((((WatchPositionItem) item)).watchlistPosition.securityDTO.lastPrice!=null)
                {
                    tvSecurityPrice.setText(SecurityCompactDTO.getShortValue((((WatchPositionItem) item)).watchlistPosition.securityDTO.lastPrice));
                }

                //currency
                tvSecurityCurrency.setText(((WatchPositionItem) item).watchlistPosition.securityDTO.getCurrencyDisplay());

                tvSecurityExtraInfo.setVisibility(View.GONE);
                tvSecurityExtraInfo.setText("xxx人关注");
            }
        }

        return convertView;
    }

    public void setTotalClosedAccount(int totalClosedAccount){
        this.totalClosedAccount = totalClosedAccount;
    }
}
