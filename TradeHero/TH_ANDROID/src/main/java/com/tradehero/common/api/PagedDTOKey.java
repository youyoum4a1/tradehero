package com.tradehero.common.api;

import com.tradehero.common.persistence.DTOKey;

/**
 * Key that provides pagination, typically when querying for a list.
 * Created by xavier on 12/13/13.
 */
public interface PagedDTOKey extends DTOKey
{
    Integer getPage();
}
