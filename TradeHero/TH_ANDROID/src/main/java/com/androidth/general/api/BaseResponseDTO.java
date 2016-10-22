package com.androidth.general.api;

import android.support.annotation.Nullable;
import android.util.Log;

import com.androidth.general.api.achievement.UserAchievementDTO;
import com.androidth.general.api.level.UserXPAchievementDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Here only to account for the fact that sometimes achievements are passed
 */
public class BaseResponseDTO
{
    @Nullable public List<UserXPAchievementDTO> xpEarned;
    @Nullable public List<UserAchievementDTO> achievements;
    @Nullable public Integer originalXP;

    @Nullable
    public Integer getOriginalXP() {
        return originalXP;
    }

    public void setOriginalXP(@Nullable Integer originalXP) {
        this.originalXP = originalXP;
    }

    public List<UserAchievementDTO> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<UserAchievementDTO> achievements) {
        this.achievements = achievements;
    }

    public List<UserXPAchievementDTO> getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(List<UserXPAchievementDTO> xpEarned) {
        this.xpEarned = xpEarned;
    }


    public static class CustomJsonDateDeserializer extends JsonDeserializer<Date>
    {
        public CustomJsonDateDeserializer() {
        }

        @Override
        public Date deserialize(JsonParser jsonparser,
                                DeserializationContext deserializationcontext) throws IOException {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Log.v("SignalR", "Deserialize "+jsonparser.getText());
            String date = jsonparser.getText();
            try {
                Log.v("SignalR", "Deserialize1 "+date);
                return format.parse(date);
            } catch (ParseException e) {
                Log.v("SignalR", "Deserialize2 "+e.getLocalizedMessage());
                throw new RuntimeException(e);
            }

        }

    }
}
