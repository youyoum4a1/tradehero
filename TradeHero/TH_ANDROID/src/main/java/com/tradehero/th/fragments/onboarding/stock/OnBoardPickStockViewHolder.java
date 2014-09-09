package com.tradehero.th.fragments.onboarding.stock;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class OnBoardPickStockViewHolder
{
    @InjectView(R.id.stocks_list) AbsListView stockListView;
    @InjectView(R.id.switcher_stock) ViewSwitcher switcher;

    @NotNull DTOAdapterNew<SelectableSecurityDTO> selectedStocksAdapter;

    //<editor-fold desc="Constructors">
    public OnBoardPickStockViewHolder(@NotNull Context context)
    {
        super();
        selectedStocksAdapter = new ArrayDTOAdapterNew<SelectableSecurityDTO, SecuritySelectableViewRelative>(
                context,
                R.layout.selectable_security_item);
    }
    //</editor-fold>

    public void attachView(View view)
    {
        ButterKnife.inject(this, view);
        switcher.setDisplayedChild(0);
        stockListView.setAdapter(selectedStocksAdapter);
    }

    public void detachView()
    {
        ButterKnife.reset(this);
    }

    public void setStocks(@NotNull List<SecurityCompactDTO> securityCompactDTOs)
    {
        List<SelectableSecurityDTO> list = new ArrayList<>();
        for (SecurityCompactDTO securityCompactDTO : securityCompactDTOs)
        {
            SelectableSecurityDTO item = new SelectableSecurityDTO(securityCompactDTO);
            item.selected = true;
            list.add(item);
        }
        selectedStocksAdapter.clear();
        selectedStocksAdapter.addAll(list);
        selectedStocksAdapter.notifyDataSetChanged();
        switcher.setDisplayedChild(1);
    }

    @OnItemClick(R.id.stocks_list)
    void onItemClick(
            AdapterView<?> adapterView,
            @SuppressWarnings("UnusedParameters") View view,
            int position,
            @SuppressWarnings("UnusedParameters") long l)
    {
        SelectableSecurityDTO value = (SelectableSecurityDTO) adapterView.getItemAtPosition(position);
        value.selected = !value.selected;
        selectedStocksAdapter.notifyDataSetChanged();
    }

    public @NotNull SecurityCompactDTOList getSelectedStocks()
    {
        SecurityCompactDTOList selected = new SecurityCompactDTOList();
        SelectableSecurityDTO value;
        for (int position = 0; position < selectedStocksAdapter.getCount(); position++)
        {
            value = selectedStocksAdapter.getItem(position);
            if (value.selected)
            {
                selected.add(value.value);
            }
        }
        return selected;
    }
}
