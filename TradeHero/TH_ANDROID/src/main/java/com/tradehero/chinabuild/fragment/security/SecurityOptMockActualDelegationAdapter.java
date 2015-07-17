package com.tradehero.chinabuild.fragment.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.trade.ClosedTradeDTO;
import com.tradehero.th.api.trade.ClosedTradeDTOList;

public class SecurityOptMockActualDelegationAdapter extends BaseAdapter{
    private ClosedTradeDTOList mList = new ClosedTradeDTOList();
    private LayoutInflater inflater;
    private int mSelectedPosition = -1;

    public SecurityOptMockActualDelegationAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ClosedTradeDTO getItem(int i) {
        return mList.get(i);
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
        ClosedTradeDTO item = getItem(i);
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
        operation.setText(item.entrust_name);
        TextView price = (TextView)convertView.findViewById(R.id.price);
        price.setText(item.entrust_price);
        TextView numbers = (TextView)convertView.findViewById(R.id.numbers);
        numbers.setText(item.entrust_amt);
        TextView timeDate = (TextView)convertView.findViewById(R.id.time_date);
        TextView timeTime = (TextView) convertView.findViewById(R.id.time_time);
        timeDate.setText(item.entrust_date);
        timeTime.setText(item.entrust_time);

        return convertView;
    }

    public void setItems(ClosedTradeDTOList list) {
        mList = list;
    }

    public void setSelectedItem(int position) {
        if (mSelectedPosition == position) {
            mSelectedPosition = -1;
        } else {
            mSelectedPosition = position;
        }
    }
}
