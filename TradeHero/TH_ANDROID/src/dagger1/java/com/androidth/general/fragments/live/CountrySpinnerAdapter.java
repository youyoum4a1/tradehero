package com.androidth.general.fragments.live;

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

import com.androidth.general.api.DTOView;
import com.androidth.general.api.live.CountryUtil;
import com.androidth.general.api.market.Country;
import com.androidth.general.common.utils.SDKUtils;
import com.androidth.general.utils.GraphicUtil;
import com.tradehero.th.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CountrySpinnerAdapter extends ArrayAdapter<CountrySpinnerAdapter.DTO>
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
                            GraphicUtil.createStateListDrawableRes(tv.getContext(), R.drawable.abc_spinner_mtrl_am_alpha), null);
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

    @NonNull public static List<DTO> createDTOs(@NonNull List<? extends Country> countries, @Nullable Country typeQualifier)
    {
        List<DTO> created = new ArrayList<>();
        for (Country countryDTO : countries)
        {
            created.add(new DTO(countryDTO));
        }
        return created;
    }

    @NonNull public static List<DTO> getFilterByPhoneCountryCode(
            @NonNull List<? extends DTO> liveCountryDTOs,
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

    @NonNull public static List<DTO> getFilterByCountry(
            @NonNull List<? extends DTO> liveCountryDTOs,
            @NonNull List<Country> countries)
    {
        List<DTO> filtered = new ArrayList<>();

        for (DTO candidate : liveCountryDTOs)
        {
            if (countries.contains(candidate.country))
            {
                filtered.add(candidate);
            }
        }

        return filtered;
    }

    public static class CountryViewHolder implements DTOView<DTO>
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
    }

    public static class DTO
    {
        @NonNull public final Country country;
        @DrawableRes public final int logoId;
        @StringRes public final int locationName;
        public final int phoneCountryCode;
        @NonNull public final String phoneCountryCodeText;

        public DTO(@NonNull Country country)
        {
            this.country = country;
            this.logoId = country.logoId;
            this.locationName = country.locationName;
            this.phoneCountryCode = CountryUtil.getPhoneCodePlusLeadingDigits(country);
            this.phoneCountryCodeText = "+" + phoneCountryCode;
        }

        @Override public int hashCode()
        {
            return country.hashCode();
        }

        @Override public boolean equals(Object o)
        {
            if (o == this) return true;
            if (o == null) return false;
            return o instanceof DTO && ((DTO) o).country.equals(country);
        }
    }

    public static class DTOCountryNameComparator implements Comparator<DTO>
    {
        @NonNull private final Context context;

        public DTOCountryNameComparator(@NonNull Context context)
        {
            this.context = context;
        }

        @Override public int compare(@NonNull DTO lhs, @NonNull DTO rhs)
        {
            return context.getString(lhs.country.locationName).compareTo(context.getString(rhs.country.locationName));
        }
    }
}
