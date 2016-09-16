package com.androidth.general.fragments.kyc.dto;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.androidth.general.models.fastfill.Gender;

import java.util.ArrayList;
import java.util.List;

public class GenderDTO
{
    @NonNull public final Gender gender;
    @NonNull public final String text;

    public GenderDTO(@NonNull Resources resources, @NonNull Gender gender)
    {
        this(gender, resources.getString(gender.dropDownText));
    }

    public GenderDTO(@NonNull Gender gender, @NonNull String text)
    {
        this.gender = gender;
        this.text = text;
    }

    @Override public String toString()
    {
        return text;
    }

    @NonNull public static List<GenderDTO> createList(@NonNull Resources resources, @NonNull List<Gender> genders)
    {
        List<GenderDTO> created = new ArrayList<>();
        for (Gender gender : genders)
        {
            created.add(new GenderDTO(resources, gender));
        }
        return created;
    }

    @Override public int hashCode()
    {
        return gender.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        if (o == null) return false;
        if (o == this) return true;
        return o instanceof GenderDTO && ((GenderDTO) o).gender.equals(gender);
    }
}
