package com.tradehero.th.api.article;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;

/**
 * Created by Tho Nguyen on 11/21/2014.
 */
public class ArticleInfoDTO extends AbstractDiscussionCompactDTO
    implements Comparable<ArticleInfoDTO>
{
    public String image;
    public String headline;
    public String previewText;
    public String url;
    public boolean comingSoon;

    public ArticleInfoDTO()
    {
    }

    @Override public DiscussionKey getDiscussionKey()
    {
        return new ArticleInfoDTOKey(id);
    }

    @Override public int compareTo(ArticleInfoDTO another)
    {
        if (another == null) return 0;

        return (id - another.id);
    }
}
