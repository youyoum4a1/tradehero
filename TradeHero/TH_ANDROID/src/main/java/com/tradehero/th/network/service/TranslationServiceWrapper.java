package com.tradehero.th.network.service;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.models.news.MiddleCallbackPaginationNewsItem;
import com.tradehero.th.models.translation.TokenData;
import com.tradehero.th.models.translation.TranslationResult;
import com.tradehero.th.network.NetworkConstants;
import com.tradehero.th.utils.Constants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import com.tradehero.th.models.translation.TokenData;
import com.tradehero.th.models.translation.TranslationResult;
import com.tradehero.th.network.NetworkConstants;
import retrofit.RetrofitError;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by tradehero on 14-3-8.
 */
@Singleton
public class TranslationServiceWrapper {


        private final TranslationTokenService translationTokenService;
        private final TranslationService translationService;

        @Inject
        public TranslationServiceWrapper(TranslationTokenService translationTokenService, TranslationService translationService)
        {
            this.translationTokenService = translationTokenService;
            this.translationService = translationService;
        }

        public TranslationResult translate(String from, String to, String text) throws RetrofitError
        {
            TokenData tokenData = translationTokenService.requestToken(
                    NetworkConstants.TRANSLATION_REQ_TSCOPE,
                    NetworkConstants.TRANSLATION_GRANT_TYPE,
                    NetworkConstants.TRANSLATION_CLIENT_ID,
                    NetworkConstants.TRANSLATION_CLIENT_SECRET
                    );
            Timber.d("translate tokenData %s "+tokenData);
            if (tokenData == null || tokenData.getAccessToken() == null){
                return null;
            }
            String contentType = "text/plain";
            TranslationResult translationResult = translationService.requestForTranslation("Bearer "+tokenData.getAccessToken(), from, to, contentType, text);
            return translationResult;
        }

}
