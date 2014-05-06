package com.tradehero.common.api;

import com.tradehero.common.persistence.DTOKey;


public interface PagedDTOKey extends DTOKey
{
    Integer getPage();
}
