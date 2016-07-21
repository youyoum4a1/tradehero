package com.androidth.general.fragments.translation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnItemClick;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.i18n.LanguageDTO;
import com.androidth.general.api.translation.TranslatableLanguageDTOFactory;
import com.androidth.general.api.translation.TranslatableLanguageDTOFactoryFactory;
import com.androidth.general.api.translation.TranslationToken;
import com.androidth.general.api.translation.UserTranslationSettingDTO;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.persistence.translation.TranslationTokenCacheRx;
import com.androidth.general.persistence.translation.TranslationTokenKey;
import com.androidth.general.persistence.translation.UserTranslationSettingPreference;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

import butterknife.Unbinder;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class TranslatableLanguageListFragment extends BaseFragment
{
    @Inject TranslationTokenCacheRx translationTokenCache;
    @Inject TranslatableLanguageDTOFactoryFactory translatableLanguageDTOFactoryFactory;
    @Inject UserTranslationSettingPreference userTranslationSettingPreference;
    private UserTranslationSettingDTO currentSettings;
    private TranslatableLanguageItemAdapter itemAdapter;
    private Subscription tokenFetchSubscription;
    @BindView(android.R.id.list) ListView listView;
    @BindView(android.R.id.empty) View emptyView;

    private Unbinder unbinder;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        itemAdapter = new TranslatableLanguageItemAdapter(activity, R.layout.translatable_language_item);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_translatable_language_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(itemAdapter);
        listView.setEmptyView(emptyView);
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
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        tokenFetchSubscription = null;
        itemAdapter = null;
        super.onDestroy();
    }

    @Override public void onDetach()
    {
        itemAdapter = null;
        super.onDetach();
    }

    protected void fetchToken()
    {
        unsubscribe(tokenFetchSubscription);
        tokenFetchSubscription = AppObservable.bindSupportFragment(
                this,
                translationTokenCache.get(new TranslationTokenKey()))
                .observeOn(AndroidSchedulers.mainThread())
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

    protected void handleTranslationTokenReceived(@NonNull TranslationToken translationToken)
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
            handleLanguages(translatableLanguageDTOFactory.getTargetLanguages(getResources()));
        }
    }

    protected void handleLanguages(@NonNull List<LanguageDTO> languageDTOs)
    {
        itemAdapter.clear();
        itemAdapter.addAll(languageDTOs);
        itemAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    @OnItemClick(android.R.id.list)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        LanguageDTO languageDTO = (LanguageDTO) adapterView.getItemAtPosition(position);
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
