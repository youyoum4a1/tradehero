package com.tradehero.chinabuild.fragment.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.trade.ClosedTradeDTO;
import com.tradehero.th.api.trade.ClosedTradeDTOList;

public class SecurityOptMockActualQueryTradeAdapter extends BaseAdapter{
    private ClosedTradeDTOList mList = new ClosedTradeDTOList();
    private LayoutInflater inflater;
    private boolean mIsShowMore = false;

    public SecurityOptMockActualQueryTradeAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (!mIsShowMore && mList.size() > 3) {
            return 3;
        }
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
            convertView = inflater.inflate(R.layout.security_opt_query_trade_position_item, null);
        }
        ClosedTradeDTO item = getItem(i);
        TextView stockTitle = (TextView)convertView.findViewById(R.id.stock_name);
        stockTitle.setText(item.securityName);
        TextView stockID = (TextView)convertView.findViewById(R.id.stock_id);
        stockID.setText(item.securityId);
        TextView operation = (TextView)convertView.findViewById(R.id.operation);
        operation.setText(item.entrust_name);
        TextView price = (TextView)convertView.findViewById(R.id.price);
        price.setText(item.business_price);
        TextView numbers = (TextView)convertView.findViewById(R.id.numbers);
        numbers.setText(item.business_amt);
        TextView timeDate = (TextView)convertView.findViewById(R.id.time_date);
        TextView timeTime = (TextView)convertView.findViewById(R.id.time_time);
        timeDate.setText(item.business_date);
        timeTime.setText(item.business_time);

        return convertView;
    }

    public void setItems(ClosedTradeDTOList list) {
        mList = list;
    }

    public void setShowMore(boolean isShowMore) {
        mIsShowMore = isShowMore;
    }
}
