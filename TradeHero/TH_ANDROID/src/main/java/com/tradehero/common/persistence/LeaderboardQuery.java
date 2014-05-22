package com.tradehero.common.persistence;

public class LeaderboardQuery extends Query
{
    private static final String SORT_TYPE = LeaderboardQuery.class.getName() + ".sortType";
    private static String PAGE_KEY = LeaderboardQuery.class.getName() + ".page";
    private static String WIN_RATIO = LeaderboardQuery.class.getName() + ".winRatio";
    private static String MONTHLY_ACTIVITY = LeaderboardQuery.class.getName() + ".monthlyActivity";
    private static String HOLDING_PERIOD = LeaderboardQuery.class.getName() + ".holdingPeriod";

    public Integer getPage()
    {
        return (Integer) getProperty(PAGE_KEY);
    }

    public void setPage(Integer page)
    {
        setProperty(PAGE_KEY, page);
    }

    public Integer getSortType()
    {
        return (Integer) getProperty(SORT_TYPE);
    }

    public void setSortType(Integer sortType)
    {
        setProperty(SORT_TYPE, sortType);
    }

    public Float getWinRatio()
    {
        return (Float) getProperty(WIN_RATIO);
    }

    public void setWinRatio(Float winRatio)
    {
        setProperty(WIN_RATIO, winRatio);
    }

    public Float getHoldingPeriod()
    {
        return (Float) getProperty(HOLDING_PERIOD);
    }

    public void setHoldingPeriod(Float holdingPeriod)
    {
        setProperty(HOLDING_PERIOD, holdingPeriod);
    }

    public Float getMonthlyActivity()
    {
        return (Float) getProperty(MONTHLY_ACTIVITY);
    }

    public void setMonthlyActivity(Float monthlyActivity)
    {
        setProperty(MONTHLY_ACTIVITY, monthlyActivity);
    }
}
