package com.tradehero.th.models.share;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTOFactory;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.news.NewsDialogFactory;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.translation.TranslationCacheRx;
import com.tradehero.th.persistence.translation.TranslationKey;
import com.tradehero.th.persistence.translation.TranslationKeyFactory;
import com.tradehero.th.persistence.translation.TranslationKeyList;
import com.tradehero.th.persistence.translation.TranslationTokenCacheRx;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;
import com.tradehero.th.utils.AlertDialogUtil;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class SocialShareTranslationHelper extends SocialShareHelper
{
    @NotNull protected final TranslationKeyFactory translationKeyFactory;
    @NotNull protected final AbstractDiscussionCompactDTOFactory abstractDiscussionCompactDTOFactory;
    @NotNull protected final TranslationTokenCacheRx translationTokenCache;
    @NotNull protected final TranslationCacheRx translationCache;
    @NotNull protected final UserTranslationSettingPreference userTranslationSettingPreference;

    protected Subscription translationTokenSubscription;
    protected TranslationToken translationToken;
    protected UserTranslationSettingDTO userTranslationSettingDTO;
    @Nullable private AbstractDiscussionCompactDTO toTranslate;
    private TranslationKeyList remainingKeys;
    private AbstractDiscussionCompactDTO translated;

    //<editor-fold desc="Constructors">
    @Inject public SocialShareTranslationHelper(
            @NotNull Context applicationContext,
            @NotNull Provider<Activity> activityProvider,
            @NotNull Provider<DashboardNavigator> navigatorProvider,
            @NotNull NewsDialogFactory newsDialogFactory,
            @NotNull AlertDialogUtil alertDialogUtil,
            @NotNull Provider<SocialSharer> socialSharerProvider,
            @NotNull TranslationKeyFactory translationKeyFactory,
            @NotNull AbstractDiscussionCompactDTOFactory abstractDiscussionCompactDTOFactory,
            @NotNull TranslationTokenCacheRx translationTokenCache,
            @NotNull TranslationCacheRx translationCache,
            @NotNull UserTranslationSettingPreference userTranslationSettingPreference)
    {
        super(applicationContext, activityProvider, navigatorProvider, newsDialogFactory, alertDialogUtil, socialSharerProvider);
        this.translationKeyFactory = translationKeyFactory;
        this.abstractDiscussionCompactDTOFactory = abstractDiscussionCompactDTOFactory;
        this.translationTokenCache = translationTokenCache;
        this.translationCache = translationCache;
        this.userTranslationSettingPreference = userTranslationSettingPreference;
        fetchTranslationToken();
    }
    //</editor-fold>

    @Override public void onDetach()
    {
        detachTranslationTokenCache();
        setMenuClickedListener(null);
        translationTokenSubscription = null;
        super.onDetach();
    }

    //<editor-fold desc="Listener Handling">
    @Override public void setMenuClickedListener(@Nullable SocialShareHelper.OnMenuClickedListener menuClickedListener)
    {
        if (menuClickedListener != null && !(menuClickedListener instanceof OnMenuClickedListener))
        {
            throw new IllegalArgumentException("Only accepts OnMenuClickedListener");
        }
        super.setMenuClickedListener(menuClickedListener);
    }

    protected void notifyTranslationClicked(AbstractDiscussionCompactDTO toTranslate)
    {
        OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslationClicked(toTranslate);
        }
    }

    protected void notifyTranslatedOneAttribute(AbstractDiscussionCompactDTO toTranslate,
            TranslationResult translationResult)
    {
        OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslatedOneAttribute(toTranslate, translationResult);
        }
    }

    protected void notifyTranslatedAllAtributes(AbstractDiscussionCompactDTO toTranslate, AbstractDiscussionCompactDTO translated)
    {
        OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslatedAllAtributes(toTranslate, translated);
        }
    }

    protected void notifyTranslateFailed(AbstractDiscussionCompactDTO toTranslate, Throwable error)
    {
        OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslateFailed(toTranslate, error);
        }
    }
    //</editor-fold>

    @NotNull public String getTargetLanguage()
    {
        if (userTranslationSettingDTO != null)
        {
            return userTranslationSettingDTO.languageCode;
        }
        return applicationContext.getResources().getConfiguration().locale.getLanguage();
    }

    public boolean isAutoTranslate()
    {
        return userTranslationSettingDTO != null
                && userTranslationSettingDTO.autoTranslate;
    }

    public boolean canTranslate(@Nullable AbstractDiscussionCompactDTO discussionToTranslate)
    {
        return translationToken != null &&
                discussionToTranslate != null &&
                translationKeyFactory.isValidLangCode(discussionToTranslate.langCode) &&
                !discussionToTranslate.langCode.equals(getTargetLanguage());
    }

    public void shareOrTranslate(AbstractDiscussionCompactDTO discussionToShare)
    {
        if (canTranslate(discussionToShare))
        {
            cancelFormWaiting();
            dismissShareDialog();
            Context currentActivityContext = activityHolder.get();
            if (currentActivityContext != null)
            {
                shareDialog = ((NewsDialogFactory) shareDialogFactory).createNewsDialog(
                        currentActivityContext, discussionToShare,
                        createShareMenuClickedListener());
            }
        }
        else
        {
            share(discussionToShare);
        }
    }

    @NotNull @Override protected NewsDialogLayout.OnMenuClickedListener createShareMenuClickedListener()
    {
        return new SocialShareTranslationHelperShareMenuClickedListener();
    }

    protected class SocialShareTranslationHelperShareMenuClickedListener
            extends SocialShareHelperShareMenuClickedListener
            implements NewsDialogLayout.OnMenuClickedListener
    {
        @Override public void onTranslationRequestedClicked(AbstractDiscussionCompactDTO toTranslate)
        {
            dismissShareDialog();
            notifyTranslationClicked(toTranslate);
            translate(toTranslate);
        }
    }

    public void translate(@Nullable AbstractDiscussionCompactDTO toTranslate)
    {
        if (toTranslate != null && toTranslate.langCode != null)
        {
            this.toTranslate = toTranslate;
            this.translated = abstractDiscussionCompactDTOFactory.clone(toTranslate);

            remainingKeys = translationKeyFactory.createFrom(toTranslate, getTargetLanguage());
            if (remainingKeys.size() == 0)
            {
                notifyAllDoneIfPossible();
            }
            else
            {
                for (TranslationKey key : new TranslationKeyList(remainingKeys))
                {
                    translationCache.get(key).
                            observeOn(AndroidSchedulers.mainThread())
                            .subscribe(createTranslationCacheObserver(key));
                }
            }
        }
    }

    public void notifyAllDoneIfPossible()
    {
        if (remainingKeys == null || remainingKeys.size() == 0)
        {
            notifyTranslatedAllAtributes(toTranslate, translated);
        }
    }

    @NotNull protected Observer<Pair<TranslationKey, TranslationResult>> createTranslationCacheObserver(@NotNull TranslationKey key)
    {
        return new SocialShareTranslationHelperTranslationCacheObserver(key);
    }

    protected class SocialShareTranslationHelperTranslationCacheObserver implements Observer<Pair<TranslationKey, TranslationResult>>
    {
        @NotNull private TranslationKey key;

        public SocialShareTranslationHelperTranslationCacheObserver(@NotNull TranslationKey key)
        {
            this.key = key;
        }

        @Override public void onNext(Pair<TranslationKey, TranslationResult> pair)
        {
            notifyTranslatedOneAttribute(toTranslate, pair.second);
            abstractDiscussionCompactDTOFactory.populateTranslation(translated, key, pair.second);
            remainingKeys.remove(key);
            notifyAllDoneIfPossible();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            notifyTranslateFailed(toTranslate, e);
            remainingKeys.remove(key);
            notifyAllDoneIfPossible();
        }
    }

    public interface OnMenuClickedListener extends SocialShareHelper.OnMenuClickedListener
    {
        void onTranslationClicked(AbstractDiscussionCompactDTO toTranslate);

        void onTranslatedOneAttribute(AbstractDiscussionCompactDTO toTranslate, TranslationResult translationResult);

        void onTranslatedAllAtributes(AbstractDiscussionCompactDTO toTranslate, AbstractDiscussionCompactDTO translated);

        void onTranslateFailed(AbstractDiscussionCompactDTO toTranslate, Throwable error);
    }

    protected void fetchTranslationToken()
    {
        detachTranslationTokenCache();
        TranslationTokenKey key = new TranslationTokenKey();
        translationTokenSubscription = translationTokenCache.get(key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createTranslationTokenObserver());
    }

    protected void detachTranslationTokenCache()
    {
        Subscription copy = translationTokenSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        translationTokenSubscription = null;
    }

    protected Observer<Pair<TranslationTokenKey, TranslationToken>> createTranslationTokenObserver()
    {
        return new SettingsTranslationTokenObserver();
    }

    protected class SettingsTranslationTokenObserver implements Observer<Pair<TranslationTokenKey, TranslationToken>>
    {
        @Override public void onNext(Pair<TranslationTokenKey, TranslationToken> pair)
        {
            linkWith(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e("Failed", e);
        }
    }

    protected void linkWith(@NotNull TranslationToken token)
    {
        this.translationToken = token;
        try
        {
            linkWith(userTranslationSettingPreference.getOfSameTypeOrDefault(token));
        } catch (IOException e)
        {
            Timber.e(e, "Failed to get translation preferences");
        }
    }

    protected void linkWith(@Nullable UserTranslationSettingDTO userTranslationSettingDTO)
    {
        this.userTranslationSettingDTO = userTranslationSettingDTO;
    }
}