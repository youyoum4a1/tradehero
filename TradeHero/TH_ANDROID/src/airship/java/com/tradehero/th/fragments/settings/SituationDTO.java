package com.tradehero.th.fragments.settings;

import android.support.annotation.NonNull;

import com.urbanairship.actions.Action.Situation;

import java.util.ArrayList;
import java.util.List;

public class SituationDTO implements CharSequence
{
    @NonNull public final Situation situation;
    //<editor-fold desc="Constructors">
    public SituationDTO(@NonNull Situation situation)
    {
        this.situation = situation;
    }
    //</editor-fold>

    @Override public int length()
    {
        return toString().length();
    }

    @Override public char charAt(int index)
    {
        return toString().charAt(index);
    }

    @Override public CharSequence subSequence(int start, int end)
    {
        return toString().subSequence(start, end);
    }

    @NonNull @Override public String toString()
    {
        //This is changed
        //return situation.name().replace('_', ' ').toLowerCase();
        return situation.toString().replace('_', ' ').toLowerCase();
    }

    @NonNull public static List<SituationDTO> getAll()
    {
        List<SituationDTO> all = new ArrayList<>();
        /*for (Situation situation : Situation.values())
        {
            all.add(new SituationDTO(situation));
        }*/
        return all;
    }
}
