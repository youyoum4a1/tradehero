package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.TrendingAllSecurityListType;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;

public class SecurityListAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private SecurityCompactDTOList securityCompactDTOs;
    private int securityType = 0;//0:热门关注，1:热门持有，2:中国概念

    public SecurityListAdapter(Context context, SecurityCompactDTOList list, int securityType)
    {
        DaggerUtils.inject(this);
        this.context = context;
        this.securityCompactDTOs = list;
        this.securityType = securityType;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public SecurityListAdapter(Context context, int securityType)
    {
        DaggerUtils.inject(this);
        this.context = context;
        //this.securityCompactDTOs = list;
        this.securityType = securityType;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setType(int securityType)
    {
        this.securityType = securityType;
    }

    public void setSecurityList(SecurityCompactDTOList list)
    {
        this.securityCompactDTOs = list;
    }

    public void addItems(SecurityCompactDTOList listAdd)
    {
        this.securityCompactDTOs.addAll(listAdd);
    }

    @Override public int getCount()
    {
        return securityCompactDTOs == null ? 0 : securityCompactDTOs.size();
    }

    @Override public Object getItem(int i)
    {
        return securityCompactDTOs == null ? null : securityCompactDTOs.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        SecurityCompactDTO item = (SecurityCompactDTO) getItem(position);
        if (item != null)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.security_list_item, viewGroup, false);
                holder = new ViewHolder();
                holder.tvSecurityName = (TextView) convertView.findViewById(R.id.tvSecurityName);
                holder.tvSecurityExtroInfo = (TextView) convertView.findViewById(R.id.tvSecurityExtraInfo);
                holder.tvSecurityRate = (TextView) convertView.findViewById(R.id.tvSecurityRate);
                holder.tvSecurityPrice = (TextView) convertView.findViewById(R.id.tvSecurityPrice);
                holder.tvSecurityCurrency = (TextView) convertView.findViewById(R.id.tvSecurityCurrency);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvSecurityName.setText(item.name);
            if (securityType == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_RISE_PERCENT)
            {
                holder.tvSecurityExtroInfo.setVisibility(View.GONE);
                //holder.tvSecurityExtroInfo.setText(context.getResources().getString(R.string.people_watched, item.watchCount));
            }
            else  if (securityType == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_HOLD)
            {
                holder.tvSecurityExtroInfo.setText(context.getResources().getString(R.string.people_holded, item.holdCount));
            }
            else if (securityType == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_CHINA_CONCEPT)
            {
                holder.tvSecurityExtroInfo.setText(context.getResources().getString(R.string.security_market_cap_usd, "" + NumberDisplayUtils.getString(item.marketCapRefUSD)));
            }
            else if (securityType == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_COMPETITION
                    || securityType == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_SEARCH)
            {
                holder.tvSecurityExtroInfo.setVisibility(View.GONE);
                holder.tvSecurityExtroInfo.setText(context.getResources().getString(R.string.people_holded, item.holdCount));
            }

            //ROI
            if (item.risePercent != null)
            {
                THSignedNumber roi = THSignedPercentage.builder(item.risePercent * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                holder.tvSecurityRate.setText(roi.toString());
                holder.tvSecurityRate.setTextColor(context.getResources().getColor(roi.getColorResId()));
            }

            holder.tvSecurityPrice.setText(String.valueOf(item.lastPrice));
            holder.tvSecurityCurrency.setText(item.getCurrencyDisplay());
        }
        return convertView;
    }

    static class ViewHolder
    {
        //public ImageView localImageView = null;
        public TextView tvSecurityName = null;
        public TextView tvSecurityExtroInfo = null;
        public TextView tvSecurityRate = null;
        public TextView tvSecurityPrice = null;
        public TextView tvSecurityCurrency = null;
    }
}
