package com.ayondo.academy.fragments.translation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.ayondo.academy.R;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.i18n.LanguageDTO;
import com.ayondo.academy.api.translation.UserTranslationSettingDTO;

public class TranslatableLanguageItemViewHolder implements DTOView<LanguageDTO>
{
    @Bind(R.id.translatable_text_code) protected TextView languageCode;
    @Bind(R.id.translatable_text_local) protected  TextView languageName;
    @Bind(R.id.translatable_text_own_language) protected TextView languageNameOwn;
    @Bind(R.id.translatable_tick_is_current) protected View isCurrentView;

    @Nullable private UserTranslationSettingDTO currentTranslationSetting;
    private LanguageDTO languageDTO;

    public void initViews(@NonNull View view)
    {
        ButterKnife.bind(this, view);
    }

    public void resetViews()
    {
        ButterKnife.unbind(this);
    }

    public void setCurrentTranslationSetting(@Nullable UserTranslationSettingDTO currentTranslationSetting)
    {
        this.currentTranslationSetting = currentTranslationSetting;
        displayIsCurrent();
    }

    public void display(LanguageDTO languageDTO)
    {
        linkWith(languageDTO, true);
    }

    public void linkWith(LanguageDTO languageDTO, boolean andDisplay)
    {
        this.languageDTO = languageDTO;
        if (andDisplay)
        {
            display();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayLanguageCode();
        displayLanguageName();
        displayLanguageNameOwn();
        displayIsCurrent();
    }

    public void displayLanguageCode()
    {
        if (languageCode != null)
        {
            if (languageDTO != null)
            {
                languageCode.setText(languageDTO.code);
            }
            else
            {
                languageCode.setText(R.string.na);
            }
        }
    }

    public void displayLanguageName()
    {
        if (languageName != null)
        {
            if (languageDTO != null)
            {
                languageName.setText(languageDTO.name);
            }
            else
            {
                languageName.setText(R.string.na);
            }
        }
    }

    public void displayLanguageNameOwn()
    {
        if (languageNameOwn != null)
        {
            if (languageDTO != null)
            {
                languageNameOwn.setText(languageDTO.nameInOwnLang);
            }
            else
            {
                languageNameOwn.setText(R.string.na);
            }
        }
    }

    public void displayIsCurrent()
    {
        if (isCurrentView != null)
        {
            boolean isCurrent = currentTranslationSetting != null
                    && languageDTO != null
                    && languageDTO.code.equals(currentTranslationSetting.languageCode);
            isCurrentView.setVisibility(isCurrent ? View.VISIBLE : View.GONE);
        }
    }
    //</editor-fold>
}
