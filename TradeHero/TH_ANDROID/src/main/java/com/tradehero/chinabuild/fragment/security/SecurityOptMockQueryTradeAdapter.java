package com.tradehero.chinabuild.fragment.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tradehero.livetrade.DataUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.trade.ClosedTradeDTO;
import com.tradehero.th.api.trade.ClosedTradeDTOList;
import java.text.SimpleDateFormat;

public class SecurityOptMockQueryTradeAdapter extends BaseAdapter{
    private ClosedTradeDTOList mList = new ClosedTradeDTOList();
    private LayoutInflater inflater;
    private boolean mIsShowMore = false;

    public SecurityOptMockQueryTradeAdapter(Context context){
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
        operation.setText(item.quantity > 0 ? R.string.is_buy : R.string.is_sell);
        TextView price = (TextView)convertView.findViewById(R.id.price);
        price.setText(item.currencyDisplay + DataUtils.keepTwoDecimal(item.price));
        TextView numbers = (TextView)convertView.findViewById(R.id.numbers);
        numbers.setText(item.quantity < 0 ? String.valueOf(item.quantity * -1) : String.valueOf(item.quantity));
        TextView timeDate = (TextView)convertView.findViewById(R.id.time_date);
        TextView timeTime = (TextView)convertView.findViewById(R.id.time_time);
        if (item.closedAtUtc != null) {
            timeDate.setText(new SimpleDateFormat("yyyy/MM/dd").format(item.closedAtUtc));
            timeTime.setText(new SimpleDateFormat("HH:mm:ss").format(item.closedAtUtc));
        }

        return convertView;
    }

    public void setItems(ClosedTradeDTOList list) {
        mList = list;
    }

    public void setShowMore(boolean isShowMore) {
        mIsShowMore = isShowMore;
    }
}
