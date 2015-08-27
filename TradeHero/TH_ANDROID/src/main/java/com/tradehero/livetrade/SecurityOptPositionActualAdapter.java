package com.tradehero.livetrade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tradehero.livetrade.thirdPartyServices.haitong.SecurityOptPositionActualDTO;
import com.tradehero.th.R;

import java.util.ArrayList;

/**
 * Created by palmer on 15/7/17.
 */
public class SecurityOptPositionActualAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<SecurityOptPositionActualDTO> securityOptPositionDTOs = new ArrayList();

    private int color_up;
    private int color_down;

    public SecurityOptPositionActualAdapter(Context context){
        inflater = LayoutInflater.from(context);
        color_up = context.getResources().getColor(R.color.number_up);
        color_down = context.getResources().getColor(R.color.number_down);
    }

    public void addData(ArrayList<SecurityOptPositionActualDTO> securityOptPositionDTOs){
        this.securityOptPositionDTOs.clear();
        if(securityOptPositionDTOs != null){
            this.securityOptPositionDTOs.addAll(securityOptPositionDTOs);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return securityOptPositionDTOs.size();
    }

    @Override
    public SecurityOptPositionActualDTO getItem(int i) {
        return securityOptPositionDTOs.get(i);
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
        SecurityOptPositionActualDTO securityOptPositionDTO = getItem(i);
        if(securityOptPositionDTO!=null){
            holder.stockName.setText(securityOptPositionDTO.sec_name);
            holder.code.setText(securityOptPositionDTO.sec_code);
            if(securityOptPositionDTO.profit_ratio >= 0){
                holder.benefit.setTextColor(color_up);
                holder.percentageBenefit.setTextColor(color_up);
            } else {
                holder.benefit.setTextColor(color_down);
                holder.percentageBenefit.setTextColor(color_down);
            }
            holder.basePrice.setText(String.valueOf(securityOptPositionDTO.cost_price));
            holder.base.setText(DataUtils.keepInteger(securityOptPositionDTO.buy_money));
            String ratio = securityOptPositionDTO.profit_ratio + "%";
            holder.percentageBenefit.setText(ratio);
            holder.benefit.setText(DataUtils.keepInteger(securityOptPositionDTO.profit));
            holder.totalAccount.setText(DataUtils.keepInteger(securityOptPositionDTO.current_amt));
            holder.availableAccount.setText(DataUtils.keepInteger(securityOptPositionDTO.enable_amt));
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
