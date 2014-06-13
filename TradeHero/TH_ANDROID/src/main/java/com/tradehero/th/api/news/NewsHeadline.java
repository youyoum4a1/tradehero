package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

public interface NewsHeadline extends DTO
{
    Date getDate();
    String getUrl();
    String getTitle();
}
