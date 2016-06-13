package com.androidth.general.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.common.persistence.DTOKey;

/**
 * Created by ayushnvijay on 6/13/16.
 */
public class EmailDTO implements DTOKey {
    @NonNull
    public final String email;
    public EmailDTO(@NonNull String email)
    {
        this.email = email;
    }
    @Override public int hashCode()
    {
        return email.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof EmailDTO
                && ((EmailDTO) other).email.equals(email);
    }

}
