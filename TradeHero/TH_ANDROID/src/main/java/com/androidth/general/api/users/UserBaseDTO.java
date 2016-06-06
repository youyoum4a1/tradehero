package com.androidth.general.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.api.ExtendedDTO;
import com.androidth.general.api.market.Country;
import java.util.Date;
import timber.log.Timber;

// TODO remove ExtendedDTO
public class UserBaseDTO extends ExtendedDTO
{
    public int id;
    @Nullable public String picture;
    public String displayName;
    public String firstName;
    public String lastName;
    public Date memberSince;
    public boolean isAdmin;
    public String activeSurveyURL;
    public String activeSurveyImageURL;
    public Double roiSinceInception;
    @Nullable public String countryCode;

    @JsonIgnore @NonNull public UserBaseKey getBaseKey()
    {
        return new UserBaseKey(id);
    }

    @JsonIgnore public boolean isOfficialAccount()
    {
        return getBaseKey().isOfficialAccount();
    }

    @JsonIgnore @Nullable public Country getCountry()
    {
        if (countryCode != null)
        {
            try
            {
                return Country.valueOf(countryCode);
            }
            catch (IllegalArgumentException e)
            {
                Timber.e(e, "Failed to get Country.%s", countryCode);
            }
        }
        return null;
    }

    @Override public int hashCode()
    {
        return Integer.valueOf(id).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof UserBaseDTO) && Integer.valueOf(id).equals(((UserBaseDTO) other).id);
    }

    @Override public String toString()
    {
        return "UserBaseDTO{" +
                "id=" + id +
                ", picture='" + picture + '\'' +
                ", displayName='" + displayName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", memberSince=" + memberSince +
                ", isAdmin=" + isAdmin +
                ", activeSurveyURL='" + activeSurveyURL + '\'' +
                ", activeSurveyImageURL='" + activeSurveyImageURL + '\'' +
                ", roiSinceInception=" + roiSinceInception +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
