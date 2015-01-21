package com.tradehero.th.fragments.translation;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;

public class TranslatableLanguageItemAdapter extends ArrayDTOAdapterNew<LanguageDTO, TranslatableLanguageItemLinearView>
{
    @Nullable private UserTranslationSettingDTO userTranslationSettingDTO;

    //<editor-fold desc="Constructors">
    public TranslatableLanguageItemAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

    public void setUserTranslationSettingDTO(@Nullable UserTranslationSettingDTO userTranslationSettingDTO)
    {
        this.userTranslationSettingDTO = userTranslationSettingDTO;
    }

    @Override public TranslatableLanguageItemLinearView getView(int position, View convertView, ViewGroup viewGroup)
    {
        TranslatableLanguageItemLinearView view = super.getView(position, convertView, viewGroup);
        view.setCurrentTranslationSetting(userTranslationSettingDTO);
        return view;
    }
}
