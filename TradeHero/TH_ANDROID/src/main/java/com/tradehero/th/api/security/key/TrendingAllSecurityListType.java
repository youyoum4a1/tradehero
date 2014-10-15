package com.tradehero.th.api.security.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrendingAllSecurityListType extends TrendingSecurityListType
{
    public static final int ALL_SECURITY_LIST_TYPE_WATCH = 0;
    public static final int ALL_SECURITY_LIST_TYPE_HOLD = 1;//热门持有
    public static final int ALL_SECURITY_LIST_TYPE_CHINA_CONCEPT = 2;//中国概念
    public static final int ALL_SECURITY_LIST_TYPE_COMPETITION = 3;//比赛专属股票列表，根据competitionId获取
    public static final int ALL_SECURITY_LIST_TYPE_SEARCH = 4;//主动搜索出来的股票列表
    public static final int ALL_SECURITY_LIST_TYPE_RISE_PERCENT = 5;//涨幅榜


    public int type = ALL_SECURITY_LIST_TYPE_WATCH;
    public int competitionId = 0;
    public String q = "";

    //<editor-fold desc="Constructors">
    public TrendingAllSecurityListType(@NotNull TrendingSecurityListType other)
    {
        super(other);
    }

    public TrendingAllSecurityListType(@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(exchange, page, perPage);
    }

    public TrendingAllSecurityListType(@Nullable int securityType,@Nullable String exchange, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(exchange, page, perPage);
        type = securityType;
    }

    public TrendingAllSecurityListType(@Nullable int securityType,@Nullable int competitionId, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(page, perPage);
        type = securityType;
        this.competitionId = competitionId;
    }

    public TrendingAllSecurityListType(@Nullable int securityType,@Nullable int competitionId,@NotNull String searchWord, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(page, perPage);
        type = securityType;
        this.q = searchWord;
        this.competitionId = competitionId;
    }

    public TrendingAllSecurityListType(@Nullable String exchange, @Nullable Integer page)
    {
        super(exchange, page);
    }

    public TrendingAllSecurityListType(@Nullable String exchange)
    {
        super(exchange);
    }

    public TrendingAllSecurityListType(@Nullable Integer page, @Nullable Integer perPage)
    {
        super(page, perPage);
    }

    public TrendingAllSecurityListType(@Nullable Integer page)
    {
        super(page);
    }

    public TrendingAllSecurityListType()
    {
        super();
    }
    //</editor-fold>

    @Override public boolean equals(TrendingSecurityListType other)
    {
        return (other instanceof TrendingAllSecurityListType) && super.equals(other);
    }

    @Override public int compareTo(TrendingSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }

        if (!TrendingAllSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return TrendingAllSecurityListType.class.getName().compareTo(another.getClass().getName());
        }

        return compareTo(TrendingAllSecurityListType.class.cast(another));
    }

    public int compareTo(TrendingAllSecurityListType another)
    {
        return super.compareTo(another);
    }

    @Override public String toString()
    {
        return "TrendingAllSecurityListType{" +
                "exchange='" + exchange + "'" +
                "competitionId='" + competitionId + "'" +
                "q='" + q + "'" +
                ", page=" + getPage() +
                ", perPage=" + perPage +
                '}';
    }
}
