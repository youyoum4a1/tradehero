package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.thm.R;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import timber.log.Timber;

public class SettingsTransactionHistoryAdapter extends BaseAdapter
{
    protected final LayoutInflater inflater;
    protected final Context context;
    private final int layoutResourceId;

    private List<UserTransactionHistoryDTO> items;
    private final DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public SettingsTransactionHistoryAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super();
        this.items = new ArrayList<>();
        this.context = context;
        this.inflater = inflater;
        this.layoutResourceId = layoutResourceId;
    }

    public void setItems(List<UserTransactionHistoryDTO> items)
    {
        this.items = items;
    }

    public void addItem(UserTransactionHistoryDTO item)
    {
        if (this.items != null)
        {
            this.items.add(item);
        }
    }

    @Override public int getCount()
    {
        Timber.d("getCount");
        return items != null ? items.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        Timber.d("getItem %d", i);
        return items != null ? items.get(i) : null;
    }

    @Override public long getItemId(int i)
    {
        Timber.d("getItemId %d", i);
        return i;
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        Timber.d("getView %d", position);
        if (convertView == null)
        {
            convertView = inflater.inflate(layoutResourceId, viewGroup, false);
        }

        UserTransactionHistoryDTO item = (UserTransactionHistoryDTO) getItem(position);

        TextView transactionDate = (TextView) convertView.findViewById(R.id.transaction_date);
        TextView transactionTime = (TextView) convertView.findViewById(R.id.transaction_time);

        if (item.createdAtUtc == null)
        {
            transactionDate.setText("");
            transactionTime.setText("");
        }
        else
        {
            transactionDate.setText(dateFormat.format(item.createdAtUtc));
            transactionTime.setText(timeFormat.format(item.createdAtUtc));
        }

        TextView transactionBalance = (TextView) convertView.findViewById(R.id.transaction_balance);
        String balance = item.balance != null ? item.balance.toString() : "-";
        transactionBalance.setText(balance);

        TextView transactionComment = (TextView) convertView.findViewById(R.id.transaction_comment);
        transactionComment.setText(item.comment);

        return convertView;
    }

    @Override public void notifyDataSetChanged()
    {
        Timber.d("notifyDataSetChanged");
        super.notifyDataSetChanged();
    }
}
