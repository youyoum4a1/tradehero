package com.tradehero.th.models;

import java.util.ArrayList;

/**
 * Will remove it. Use UserProfileDTO
 */
@Deprecated
public class ProfileDTO
{

    private String thLinked;

    public String getThLinked()
    {
        return thLinked;
    }

    public void setThLinked(String thLinked)
    {
        this.thLinked = thLinked;
    }

    public String getCcPerMonthBalance()
    {
        return ccPerMonthBalance;
    }

    public void setCcPerMonthBalance(String ccPerMonthBalance)
    {
        this.ccPerMonthBalance = ccPerMonthBalance;
    }

    public String getAlertCount()
    {
        return alertCount;
    }

    public void setAlertCount(String alertCount)
    {
        this.alertCount = alertCount;
    }

    public String getCcBalance()
    {
        return ccBalance;
    }

    public void setCcBalance(String ccBalance)
    {
        this.ccBalance = ccBalance;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public ArrayList<Rank> getRank()
    {
        return rank;
    }

    public void setRank(Rank rank)
    {
        this.rank.add(rank);
    }

    public String getEmailNotificationsEnabled()
    {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(String emailNotificationsEnabled)
    {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public String getLiLinked()
    {
        return liLinked;
    }

    public void setLiLinked(String liLinked)
    {
        this.liLinked = liLinked;
    }

    public String getPushNotificationsEnabled()
    {
        return pushNotificationsEnabled;
    }

    public void setPushNotificationsEnabled(String pushNotificationsEnabled)
    {
        this.pushNotificationsEnabled = pushNotificationsEnabled;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public ArrayList<EnrolledProviders> getEnrolledProviders()
    {
        return enrolledProviders;
    }

    public void setEnrolledProviders(EnrolledProviders enrolledProviders)
    {
        this.enrolledProviders.add(enrolledProviders);
    }

    public String getMemberSince()
    {
        return memberSince;
    }

    public void setMemberSince(String memberSince)
    {
        this.memberSince = memberSince;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getUnreadCount()
    {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount)
    {
        this.unreadCount = unreadCount;
    }

    public Profilio getPortfolio()
    {
        return portfolio;
    }

    public void setPortfolio(Profilio portfolio)
    {
        this.portfolio = portfolio;
    }

    public ArrayList<Heroids> getHeroIds()
    {
        return heroIds;
    }

    public void setHeroIds(Heroids heroIds)
    {
        this.heroIds.add(heroIds);
    }

    public String getFbLinked()
    {
        return fbLinked;
    }

    public void setFbLinked(String fbLinked)
    {
        this.fbLinked = fbLinked;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public ArrayList<UserAlertPlans> getUserAlertPlans()
    {
        return userAlertPlans;
    }

    public void setUserAlertPlans(UserAlertPlans userAlertPlans)
    {
        this.userAlertPlans.add(userAlertPlans);
    }

    public String getTradesSharedCount_FB()
    {
        return tradesSharedCount_FB;
    }

    public void setTradesSharedCount_FB(String tradesSharedCount_FB)
    {
        this.tradesSharedCount_FB = tradesSharedCount_FB;
    }

    public String getTwLinked()
    {
        return twLinked;
    }

    public void setTwLinked(String twLinked)
    {
        this.twLinked = twLinked;
    }

    public String getUseTHPrice()
    {
        return useTHPrice;
    }

    public void setUseTHPrice(String useTHPrice)
    {
        this.useTHPrice = useTHPrice;
    }

    public String getFirstFollowAllTime()
    {
        return firstFollowAllTime;
    }

    public void setFirstFollowAllTime(String firstFollowAllTime)
    {
        this.firstFollowAllTime = firstFollowAllTime;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getPicture()
    {
        return picture;
    }

    public void setPicture(String picture)
    {
        this.picture = picture;
    }

    public String getFollowerCount()
    {
        return followerCount;
    }

    public void setFollowerCount(String followerCount)
    {
        this.followerCount = followerCount;
    }

    private String ccPerMonthBalance;
    private String alertCount;
    private String ccBalance;
    private String id;
    private ArrayList<Rank> rank;/*class*/

    private String emailNotificationsEnabled;
    private String liLinked;
    private String pushNotificationsEnabled;
    private String firstName;
    private ArrayList<EnrolledProviders> enrolledProviders;
    private String memberSince;
    private String lastName;
    private String unreadCount;
    private Profilio portfolio; //class
    private ArrayList<Heroids> heroIds;//class
    private String fbLinked;
    private String email;
    private ArrayList<UserAlertPlans> userAlertPlans;//class
    private String tradesSharedCount_FB;
    private String twLinked;
    private String useTHPrice;
    private String firstFollowAllTime;
    private String displayName;
    private String picture;
    private String followerCount;
}
