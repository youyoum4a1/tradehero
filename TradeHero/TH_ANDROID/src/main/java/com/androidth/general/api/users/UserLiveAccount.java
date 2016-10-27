package com.androidth.general.api.users;

import com.androidth.general.common.persistence.DTO;

import java.util.Date;

import io.realm.RealmObject;

public class UserLiveAccount extends RealmObject implements DTO
{
    public int id;
    public int userId;
    public int ssoProviderId;
    public String userName;
    public String userNameTwo;
    public Date createdOnUTC;
    public boolean isActive;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getSsoProviderId() {
        return ssoProviderId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserNameTwo() {
        return userNameTwo;
    }

    public Date getCreatedOnUTC() {
        return createdOnUTC;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "UserLiveAccounts{" +
                "id=" + id +
                ", userId=" + userId +
                ", ssoProviderId=" + ssoProviderId +
                ", userName='" + userName + '\'' +
                ", userNameTwo='" + userNameTwo + '\'' +
                ", createdOnUTC=" + createdOnUTC +
                ", isActive=" + isActive +
                '}';
    }
}