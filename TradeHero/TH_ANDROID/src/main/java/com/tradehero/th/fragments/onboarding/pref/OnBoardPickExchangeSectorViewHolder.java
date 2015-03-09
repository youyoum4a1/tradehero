package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.internal.util.Predicate;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.api.market.KnownSectors;
import com.tradehero.th.api.market.SectorCompactDTO;
import com.tradehero.th.api.market.SectorCompactDTOList;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.market.ExchangeSpinner;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSpinnerIconAdapterNew;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import com.tradehero.th.persistence.market.ExchangeMarketPreference;

public class OnBoardPickExchangeSectorViewHolder
{
    private static final int DEFAULT_SECTOR_ID = KnownSectors.SECTOR_ID_TECHNOLOGY;

    @InjectView(R.id.spinner_exchange) ExchangeSpinner exchangeSpinner;
    @InjectView(R.id.spinner_sector) Spinner sectorSpinner;

    @NonNull Context context;
    @NonNull ExchangeMarketPreference preferredMarketCountry;
    @NonNull StringPreference onBoardExchangePref;
    @NonNull TrendingFilterSpinnerIconAdapterNew exchangeAdapter;
    @NonNull SectorSpinnerAdapterNew sectorAdapter;

    @Nullable ExchangeSectorCompactListDTO exchangeSectorCompacts;
    @Nullable ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs;
    @Nullable UserProfileDTO userProfile;

    //<editor-fold desc="Constructors">
    public OnBoardPickExchangeSectorViewHolder(
            @NonNull Context context,
            @NonNull ExchangeMarketPreference preferredMarketCountry,
            @NonNull StringPreference onBoardExchangePref)
    {
        super();
        this.context = context;
        this.preferredMarketCountry = preferredMarketCountry;
        this.onBoardExchangePref = onBoardExchangePref;
        exchangeAdapter = new TrendingFilterSpinnerIconAdapterNew(context, R.layout.trending_filter_spinner_item);
        exchangeAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);
        sectorAdapter = new SectorSpinnerAdapterNew(context, R.layout.sector_spinner_item);
        sectorAdapter.setDropDownViewResource(R.layout.sector_spinner_dropdown_item);
    }
    //</editor-fold>

    public void attachView(View view)
    {
        ButterKnife.inject(this, view);
        exchangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Object item  = exchangeAdapter.getItem(position);
                Class clazz = item.getClass();
            }

            @Override public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        exchangeSpinner.setAdapter(exchangeAdapter);
        setExchangeSpinnerToUserCountry();
        sectorSpinner.setAdapter(sectorAdapter);
        setSectorSpinnerToDefault();
    }

    public void detachView()
    {
        ButterKnife.reset(this);
    }

    public void setUserProfile(@NonNull UserProfileDTO userProfile)
    {
        this.userProfile = userProfile;
        setExchangeSpinnerToUserCountry();
    }

    public void setExchangeSector(@NonNull ExchangeSectorCompactListDTO exchangeSectorCompactListDTO)
    {
        this.exchangeSectorCompacts = exchangeSectorCompactListDTO;
        exchangeAdapter.clear();
        exchangeCompactSpinnerDTOs = new ExchangeCompactSpinnerDTOList(
                context.getResources(),
                exchangeSectorCompactListDTO.exchanges);
        exchangeAdapter.addAll(exchangeCompactSpinnerDTOs);
        exchangeAdapter.notifyDataSetChanged();

        sectorAdapter.clear();
        sectorAdapter.addAll(exchangeSectorCompactListDTO.sectors);
        sectorAdapter.notifyDataSetChanged();

        setExchangeSpinnerToUserCountry();
        setSectorSpinnerToDefault();
    }

    protected void setExchangeSpinnerToUserCountry()
    {
        UserProfileDTO userProfileCopy = userProfile;
        Country userCountry = null;
        if (userProfileCopy != null)
        {
            userCountry = userProfileCopy.getCountry();
        }
        if (userCountry == null)
        {
            userCountry = Country.US;
        }
        ExchangeSpinner exchangeSpinnerCopy = exchangeSpinner;
        ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOsCopy = exchangeCompactSpinnerDTOs;
        if (exchangeSpinnerCopy != null && exchangeCompactSpinnerDTOsCopy != null)
        {
            ExchangeCompactSpinnerDTO found = exchangeCompactSpinnerDTOsCopy.findFirstDefaultFor(userCountry);
            if (found != null)
            {
                exchangeSpinnerCopy.setSelection(found);
            }
        }
    }

    protected void setSectorSpinnerToDefault()
    {
        Spinner sectorSpinnerCopy = sectorSpinner;
        ExchangeSectorCompactListDTO exchangeSectorCompactsCopy = exchangeSectorCompacts;
        if (exchangeSectorCompactsCopy != null && sectorSpinnerCopy != null)
        {
            SectorCompactDTOList sectors = exchangeSectorCompactsCopy.sectors;
            if (sectors != null)
            {
                SectorCompactDTO sectorCompactDTO = sectors.findFirstWhere(new Predicate<SectorCompactDTO>()
                {
                    @Override public boolean apply(SectorCompactDTO sectorCompactDTO1)
                    {
                        return sectorCompactDTO1.id == DEFAULT_SECTOR_ID;
                    }
                });
                if (sectorCompactDTO != null)
                {
                    sectorSpinnerCopy.setSelection(sectors.indexOf(sectorCompactDTO));
                }
            }
        }
    }

    public OnBoardPrefDTO getOnBoardPrefs()
    {
        //save exchange for user selected
        preferredMarketCountry.set(((ExchangeCompactDTO) exchangeSpinner.getSelectedItem())
                .getExchangeIntegerId());
        return new OnBoardPrefDTO(
                (ExchangeCompactDTO) exchangeSpinner.getSelectedItem(),
                (SectorCompactDTO) sectorSpinner.getSelectedItem());
    }
}
