package com.tradehero.th.fragments.translation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import org.jetbrains.annotations.Nullable;

public class TranslatableLanguageItemAdapter extends ArrayDTOAdapterNew<LanguageDTO, TranslatableLanguageItemLinearView>
{
    @Nullable private UserTranslationSettingDTO userTranslationSettingDTO;

    //<editor-fold desc="Constructors">
    public TranslatableLanguageItemAdapter(Context context, int layoutResourceId)
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
