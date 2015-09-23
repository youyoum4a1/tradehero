package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tradehero.livetrade.DataUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;

public class PositionTradeListAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private TradeDTOList tradeDTOs;

    public PositionTradeListAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setTradeList(TradeDTOList list)
    {
        this.tradeDTOs = list;
        notifyDataSetChanged();
    }

    public void addItems(TradeDTOList listAdd)
    {
        this.tradeDTOs.addAll(listAdd);
    }

    @Override public int getCount()
    {
        return tradeDTOs == null ? 0 : tradeDTOs.size();
    }

    @Override public Object getItem(int i)
    {
        return tradeDTOs == null ? null : tradeDTOs.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        TradeDTO item = (TradeDTO) getItem(position);
        if (item != null)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.position_tradelist_item, viewGroup, false);
                holder = new ViewHolder();
                holder.tvTradeTime = (TextView) convertView.findViewById(R.id.tvTradeTime);
                holder.tvTradeOperater = (TextView) convertView.findViewById(R.id.tvTradeOperater);
                holder.tvTradeQuantity = (TextView) convertView.findViewById(R.id.tvTradeQuantity);
                holder.tvTradePrice = (TextView) convertView.findViewById(R.id.tvTradePrice);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvTradeOperater.setText(item.isBuy() ? "购买" : "出售");
            holder.tvTradeQuantity.setText(item.displayTradeQuantity() + " x ");
            holder.tvTradePrice.setText(item.getCurrencyDisplay() + DataUtils.keepTwoDecimal(item.getUnitPriceCurrency()));
            holder.tvTradeTime.setText(DateUtils.getFormattedUtcDate(context.getResources(), item.dateTime));
        }
        return convertView;
    }

    static class ViewHolder
    {
        public TextView tvTradeTime = null;
        public TextView tvTradeOperater = null;
        public TextView tvTradeQuantity = null;
        public TextView tvTradePrice = null;
    }
}
