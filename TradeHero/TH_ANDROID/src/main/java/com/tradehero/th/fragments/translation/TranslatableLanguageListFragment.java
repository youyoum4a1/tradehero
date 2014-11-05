package com.tradehero.th.fragments.translation;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.translation.TranslatableLanguageDTOFactory;
import com.tradehero.th.api.translation.TranslatableLanguageDTOFactoryFactory;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.translation.TranslationTokenCacheRx;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class TranslatableLanguageListFragment extends DashboardFragment
{
    @Inject TranslationTokenCacheRx translationTokenCache;
    @Inject TranslatableLanguageDTOFactoryFactory translatableLanguageDTOFactoryFactory;
    @Inject UserTranslationSettingPreference userTranslationSettingPreference;
    private UserTranslationSettingDTO currentSettings;
    private TranslatableLanguageItemAdapter itemAdapter;
    private Subscription tokenFetchSubscription;
    @InjectView(android.R.id.list) ListView listView;
    @InjectView(android.R.id.empty) View emptyView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        itemAdapter = createAdapter();
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
        listView.setEmptyView(emptyView);
        listView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchToken();
    }

    @Override public void onDestroyView()
    {
        unsubscribe(tokenFetchSubscription);
        listView.setEmptyView(null);
        listView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        tokenFetchSubscription = null;
        itemAdapter = null;
        super.onDestroy();
    }

    protected TranslatableLanguageItemAdapter createAdapter()
    {
        return new TranslatableLanguageItemAdapter(getActivity(), R.layout.translatable_language_item);
    }

    protected void fetchToken()
    {
        unsubscribe(tokenFetchSubscription);
        tokenFetchSubscription = AndroidObservable.bindFragment(
                this,
                translationTokenCache.get(new TranslationTokenKey()))
                .subscribe(createTokenFetchObserver());
    }

    protected Observer<Pair<TranslationTokenKey, TranslationToken>> createTokenFetchObserver()
    {
        return new TranslatableLanguageListFragmentTokenFetchObserver();
    }

    protected class TranslatableLanguageListFragmentTokenFetchObserver implements Observer<Pair<TranslationTokenKey, TranslationToken>>
    {
        @Override public void onNext(Pair<TranslationTokenKey, TranslationToken> pair)
        {
            handleTranslationTokenReceived(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_incomplete_info_message);
        }
    }

    protected void handleTranslationTokenReceived(@NotNull TranslationToken translationToken)
    {
        try
        {
            currentSettings = userTranslationSettingPreference.getOfSameTypeOrDefault(translationToken);
        } catch (IOException e)
        {
            currentSettings = null;
            Timber.e(e, "Failed to pull preference %s", translationToken.getClass());
        }
        itemAdapter.setUserTranslationSettingDTO(currentSettings);
        TranslatableLanguageDTOFactory translatableLanguageDTOFactory = translatableLanguageDTOFactoryFactory.create(translationToken);
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
            } catch (JsonProcessingException e)
            {
                THToast.show(R.string.translation_error_saving_preference);
                Timber.e(e, "Failed saving preference %s", languageDTO);
            }
        }
        navigator.get().popFragment();
    }
}
