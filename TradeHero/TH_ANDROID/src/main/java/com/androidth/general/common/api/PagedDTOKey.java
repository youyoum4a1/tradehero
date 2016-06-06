package com.androidth.general.common.api;

import com.androidth.general.common.persistence.DTOKey;

public interface PagedDTOKey extends DTOKey
{
    Integer getPage();
}
