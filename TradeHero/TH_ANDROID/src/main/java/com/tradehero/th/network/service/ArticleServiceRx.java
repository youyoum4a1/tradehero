package com.tradehero.th.network.service;

import com.tradehero.th.api.article.ArticleInfoDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface ArticleServiceRx
{
    @GET("/articles/all") Observable<PaginatedDTO<ArticleInfoDTO>> getAllArticles(
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
}
