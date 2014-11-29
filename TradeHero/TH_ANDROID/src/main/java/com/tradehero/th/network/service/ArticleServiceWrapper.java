package com.tradehero.th.network.service;

import com.tradehero.th.api.article.ArticleInfoDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton
public class ArticleServiceWrapper
{
    private final ArticleServiceRx articleServiceRx;

    @Inject ArticleServiceWrapper(ArticleServiceRx articleServiceRx)
    {
        this.articleServiceRx = articleServiceRx;
    }

    public Observable<PaginatedDTO<ArticleInfoDTO>> getAllArticlesRx(PaginationDTO paginationDTO)
    {
        return articleServiceRx.getAllArticles(paginationDTO.page, paginationDTO.perPage);
    }
}
