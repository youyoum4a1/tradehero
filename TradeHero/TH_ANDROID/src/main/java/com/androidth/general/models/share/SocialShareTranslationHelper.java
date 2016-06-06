package com.androidth.general.models.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTOFactory;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.translation.TranslationResult;
import com.androidth.general.api.translation.TranslationToken;
import com.androidth.general.api.translation.UserTranslationSettingDTO;
import com.androidth.general.auth.AuthenticationProvider;
import com.androidth.general.auth.SocialAuth;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.news.NewsDialogFactory;
import com.androidth.general.fragments.news.NewsDialogLayout;
import com.androidth.general.fragments.news.ShareDialogLayout;
import com.androidth.general.network.share.SocialSharer;
import com.androidth.general.network.share.dto.SocialDialogResult;
import com.androidth.general.network.share.dto.TranslateResult;
import com.androidth.general.persistence.translation.TranslationCacheRx;
import com.androidth.general.persistence.translation.TranslationKey;
import com.androidth.general.persistence.translation.TranslationKeyFactory;
import com.androidth.general.persistence.translation.TranslationTokenCacheRx;
import com.androidth.general.persistence.translation.TranslationTokenKey;
import com.androidth.general.persistence.translation.UserTranslationSettingPreference;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class SocialShareTranslationHelper extends SocialShareHelper
{
    @NonNull protected final TranslationTokenCacheRx translationTokenCache;
    @NonNull protected final TranslationCacheRx translationCache;
    @NonNull protected final UserTranslationSettingPreference userTranslationSettingPreference;

    private boolean shareOnly = false; // TODO Ugly, find a better way

    //<editor-fold desc="Constructors">
    @Inject public SocialShareTranslationHelper(
            @NonNull Context applicationContext,
            @NonNull Provider<Activity> activityProvider,
            @NonNull Provider<DashboardNavigator> navigatorProvider,
            @NonNull Provider<SocialSharer> socialSharerProvider,
            @NonNull @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviders,
            @NonNull TranslationTokenCacheRx translationTokenCache,
            @NonNull TranslationCacheRx translationCache,
            @NonNull UserTranslationSettingPreference userTranslationSettingPreference)
    {
        super(applicationContext,
                activityProvider,
                navigatorProvider,
                socialSharerProvider,
                authenticationProviders);
        this.translationTokenCache = translationTokenCache;
        this.translationCache = translationCache;
        this.userTranslationSettingPreference = userTranslationSettingPreference;
    }
    //</editor-fold>

    @NonNull public Observable<SocialDialogResult> show(@NonNull DTO whatToShare, boolean shareOnly)
    {
        this.shareOnly = shareOnly;
        return super.show(whatToShare);
    }

    @NonNull public Observable<UserTranslationSettingDTO> getNullableSettings()
    {
        return translationTokenCache.getOne(new TranslationTokenKey())
                .map(new Func1<Pair<TranslationTokenKey, TranslationToken>, UserTranslationSettingDTO>()
                {
                    @Override public UserTranslationSettingDTO call(Pair<TranslationTokenKey, TranslationToken> tokenPair)
                    {
                        UserTranslationSettingDTO setting = null;
                        try
                        {
                            setting = userTranslationSettingPreference.getOfSameTypeOrDefault(tokenPair.second);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return setting;
                    }
                });
    }

    @NonNull public Observable<String> getTargetLanguage()
    {
        return getNullableSettings()
                .map(new Func1<UserTranslationSettingDTO, String>()
                {
                    @Override public String call(@Nullable UserTranslationSettingDTO setting)
                    {
                        if (setting != null)
                        {
                            return setting.languageCode;
                        }
                        return resources.getConfiguration().locale.getLanguage();
                    }
                });
    }

    @NonNull public Observable<Boolean> isAutoTranslate()
    {
        return getNullableSettings().map(new Func1<UserTranslationSettingDTO, Boolean>()
        {
            @Override public Boolean call(@Nullable UserTranslationSettingDTO setting)
            {
                return setting != null && setting.autoTranslate;
            }
        });
    }

    @NonNull public Observable<Boolean> canTranslate(@NonNull final AbstractDiscussionCompactDTO discussionToTranslate)
    {
        return translationTokenCache.getOne(new TranslationTokenKey())
                .flatMap(new Func1<Pair<TranslationTokenKey, TranslationToken>, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(final Pair<TranslationTokenKey, TranslationToken> pair)
                    {
                        return getTargetLanguage().map(new Func1<String, Boolean>()
                        {
                            @Override public Boolean call(String targetLanguage)
                            {
                                return pair.second != null &&
                                        TranslationKeyFactory.isValidLangCode(discussionToTranslate.langCode) &&
                                        discussionToTranslate.langCode != null &&
                                        !discussionToTranslate.langCode.equals(targetLanguage);
                            }
                        });
                    }
                });
    }

    @NonNull public Observable<TranslateFlags> getTranslateFlags(@NonNull final AbstractDiscussionCompactDTO discussionToTranslate)
    {
        return translationTokenCache.getOne(new TranslationTokenKey())
                .map(new Func1<Pair<TranslationTokenKey, TranslationToken>, TranslateFlags>()
                {
                    @Override public TranslateFlags call(Pair<TranslationTokenKey, TranslationToken> tokenPair)
                    {
                        UserTranslationSettingDTO setting = null;
                        try
                        {
                            setting = userTranslationSettingPreference.getOfSameTypeOrDefault(tokenPair.second);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        String targetLanguage;
                        if (setting != null)
                        {
                            targetLanguage = setting.languageCode;
                        }
                        else
                        {
                            targetLanguage = resources.getConfiguration().locale.getLanguage();
                        }

                        boolean canTranslate = tokenPair.second != null &&
                                TranslationKeyFactory.isValidLangCode(discussionToTranslate.langCode) &&
                                discussionToTranslate.langCode != null &&
                                !discussionToTranslate.langCode.equals(targetLanguage);
                        boolean autoTranslate = setting != null && setting.autoTranslate;
                        return new TranslateFlags(
                                discussionToTranslate,
                                canTranslate,
                                autoTranslate);
                    }
                });
    }

    @NonNull public <T extends AbstractDiscussionCompactDTO> Observable<Pair<T, Boolean>> canTranslate(@NonNull Observable<T> discussionsToTranslate)
    {
        return discussionsToTranslate.flatMap(new Func1<T, Observable<Pair<T, Boolean>>>()
        {
            @Override public Observable<Pair<T, Boolean>> call(final T discussionCompactDTO)
            {
                return canTranslate(discussionCompactDTO)
                        .map(new Func1<Boolean, Pair<T, Boolean>>()
                        {
                            @Override public Pair<T, Boolean> call(Boolean canTranslate)
                            {
                                return Pair.create(discussionCompactDTO, canTranslate);
                            }
                        });
            }
        });
    }

    @NonNull @Override protected Observable<Pair<Dialog, ShareDialogLayout>> createDialog(@NonNull final DTO whatToShare)
    {
        return Observable.just(whatToShare)
                .flatMap(new Func1<DTO, Observable<? extends Boolean>>()
                {
                    @Override public Observable<? extends Boolean> call(DTO dto)
                    {
                        if (dto instanceof AbstractDiscussionCompactDTO)
                        {
                            return canTranslate((AbstractDiscussionCompactDTO) dto);
                        }
                        return Observable.just(false);
                    }
                })
                .flatMap(new Func1<Boolean, Observable<Pair<Dialog, ShareDialogLayout>>>()
                {
                    @Override public Observable<Pair<Dialog, ShareDialogLayout>> call(Boolean can)
                    {
                        if (can && !shareOnly)
                        {
                            Pair<Dialog, NewsDialogLayout> pair = NewsDialogFactory.createNewsDialogRx(activityHolder.get());
                            return Observable.just(Pair.create(pair.first, (ShareDialogLayout) pair.second));
                        }
                        return SocialShareTranslationHelper.super.createDialog(whatToShare);
                    }
                });
    }

    @NonNull @Override protected Observable<? extends SocialDialogResult> handleUserAction(
            @NonNull ShareDialogLayout.UserAction userAction,
            @NonNull DTO whatToShare)
    {
        if (userAction instanceof TranslateResult)
        {
            return translate((AbstractDiscussionCompactDTO) whatToShare);
        }
        return super.handleUserAction(userAction, whatToShare);
    }

    @NonNull public Observable<? extends SocialDialogResult> translate(
            @NonNull final AbstractDiscussionCompactDTO toTranslate)
    {
        final AbstractDiscussionCompactDTO translated = AbstractDiscussionCompactDTOFactory.clone(toTranslate);
        if (toTranslate.langCode != null)
        {
            return getTargetLanguage()
                    .flatMap(new Func1<String, Observable<TranslationKey>>()
                    {
                        @Override public Observable<TranslationKey> call(String targetLanguage)
                        {
                            return Observable.from(TranslationKeyFactory.createFrom(toTranslate, targetLanguage));
                        }
                    })
                    .flatMap(new Func1<TranslationKey, Observable<? extends Pair<TranslationKey, TranslationResult>>>()
                    {
                        @Override public Observable<? extends Pair<TranslationKey, TranslationResult>> call(TranslationKey translationKey)
                        {
                            return translationCache.getOne(translationKey);
                        }
                    })
                    .doOnNext(new Action1<Pair<TranslationKey, TranslationResult>>()
                    {
                        @Override public void call(Pair<TranslationKey, TranslationResult> pair)
                        {
                            AbstractDiscussionCompactDTOFactory.populateTranslation(translated, pair.first, pair.second) // TODO remove?
                            ;
                        }
                    })
                            //.onErrorResumeNext(Observable.empty())
                    .toList()
                    .map(new Func1<List<? extends Pair<TranslationKey, TranslationResult>>, TranslateResult>()
                    {
                        @Override public TranslateResult call(List<? extends Pair<TranslationKey, TranslationResult>> list)
                        {
                            return new TranslateResult(toTranslate, translated);
                        }
                    });
        }
        return Observable.empty();
    }

    public static class TranslateFlags
    {
        @NonNull public final AbstractDiscussionCompactDTO discussion;
        public final boolean canTranslate;
        public final boolean autoTranslate;

        public TranslateFlags(@NonNull AbstractDiscussionCompactDTO discussion,
                boolean canTranslate,
                boolean autoTranslate)
        {
            this.discussion = discussion;
            this.canTranslate = canTranslate;
            this.autoTranslate = autoTranslate;
        }
    }
}