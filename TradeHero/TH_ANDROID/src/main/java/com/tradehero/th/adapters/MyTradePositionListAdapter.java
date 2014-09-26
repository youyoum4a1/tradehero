package com.tradehero.th.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.R;
import com.tradehero.th.fragments.chinabuild.data.PositionHeadItem;
import com.tradehero.th.fragments.chinabuild.data.PositionInterface;
import com.tradehero.th.fragments.chinabuild.data.PositionLockedItem;
import com.tradehero.th.fragments.chinabuild.data.SecurityPositionItem;
import com.tradehero.th.fragments.chinabuild.data.WatchPositionItem;
import com.tradehero.th.fragments.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.prefs.ShareDialogKey;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import javax.inject.Inject;

public class MyTradePositionListAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<PositionInterface> listData;
    private ArrayList<SecurityPositionItem> securityPositionList;//持仓（open）
    private ArrayList<SecurityPositionItem> securityPositionListClosed;//平仓（Close）
    private ArrayList<WatchPositionItem> watchPositionList;//自选股
    private boolean isLocked = false;
    @Inject @ShareDialogKey BooleanPreference mShareDialogKeyPreference;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

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

    public void setSecurityPositionList(ArrayList<SecurityPositionItem> list)
    {
        securityPositionList = list;
        doRefreshData();
    }

    public void setWatchPositionList(ArrayList<WatchPositionItem> list)
    {
        watchPositionList = list;
        doRefreshData();
    }

    private void doRefreshData()
    {
        listData = new ArrayList<PositionInterface>();

        if(isLocked)
        {
            //if (getSecurityPositionCount() > 0)
            //{
                listData.add(new PositionHeadItem("持仓"));
                listData.add(new PositionLockedItem());
            //}
        }
        else
        {
            if (getSecurityPositionCount() > 0)
            {
                listData.add(new PositionHeadItem(getHeadStrOfSecurityPosition()));
                listData.addAll(securityPositionList);
            }
        }

        if (getSecurityPositionClosedCount() > 0)
        {
            listData.add(new PositionHeadItem(getHeadStrOfSecurityClosedPosition()));
            listData.addAll(securityPositionListClosed);
        }

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
        PositionInterface item = (PositionInterface) getItem(position);
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
                THSignedNumber roi = THSignedPercentage.builder(((SecurityPositionItem) item).position.getROISinceInception() * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                tvSecurityRate.setText(roi.toString());
                tvSecurityRate.setTextColor(context.getResources().getColor(roi.getColorResId()));
                if (((SecurityPositionItem) item).position.getROISinceInception() * 100 > 10)
                {
                    if (mShareDialogKeyPreference.get())
                    {
                        mShareDialogKeyPreference.set(false);
                        mShareSheetTitleCache.set(context.getString(
                                R.string.share_amount_roi_value_summary));
                        ShareDialogFragment.showDialog(((FragmentActivity)context).getSupportFragmentManager(),
                                context.getString(R.string.share_amount_roi_value_title));
                    }
                }
                //price
                tvSecurityPrice.setText(String.valueOf((((SecurityPositionItem) item)).security.lastPrice));
                //currency
                tvSecurityCurrency.setText(((SecurityPositionItem) item).security.currencyDisplay);
                //extro
                Double pl = ((SecurityPositionItem) item).position.unrealizedPLRefCcy;
                if (pl == null)
                {
                    pl = 0.0;
                }
                THSignedNumber thPlSinceInception = THSignedMoney.builder(pl)
                        .withSign()
                        .signTypePlusMinusAlways()
                        .currency(((SecurityPositionItem) item).security.currencyDisplay)
                        .build();
                tvSecurityExtraInfo.setText(thPlSinceInception.toString());
                tvSecurityExtraInfo.setTextColor(context.getResources().getColor(
                        ColorUtils.getColorResourceIdForNumber(((SecurityPositionItem) item).position.unrealizedPLRefCcy)));
            }
            else if (item instanceof WatchPositionItem)
            {
                tvSecurityName.setText(((WatchPositionItem) item).watchlistPosition.securityDTO.name);

                //roi
                THSignedNumber roi = THSignedPercentage.builder(((WatchPositionItem) item).watchlistPosition.securityDTO.risePercent * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                tvSecurityRate.setText(roi.toString());
                tvSecurityRate.setTextColor(context.getResources().getColor(roi.getColorResId()));
                //price
                tvSecurityPrice.setText(String.valueOf((((WatchPositionItem) item)).watchlistPosition.securityDTO.lastPrice));
                //currency
                tvSecurityCurrency.setText(((WatchPositionItem) item).watchlistPosition.securityDTO.currencyDisplay);

                tvSecurityExtraInfo.setText("xxx人关注");
            }
        }

        return convertView;
    }
}
