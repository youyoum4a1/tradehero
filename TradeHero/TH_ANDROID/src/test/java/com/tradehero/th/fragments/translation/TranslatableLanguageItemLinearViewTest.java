package com.tradehero.th.fragments.translation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class TranslatableLanguageItemLinearViewTest
{
    @Inject Context context;
    private TranslatableLanguageItemLinearView linearView;

    @Before public void setUp()
    {
        linearView = (TranslatableLanguageItemLinearView)
                LayoutInflater.from(context).inflate(R.layout.translatable_language_item, null);
    }

    @After public void tearDown()
    {
        linearView = null;
    }

    @Test public void viewHolderIsCreatedWithViews()
    {
        assertThat(linearView.viewHolder).isNotNull();
        TranslatableLanguageItemViewHolder viewHolder = linearView.viewHolder;
        assertThat(viewHolder.languageCode).isNotNull();
        assertThat(viewHolder.languageName).isNotNull();
        assertThat(viewHolder.languageNameOwn).isNotNull();
        assertThat(viewHolder.isCurrentView).isNotNull();
    }

    @Test public void onDetachClearsViewHolder()
    {
        linearView.onDetachedFromWindow();
        TranslatableLanguageItemViewHolder viewHolder = linearView.viewHolder;
        assertThat(viewHolder.languageCode).isNull();
        assertThat(viewHolder.languageName).isNull();
        assertThat(viewHolder.languageNameOwn).isNull();
        assertThat(viewHolder.isCurrentView).isNull();
    }

    @Test public void viewHolderShouldPopulateLanguageFields()
    {
        TranslatableLanguageItemViewHolder viewHolder = linearView.viewHolder;
        LanguageDTO languageDTO = new LanguageDTO("ab", "cd", "ef");
        linearView.display(languageDTO);
        assertThat(viewHolder.languageCode.getText()).isEqualTo("ab");
        assertThat(viewHolder.languageName.getText()).isEqualTo("cd");
        assertThat(viewHolder.languageNameOwn.getText()).isEqualTo("ef");
        assertThat(viewHolder.isCurrentView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test public void viewHolderShouldHideIsCurrentIfNotNullButDifferent()
    {
        TranslatableLanguageItemViewHolder viewHolder = linearView.viewHolder;
        UserTranslationSettingDTO settingDTO = new UserTranslationSettingDTO("was", true);
        linearView.setCurrentTranslationSetting(settingDTO);
        LanguageDTO languageDTO = new LanguageDTO("ab", "cd", "ef");
        linearView.display(languageDTO);

        assertThat(viewHolder.isCurrentView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test public void viewHolderShouldShowIsCurrentIfNotNullAndSame()
    {
        TranslatableLanguageItemViewHolder viewHolder = linearView.viewHolder;
        UserTranslationSettingDTO settingDTO = new UserTranslationSettingDTO("ab", true);
        linearView.setCurrentTranslationSetting(settingDTO);
        LanguageDTO languageDTO = new LanguageDTO("ab", "cd", "ef");
        linearView.display(languageDTO);

        assertThat(viewHolder.isCurrentView.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test public void viewHolderShouldHideIsCurrentWhenNoLanguageDTO()
    {
        TranslatableLanguageItemViewHolder viewHolder = linearView.viewHolder;
        UserTranslationSettingDTO settingDTO = new UserTranslationSettingDTO("ab", true);
        linearView.setCurrentTranslationSetting(settingDTO);
        linearView.display(null);

        assertThat(viewHolder.isCurrentView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test public void callingDisplayAfterDetachedDoesNotCrash()
    {
        linearView.onDetachedFromWindow();
        TranslatableLanguageItemViewHolder viewHolder = linearView.viewHolder;
        viewHolder.display(new LanguageDTO("ab", "cd", "ef"));
    }
}
