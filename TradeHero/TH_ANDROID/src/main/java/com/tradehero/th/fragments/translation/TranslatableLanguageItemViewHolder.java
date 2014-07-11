package com.tradehero.th.fragments.translation;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.i18n.LanguageDTO;
import org.jetbrains.annotations.NotNull;

public class TranslatableLanguageItemViewHolder implements DTOView<LanguageDTO>
{
    @InjectView(R.id.translatable_text_code) protected TextView languageCode;
    @InjectView(R.id.translatable_text_local) protected  TextView languageName;
    @InjectView(R.id.translatable_text_own_language) protected TextView languageNameOwn;

    private LanguageDTO languageDTO;

    public void initViews(@NotNull View view)
    {
        ButterKnife.inject(this, view);
    }

    public void resetViews()
    {
        ButterKnife.reset(this);
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
    //</editor-fold>
}
