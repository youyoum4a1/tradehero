package com.tradehero.th.api.users;

import android.support.annotation.Nullable;

public class SearchAllowableRecipientListType extends SearchUserListType
{
    //<editor-fold desc="Constructors">
    public SearchAllowableRecipientListType(
            @Nullable String searchString,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(searchString, page, perPage);
    }
    //</editor-fold>
}
