package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.api.market.SectorCompactDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSpinnerIconAdapterNew;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import org.jetbrains.annotations.NotNull;

public class OnBoardPickExchangeSectorViewHolder
{
    @InjectView(R.id.spinner_exchange) Spinner exchangeSpinner;
    @InjectView(R.id.spinner_sector) Spinner sectorSpinner;
    @InjectView(R.id.spinner_country) Spinner countrySpinner;

    @NotNull Context context;
    @NotNull TrendingFilterSpinnerIconAdapterNew exchangeAdapter;
    @NotNull SectorSpinnerAdapterNew sectorAdapter;
    @NotNull CountrySpinnerAdapterNew countryAdapter;

    public OnBoardPickExchangeSectorViewHolder(
            @NotNull Context context)
    {
        super();
        this.context = context;
        exchangeAdapter = new TrendingFilterSpinnerIconAdapterNew(context, R.layout.trending_filter_spinner_item);
        exchangeAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);
        sectorAdapter = new SectorSpinnerAdapterNew(context, R.layout.sector_spinner_item);
        sectorAdapter.setDropDownViewResource(R.layout.sector_spinner_item);
        countryAdapter = new CountrySpinnerAdapterNew(context, R.layout.country_filter_spinner_item);
        countryAdapter.setDropDownViewResource(R.layout.country_spinner_dropdown_item);
        countryAdapter.addAll(Country.values());
    }

    public void attachView(View view)
    {
        ButterKnife.inject(this, view);
        exchangeSpinner.setAdapter(exchangeAdapter);
        sectorSpinner.setAdapter(sectorAdapter);
        countrySpinner.setAdapter(countryAdapter);
    }

    public void detachView()
    {
        ButterKnife.reset(this);
    }

    public void setExchangeSector(@NotNull ExchangeSectorCompactListDTO exchangeSectorCompactListDTO)
    {
        exchangeAdapter.clear();
        exchangeAdapter.addAll(new ExchangeCompactSpinnerDTOList(
                context.getResources(),
                exchangeSectorCompactListDTO.exchanges));
        exchangeAdapter.notifyDataSetChanged();

        sectorAdapter.clear();
        sectorAdapter.addAll(exchangeSectorCompactListDTO.sectors);
        sectorAdapter.notifyDataSetChanged();
    }

    public OnBoardPrefDTO getOnBoardPrefs()
    {
        return new OnBoardPrefDTO(
                (ExchangeCompactDTO) exchangeSpinner.getSelectedItem(),
                (SectorCompactDTO) sectorSpinner.getSelectedItem(),
                (Country) countrySpinner.getSelectedItem());
    }
}
