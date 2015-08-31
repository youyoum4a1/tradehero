package com.tradehero.chinabuild.fragment.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tradehero.livetrade.data.LiveTradePendingEntrustQueryDTO;
import com.tradehero.livetrade.data.subData.PendingEntrustQueryDTO;
import com.tradehero.th.R;

public class SecurityOptMockActualDelegationAdapter extends BaseAdapter{
    private LiveTradePendingEntrustQueryDTO mDto = new LiveTradePendingEntrustQueryDTO();
    private LayoutInflater inflater;
    private int mSelectedPosition = -1;

    public SecurityOptMockActualDelegationAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDto.positions.size();
    }

    @Override
    public PendingEntrustQueryDTO getItem(int i) {
        return mDto.positions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.security_opt_delegation_position_item, null);
        }
        PendingEntrustQueryDTO item = getItem(i);
        ImageView choiceImageView = (ImageView)convertView.findViewById(R.id.choice);
        if (mSelectedPosition == i) {
            choiceImageView.setImageResource(R.drawable.choice);
        } else {
            choiceImageView.setImageResource(R.drawable.no_choice);
        }
        TextView stockTitle = (TextView)convertView.findViewById(R.id.stock_name);
        stockTitle.setText(item.securityName);
        TextView stockID = (TextView)convertView.findViewById(R.id.stock_id);
        stockID.setText(item.securityId);
        TextView operation = (TextView)convertView.findViewById(R.id.operation);
        operation.setText(item.entrustName);
        TextView price = (TextView)convertView.findViewById(R.id.price);
        price.setText(String.valueOf(item.entrustPrice));
        TextView numbers = (TextView)convertView.findViewById(R.id.numbers);
        numbers.setText(String.valueOf(item.entrustAmount));
        TextView timeDate = (TextView)convertView.findViewById(R.id.time_date);
        TextView timeTime = (TextView) convertView.findViewById(R.id.time_time);
        timeDate.setText(item.entrustDate);
        timeTime.setText(item.entrustTime);

        return convertView;
    }

    public void setItems(LiveTradePendingEntrustQueryDTO dto) {
        mDto = dto;
    }

    public void setSelectedItem(int position) {
        if (mSelectedPosition == position) {
            mSelectedPosition = -1;
        } else {
            mSelectedPosition = position;
        }
    }
}
