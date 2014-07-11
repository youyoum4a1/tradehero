package com.tradehero.th.fragments.translation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.translation.TranslatableLanguageDTOFactory;
import com.tradehero.th.api.translation.TranslatableLanguageDTOFactoryFactory;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class TranslatableLanguageListFragment extends DashboardFragment
{
    @Inject TranslationTokenCache translationTokenCache;
    @Inject TranslatableLanguageDTOFactoryFactory translatableLanguageDTOFactoryFactory;
    @Inject UserTranslationSettingPreference userTranslationSettingPreference;
    private TranslationToken fetchedTranslationToken;
    private UserTranslationSettingDTO currentSettings;
    @SuppressWarnings("FieldCanBeLocal")
    private TranslatableLanguageDTOFactory translatableLanguageDTOFactory;
    private TranslatableLanguageItemAdapter itemAdapter;
    private DTOCacheNew.Listener<TranslationTokenKey, TranslationToken> tokenFetchListener;
    @InjectView(android.R.id.list) ListView listView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        itemAdapter = createAdapter();
        tokenFetchListener = createTokenFetchListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_translatable_language_list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(itemAdapter);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchToken();
    }

    @Override public void onDestroyView()
    {
        detachTokenCache();
        listView.setAdapter(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        tokenFetchListener = null;
        itemAdapter = null;
        super.onDestroy();
    }

    protected TranslatableLanguageItemAdapter createAdapter()
    {
        return new TranslatableLanguageItemAdapter(getActivity(), R.layout.translatable_language_item);
    }

    protected void fetchToken()
    {
        detachTokenCache();
        TranslationTokenKey key = new TranslationTokenKey();
        translationTokenCache.register(key, tokenFetchListener);
        translationTokenCache.getOrFetchAsync(key);
    }

    protected void detachTokenCache()
    {
        translationTokenCache.unregister(tokenFetchListener);
    }

    protected DTOCacheNew.Listener<TranslationTokenKey, TranslationToken> createTokenFetchListener()
    {
        return new TranslatableLanguageListFragmentTokenFetchListener();
    }

    protected class TranslatableLanguageListFragmentTokenFetchListener implements DTOCacheNew.Listener<TranslationTokenKey, TranslationToken>
    {
        @Override public void onDTOReceived(@NotNull TranslationTokenKey key, @NotNull TranslationToken value)
        {
            handleTranslationTokenReceived(value);
        }

        @Override public void onErrorThrown(@NotNull TranslationTokenKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_incomplete_info_message);
        }
    }

    protected void handleTranslationTokenReceived(@NotNull TranslationToken translationToken)
    {
        this.fetchedTranslationToken = translationToken;
        try
        {
            currentSettings = userTranslationSettingPreference.getOfSameTypeOrDefault(translationToken);
        }
        catch (IOException e)
        {
            currentSettings = null;
            Timber.e(e, "Failed to pull preference %s", translationToken.getClass());
        }
        itemAdapter.setUserTranslationSettingDTO(currentSettings);
        translatableLanguageDTOFactory = translatableLanguageDTOFactoryFactory.create(translationToken);
        if (translatableLanguageDTOFactory == null)
        {
            THToast.show(R.string.translation_error_creating_languages);
        }
        else
        {
            handleLanguages(translatableLanguageDTOFactory.getTargetLanguages());
        }
    }

    protected void handleLanguages(@NotNull List<LanguageDTO> languageDTOs)
    {
        itemAdapter.clear();
        itemAdapter.addAll(languageDTOs);
        itemAdapter.notifyDataSetChanged();
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        @NotNull LanguageDTO languageDTO = (LanguageDTO) adapterView.getItemAtPosition(position);
        Timber.d("%s", languageDTO);
        UserTranslationSettingDTO newSettings = this.currentSettings;
        if (newSettings != null)
        {
            try
            {
                userTranslationSettingPreference.addOrReplaceSettingDTO(newSettings.cloneForLanguage(languageDTO));
            }
            catch (JsonProcessingException e)
            {
                THToast.show(R.string.translation_error_saving_preference);
                Timber.e(e, "Failed saving preference %s", languageDTO);
            }
        }
        getDashboardNavigator().popFragment();
    }
}
