package com.tradehero.th.fragments.live;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.tradehero.common.utils.SDKUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.live.LiveCountryDTO;
import com.tradehero.th.api.live.LiveCountryDTOUtil;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.utils.GraphicUtil;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class CountrySpinnerAdapter extends ArrayAdapter<CountrySpinnerAdapter.CountryViewHolder.DTO>
{
    public static final int VIEW_HOLDER_TAG_ID = R.id.live_country_icon;
    @LayoutRes private final int viewRes;
    @LayoutRes private final int dropViewRes;

    public CountrySpinnerAdapter(
            @NonNull Context context,
            @LayoutRes int viewRes,
            @LayoutRes int dropViewRes)
    {
        super(context, 0);
        this.viewRes = viewRes;
        this.dropViewRes = dropViewRes;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = getViewWithLayout(this.viewRes, position, convertView, parent);
        if (!SDKUtils.isLollipopOrHigher())
        {
            if (v.getTag(VIEW_HOLDER_TAG_ID) != null)
            {
                CountryViewHolder viewHolder = (CountryViewHolder) v.getTag(VIEW_HOLDER_TAG_ID);
                TextView tv = viewHolder.txtCountry == null ? (viewHolder.txtCountryCode != null ? viewHolder.txtCountryCode : null)
                        : viewHolder.txtCountry;
                if (tv != null)
                {
                    tv.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            GraphicUtil.createStateListDrawableRes(getContext(), R.drawable.abc_spinner_mtrl_am_alpha), null);
                }
            }
        }
        return v;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getViewWithLayout(dropViewRes, position, convertView, parent);
    }

    @NonNull protected View getViewWithLayout(@LayoutRes int layoutResId, int position, View convertView, ViewGroup parent)
    {
        CountryViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
            viewHolder = new CountryViewHolder(convertView);
            convertView.setTag(VIEW_HOLDER_TAG_ID, viewHolder);
        }
        else
        {
            viewHolder = (CountryViewHolder) convertView.getTag(VIEW_HOLDER_TAG_ID);
        }

        viewHolder.display(getItem(position));
        return convertView;
    }

    @NonNull public static List<CountryViewHolder.DTO> createDTOs(@NonNull List<? extends LiveCountryDTO> liveCountryDTOs)
    {
        List<CountryViewHolder.DTO> created = new ArrayList<>();
        for (LiveCountryDTO liveCountryDTO : liveCountryDTOs)
        {
            created.add(new CountryViewHolder.DTO(liveCountryDTO));
        }
        return created;
    }

    @NonNull public static List<CountryViewHolder.DTO> getFilterByPhoneCountryCode(
            @NonNull List<? extends CountryViewHolder.DTO> liveCountryDTOs,
            int phoneCountryCode)
    {
        List<String> regions = PhoneNumberUtil.getInstance().getRegionCodesForCountryCode(phoneCountryCode);
        List<Country> countries = new ArrayList<>();
        for (String region : regions)
        {
            try
            {
                countries.add(Enum.valueOf(Country.class, region));
            } catch (Exception e)
            {
                Timber.e(e, "Failed to find country for region %s", region);
            }
        }
        return getFilterByCountry(liveCountryDTOs, countries);
    }

    @NonNull public static List<CountryViewHolder.DTO> getFilterByCountry(
            @NonNull List<? extends CountryViewHolder.DTO> liveCountryDTOs,
            @NonNull List<Country> countries)
    {
        List<CountryViewHolder.DTO> filtered = new ArrayList<>();

        for (CountryViewHolder.DTO candidate : liveCountryDTOs)
        {
            if (countries.contains(candidate.liveCountryDTO.country))
            {
                filtered.add(candidate);
            }
        }

        return filtered;
    }

    public static class CountryViewHolder implements DTOView<CountryViewHolder.DTO>
    {
        @Bind(R.id.live_country_icon) @Nullable ImageView imgCountry;
        @Bind(R.id.live_country_label) @Nullable TextView txtCountry;
        @Bind(R.id.live_country_phone_code) @Nullable TextView txtCountryCode;

        public CountryViewHolder(@NonNull View itemView)
        {
            ButterKnife.bind(this, itemView);
        }

        @Override public void display(DTO dto)
        {
            if (imgCountry != null)
            {
                imgCountry.setImageResource(dto.logoId);
            }
            if (txtCountry != null)
            {
                txtCountry.setText(dto.locationName);
            }
            if (txtCountryCode != null)
            {
                txtCountryCode.setText(dto.phoneCountryCodeText);
            }
        }

        public static class DTO
        {
            @NonNull public final LiveCountryDTO liveCountryDTO;
            @DrawableRes public final int logoId;
            @StringRes public final int locationName;
            public final int phoneCountryCode;
            @NonNull public final String phoneCountryCodeText;

            public DTO(@NonNull LiveCountryDTO liveCountryDTO)
            {
                this.liveCountryDTO = liveCountryDTO;
                this.logoId = liveCountryDTO.country.logoId;
                this.locationName = liveCountryDTO.country.locationName;
                this.phoneCountryCode = LiveCountryDTOUtil.getPhoneCodePlusLeadingDigits(liveCountryDTO);
                this.phoneCountryCodeText = "+" + phoneCountryCode;
            }
        }
    }
}
