package com.tradehero.chinabuild.fragment.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tradehero.livetrade.DataUtils;
import com.tradehero.livetrade.data.subData.EntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustQueryDTO;
import com.tradehero.th.R;

public class SecurityOptMockActualQueryDelegationAdapter extends BaseAdapter{
    private LiveTradeEntrustQueryDTO mDto = new LiveTradeEntrustQueryDTO();
    private LayoutInflater inflater;

    public SecurityOptMockActualQueryDelegationAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDto.positions.size();
    }

    @Override
    public EntrustQueryDTO getItem(int i) {
        return mDto.positions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.security_opt_query_delegation_position_item, null);
        }
        EntrustQueryDTO item = getItem(i);
        ImageView flagImageView = (ImageView)convertView.findViewById(R.id.flag);
        if (item.entrustStatus == LiveTradeEntrustQueryDTO.ENTRUST_STATUS_WITHDRAWED) {
            flagImageView.setImageResource(R.drawable.cancel_deal);
        } else if (item.entrustStatus == LiveTradeEntrustQueryDTO.ENTRUST_STATUS_DEALED) {
            flagImageView.setImageResource(R.drawable.finish_deal);
        } else if (item.entrustStatus == LiveTradeEntrustQueryDTO.ENTRUST_STATUS_UNDEALED) {
            flagImageView.setImageResource(R.drawable.not_finish_deal);
        }
        TextView stockTitle = (TextView)convertView.findViewById(R.id.stock_name);
        stockTitle.setText(item.securityName);
        TextView stockID = (TextView)convertView.findViewById(R.id.stock_id);
        stockID.setText(item.securityId);
        TextView operation = (TextView)convertView.findViewById(R.id.operation);
        operation.setText(item.entrustName);
        TextView price = (TextView)convertView.findViewById(R.id.price);
        price.setText(DataUtils.keepTwoDecimal(item.entrustPrice));
        TextView numbers = (TextView)convertView.findViewById(R.id.numbers);
        numbers.setText(String.valueOf(item.entrustAmount));
        TextView timeDate = (TextView)convertView.findViewById(R.id.time_date);
        TextView timeTime = (TextView)convertView.findViewById(R.id.time_time);
        timeDate.setText(item.entrustDate);
        timeTime.setText(item.entrustTime);
        return convertView;
    }

    public void setItems(LiveTradeEntrustQueryDTO dto) {
        mDto = dto;
    }
}
