package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.article.ArticleInfoDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton
public class ArticleServiceWrapper
{
    @NonNull private final ArticleServiceRx articleServiceRx;

    //<editor-fold desc="Constructor">
    @Inject ArticleServiceWrapper(@NonNull ArticleServiceRx articleServiceRx)
    {
        this.articleServiceRx = articleServiceRx;
    }
    //</editor-fold>

    @NonNull public Observable<PaginatedDTO<ArticleInfoDTO>> getAllArticlesRx(@NonNull PaginationDTO paginationDTO)
    {
        return articleServiceRx.getAllArticles(paginationDTO.page, paginationDTO.perPage);
    }
}
