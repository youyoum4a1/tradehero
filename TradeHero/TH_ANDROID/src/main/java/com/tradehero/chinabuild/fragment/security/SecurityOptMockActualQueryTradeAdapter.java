package com.tradehero.chinabuild.fragment.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tradehero.livetrade.DataUtils;
import com.tradehero.livetrade.data.subData.DealQueryDTO;
import com.tradehero.livetrade.data.LiveTradeDealQueryDTO;
import com.tradehero.th.R;

public class SecurityOptMockActualQueryTradeAdapter extends BaseAdapter{
    private LiveTradeDealQueryDTO mDto = new LiveTradeDealQueryDTO();
    private LayoutInflater inflater;
    private boolean mIsShowMore = false;

    public SecurityOptMockActualQueryTradeAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (!mIsShowMore && mDto.positions.size() > 3) {
            return 3;
        }
        return mDto.positions.size();
    }

    @Override
    public DealQueryDTO getItem(int i) {
        return mDto.positions.get(i);
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
        DealQueryDTO item = getItem(i);
        TextView stockTitle = (TextView)convertView.findViewById(R.id.stock_name);
        stockTitle.setText(item.securityName);
        TextView stockID = (TextView)convertView.findViewById(R.id.stock_id);
        stockID.setText(item.securityId);
        TextView operation = (TextView)convertView.findViewById(R.id.operation);
        operation.setText(item.entrustName);
        TextView price = (TextView)convertView.findViewById(R.id.price);
        price.setText(DataUtils.keepTwoDecimal(item.businessPrice));
        TextView numbers = (TextView)convertView.findViewById(R.id.numbers);
        numbers.setText(String.valueOf(item.businessAmount));
        TextView timeDate = (TextView)convertView.findViewById(R.id.time_date);
        TextView timeTime = (TextView)convertView.findViewById(R.id.time_time);
        timeDate.setText(item.businessDate);
        timeTime.setText(item.businessTime);

        return convertView;
    }

    public void setItems(LiveTradeDealQueryDTO dto) {
        mDto = dto;
    }

    public void setShowMore(boolean isShowMore) {
        mIsShowMore = isShowMore;
    }
}
