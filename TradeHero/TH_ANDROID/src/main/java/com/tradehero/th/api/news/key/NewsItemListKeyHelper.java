package com.ayondo.academy.api.news.key;

import com.ayondo.academy.api.pagination.PaginationDTO;

/**
 * Created by thonguyen on 28/10/14.
 */
public class NewsItemListKeyHelper
{
    private NewsItemListKeyHelper() {}

    public static NewsItemListKey nextPage(NewsItemListKey newsItemListKey)
    {
        return nextWithStep(newsItemListKey, 1);
    }

    public static NewsItemListKey previousPage(NewsItemListKey newsItemListKey)
    {
        return nextWithStep(newsItemListKey, -1);
    }

    private static NewsItemListKey nextWithStep(NewsItemListKey newsItemListKey, int step)
    {
        if (newsItemListKey.page == null)
        {
            throw new IllegalStateException("page should not be null");
        }

        PaginationDTO paginationDTO = new PaginationDTO(newsItemListKey.page, newsItemListKey.perPage);
        return copy(newsItemListKey, paginationDTO);
    }

    /**
     * TODO this is horrible!!!
     * Too many type of NewsItemListKey, NewsItemListKey should be designed to be immutable and cloneable
     * @param newsItemListKey
     * @param paginationDTO
     * @return
     */
    public static NewsItemListKey copy(NewsItemListKey newsItemListKey, PaginationDTO paginationDTO)
    {
        if (newsItemListKey instanceof NewsItemListFeaturedKey)
        {
            return new NewsItemListFeaturedKey(paginationDTO.page, paginationDTO.perPage);
        }

        if (newsItemListKey instanceof NewsItemListGlobalKey)
        {
            return new NewsItemListGlobalKey(paginationDTO.page, paginationDTO.perPage);
        }

        if (newsItemListKey instanceof NewsItemListInterestKey)
        {
            return new NewsItemListInterestKey(paginationDTO.page, paginationDTO.perPage);
        }

        if (newsItemListKey instanceof NewsItemListRegionalKey)
        {
            NewsItemListRegionalKey castedNewsItemListKey = (NewsItemListRegionalKey) newsItemListKey;
            return new NewsItemListRegionalKey(castedNewsItemListKey.countryCode, castedNewsItemListKey.languageCode, paginationDTO.page, paginationDTO.perPage);
        }

        if (newsItemListKey instanceof NewsItemListSecurityKey)
        {
            NewsItemListSecurityKey castedNewsItemListKey = (NewsItemListSecurityKey) newsItemListKey;
            return new NewsItemListSecurityKey(castedNewsItemListKey.securityIntegerId, paginationDTO.page, paginationDTO.perPage);
        }

        if (newsItemListKey instanceof NewsItemListSocialKey)
        {
            NewsItemListSocialKey castedNewsItemListKey = (NewsItemListSocialKey) newsItemListKey;
            return new NewsItemListSocialKey(castedNewsItemListKey.categoryId, paginationDTO.page, paginationDTO.perPage);
        }

        if (newsItemListKey instanceof NewsItemListSeekingAlphaKey) {
            return new NewsItemListSeekingAlphaKey(paginationDTO.page, paginationDTO.perPage);
        }

        throw new IllegalStateException("Incorrect type");
    }
}
