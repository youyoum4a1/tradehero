package com.androidth.general.api.users;

import android.support.annotation.NonNull;
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

    @Override public boolean equalFields(@NonNull SearchUserListType other)
    {
        return other instanceof SearchAllowableRecipientListType
                && super.equalFields(other);
    }
}
