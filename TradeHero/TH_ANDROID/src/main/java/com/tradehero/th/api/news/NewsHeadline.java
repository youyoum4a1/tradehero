package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

/**
 * Created by xavier on 2/21/14.
 */
public interface NewsHeadline extends DTO
{
    Date getDate();
    String getUrl();
    String getTitle();
}
