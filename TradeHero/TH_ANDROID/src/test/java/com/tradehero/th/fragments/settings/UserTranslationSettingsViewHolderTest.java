package com.tradehero.th.fragments.settings;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.api.translation.bing.BingUserTranslationSettingDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.translation.TranslatableLanguageListFragment;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowPreference;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class UserTranslationSettingsViewHolderTest
{
    @Inject TranslationTokenCache translationTokenCache;
    @Inject UserTranslationSettingPreference userTranslationSettingPreference;
    private DashboardNavigator dashboardNavigator;
    private SettingsFragment settingsFragment;

    @Before public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        dashboardNavigator = activity.getDashboardNavigator();
    }

    @After public void tearDown()
    {
        dashboardNavigator.popFragment();
        settingsFragment = null;
        translationTokenCache.invalidateAll();
        userTranslationSettingPreference.delete();
    }

    //<editor-fold desc="Lifecycle">
    @Test public void viewInitAndDestroyProper()
    {
        settingsFragment = dashboardNavigator.pushFragment(SettingsFragment.class);

        UserTranslationSettingsViewHolder holder = settingsFragment.userTranslationSettingsViewHolder;
        assertThat(holder).isNotNull();
        assertThat(holder.preferenceFragment).isNotNull();
        assertThat(holder.preferenceFragment).isSameAs(settingsFragment);
        assertThat(holder.translationContainer).isNotNull();
        assertThat(holder.translationContainer.isEnabled()).isFalse();
        assertThat(holder.translationPreferredLang).isNotNull();
        assertThat(holder.translationAuto).isNotNull();

        Preference translationPreferredLang = holder.translationPreferredLang;
        CheckBoxPreference translationAuto = holder.translationAuto;

        dashboardNavigator.popFragment();
        assertThat(holder.translationAuto).isNull();
        assertThat(translationAuto.getOnPreferenceChangeListener()).isNull();
        assertThat(translationAuto.getOnPreferenceClickListener()).isNull();
        assertThat(holder.translationPreferredLang).isNull();
        assertThat(translationPreferredLang.getOnPreferenceClickListener()).isNull();
        assertThat(translationPreferredLang.getOnPreferenceChangeListener()).isNull();
        assertThat(holder.translationContainer).isNull();
        assertThat(holder.preferenceFragment).isNull();
        assertThat(settingsFragment.userTranslationSettingsViewHolder).isNotNull();

        settingsFragment.onDestroy();
        assertThat(settingsFragment.userTranslationSettingsViewHolder).isNull();
    }
    //</editor-fold>

    //<editor-fold desc="Group enabled or not">
    @Test public void disabledIfNothingInCache()
    {
        settingsFragment = dashboardNavigator.pushFragment(SettingsFragment.class);

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        ShadowPreference shadowPreferenceContainer = Robolectric.shadowOf(settingsFragment.userTranslationSettingsViewHolder.translationContainer);
        assertThat(shadowPreferenceContainer.isEnabled()).isFalse();
    }

    @Test public void enabledIfHasCache() throws InterruptedException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        settingsFragment = dashboardNavigator.pushFragment(SettingsFragment.class);

        Thread.sleep(200); // TODO remove this HACK
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        ShadowPreference shadowPreferenceContainer = Robolectric.shadowOf(settingsFragment.userTranslationSettingsViewHolder.translationContainer);
        assertThat(shadowPreferenceContainer.isEnabled()).isTrue();
    }
    //</editor-fold>

    //<editor-fold desc="Auto is checked or not">
    @Test public void autoIsCheckedIfPrefTrue() throws JsonProcessingException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("en", true));

        settingsFragment = dashboardNavigator.pushFragment(SettingsFragment.class);

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(settingsFragment.userTranslationSettingsViewHolder.translationAuto.isChecked()).isTrue();
    }

    @Test public void autoIsNotCheckedIfPrefFalse() throws JsonProcessingException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("en", false));

        settingsFragment = dashboardNavigator.pushFragment(SettingsFragment.class);

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(settingsFragment.userTranslationSettingsViewHolder.translationAuto.isChecked()).isFalse();
    }
    //</editor-fold>

    //<editor-fold desc="Preferred language label">
    @Test public void preferredPicksLanguage() throws JsonProcessingException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", false));

        settingsFragment = dashboardNavigator.pushFragment(SettingsFragment.class);

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(settingsFragment.userTranslationSettingsViewHolder
                .translationPreferredLang.getSummary().toString())
                .contains("French");
    }
    //</editor-fold>

    //<editor-fold desc="Preferred language click">
    @Test public void clickPreferredPushesListFragment() throws JsonProcessingException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", false));

        settingsFragment = dashboardNavigator.pushFragment(SettingsFragment.class);

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertThat(Robolectric.shadowOf(settingsFragment.userTranslationSettingsViewHolder
                .translationPreferredLang).click())
            .isTrue();

        assertThat(dashboardNavigator.getCurrentFragment())
                .isExactlyInstanceOf(TranslatableLanguageListFragment.class);
    }
    //</editor-fold>
}
