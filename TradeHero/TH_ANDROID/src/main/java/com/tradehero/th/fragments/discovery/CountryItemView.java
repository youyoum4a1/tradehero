package com.ayondo.academy.fragments.discovery;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.ayondo.academy.R;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.news.CountryLanguagePairDTO;

public class CountryItemView extends LinearLayout
        implements DTOView<CountryLanguagePairDTO>
{
    @Bind(R.id.country_name) TextView countryName;
    @Bind(R.id.language_code) TextView languageCode;

    public CountryItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override public void display(CountryLanguagePairDTO dto)
    {
        countryName.setText(dto.name);
        languageCode.setText(String.format("%s-%s", dto.languageCode, dto.countryCode));
    }
}
