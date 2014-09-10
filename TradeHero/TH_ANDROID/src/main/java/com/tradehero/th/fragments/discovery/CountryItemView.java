package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.news.CountryLanguagePairDTO;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CountryItemView extends LinearLayout
        implements DTOView<CountryLanguagePairDTO>
{
    @InjectView(R.id.country_name) TextView countryName;
    @InjectView(R.id.language_code) TextView languageCode;

    public CountryItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(CountryLanguagePairDTO dto)
    {
        countryName.setText(dto.name);
        languageCode.setText(String.format("%s-%s", dto.languageCode, dto.countryCode));
    }
}
