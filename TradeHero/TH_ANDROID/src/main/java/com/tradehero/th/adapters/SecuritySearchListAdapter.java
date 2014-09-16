package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th2.R;

/*
搜索出来的股票显示
 */
public class SecuritySearchListAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private SecurityCompactDTOList securityCompactDTOs;

    public SecuritySearchListAdapter(Context context, SecurityCompactDTOList list)
    {
        DaggerUtils.inject(this);
        this.context = context;
        this.securityCompactDTOs = list;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SecuritySearchListAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                convertView = inflater.inflate(R.layout.security_search_list_item, viewGroup, false);
                holder = new ViewHolder();
                holder.tvSecurityName = (TextView) convertView.findViewById(R.id.tvSecurityName);
                holder.tvSecurityRate = (TextView) convertView.findViewById(R.id.tvSecurityRate);
                holder.tvSecurityExchange = (TextView) convertView.findViewById(R.id.tvSecurityExchange);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvSecurityName.setText(item.name);
            holder.tvSecurityExchange.setText(item.getExchangeSymbol());
            if (item.risePercent != null)
            {
                THSignedNumber roi = THSignedPercentage.builder(item.risePercent * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                holder.tvSecurityRate.setText(roi.toString());
                holder.tvSecurityRate.setTextColor(context.getResources().getColor(roi.getColorResId()));
            }
            else
            {
                holder.tvSecurityRate.setText("");
            }
        }
        return convertView;
    }

    static class ViewHolder
    {
        public TextView tvSecurityName = null;
        public TextView tvSecurityExchange = null;
        public TextView tvSecurityRate = null;
    }
}
