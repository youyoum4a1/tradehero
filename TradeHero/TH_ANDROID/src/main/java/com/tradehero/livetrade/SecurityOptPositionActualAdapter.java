package com.tradehero.livetrade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tradehero.livetrade.data.LiveTradePositionDTO;
import com.tradehero.th.R;
import com.tradehero.livetrade.data.subData.PositionDTO;


/**
 * Created by palmer on 15/7/17.
 */
public class SecurityOptPositionActualAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private LiveTradePositionDTO positionDTO;

    private int color_up;
    private int color_down;

    public SecurityOptPositionActualAdapter(Context context){
        inflater = LayoutInflater.from(context);
        color_up = context.getResources().getColor(R.color.number_up);
        color_down = context.getResources().getColor(R.color.number_down);
    }

    public void addData(LiveTradePositionDTO positionDTO){
        this.positionDTO = positionDTO;

        notifyDataSetChanged();
    }

    public LiveTradePositionDTO getData()
    {
        return positionDTO;
    }

    @Override
    public int getCount() {
        if (positionDTO == null) {
            return 0;
        }
        return positionDTO.positions.size();
    }

    @Override
    public PositionDTO getItem(int i) {
        return positionDTO.positions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.security_opt_position_item, null);
            holder = new Holder();
            holder.stockName = (TextView)convertView.findViewById(R.id.stock_name);
            holder.code = (TextView)convertView.findViewById(R.id.stock_code);
            holder.benefit = (TextView)convertView.findViewById(R.id.stock_benefit);
            holder.percentageBenefit = (TextView)convertView.findViewById(R.id.stock_benefit_percent);
            holder.base = (TextView)convertView.findViewById(R.id.stock_base_a);
            holder.basePrice = (TextView)convertView.findViewById(R.id.stock_base_b);
            holder.totalAccount = (TextView)convertView.findViewById(R.id.stock_number);
            holder.availableAccount = (TextView)convertView.findViewById(R.id.stock_available);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }
        PositionDTO dto = getItem(i);
        if(dto!=null){
            holder.stockName.setText(dto.stockName);
            holder.code.setText(dto.stockCode);
            if(dto.profitRatio >= 0){
                holder.benefit.setTextColor(color_up);
                holder.percentageBenefit.setTextColor(color_up);
            } else {
                holder.benefit.setTextColor(color_down);
                holder.percentageBenefit.setTextColor(color_down);
            }
            holder.basePrice.setText(String.valueOf(dto.price));
            holder.base.setText(DataUtils.keepInteger(dto.marketValue));
            String ratio = String.format("%.2f", dto.profitRatio) + "%";
            holder.percentageBenefit.setText(ratio);
            holder.benefit.setText(DataUtils.keepInteger(dto.profit));
            holder.totalAccount.setText(DataUtils.keepInteger(dto.currentAmount));
            holder.availableAccount.setText(DataUtils.keepInteger(dto.enableAmount));
        }

        return convertView;
    }

    class Holder {
        public TextView stockName;
        public TextView code;
        public TextView benefit;
        public TextView percentageBenefit;
        public TextView base;
        public TextView basePrice;
        public TextView totalAccount;
        public TextView availableAccount;
    }

}
