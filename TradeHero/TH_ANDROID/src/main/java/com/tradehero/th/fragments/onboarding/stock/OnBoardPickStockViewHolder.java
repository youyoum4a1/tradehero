package com.tradehero.th.fragments.onboarding.stock;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
    @InjectView(android.R.id.list) AbsListView stockListView;

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

    void attachView(View view)
    {
        ButterKnife.inject(this, view);
        stockListView.setAdapter(selectedStocksAdapter);
    }

    void detachView()
    {
        ButterKnife.reset(this);
    }

    void setStocks(@NotNull List<SecurityCompactDTO> securityCompactDTOs)
    {
        List<SelectableSecurityDTO> list = new ArrayList<>();
        for (SecurityCompactDTO securityCompactDTO : securityCompactDTOs)
        {
            list.add(new SelectableSecurityDTO(securityCompactDTO));
        }
        selectedStocksAdapter.clear();
        selectedStocksAdapter.addAll(list);
        selectedStocksAdapter.notifyDataSetChanged();
    }

    @OnItemClick(android.R.id.list)
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

    @NotNull SecurityCompactDTOList getSelectedStocks()
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
