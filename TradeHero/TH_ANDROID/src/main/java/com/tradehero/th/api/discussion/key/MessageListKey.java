package com.tradehero.th.api.discussion.key;

import com.tradehero.common.api.PagedDTOKey;
import sun.plugin2.message.Message;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:05 PM To change this template use File | Settings | File Templates. */
public class MessageListKey implements Comparable<MessageListKey>, PagedDTOKey
{
    public static final int FIRST_PAGE = 1;

    public final Integer page;
    public final Integer perPage;

    //<editor-fold desc="Constructors">
    public MessageListKey(MessageListKey other)
    {
        this.page = other.page;
        this.perPage = other.perPage;
    }

    public MessageListKey(Integer page, Integer perPage)
    {
        this.page = page;
        this.perPage = perPage;
        validate();
    }

    public MessageListKey(Integer page)
    {
        this.page = page;
        this.perPage = null;
        validate();
    }

    protected MessageListKey()
    {
        this.page = null;
        this.perPage = null;
        validate();
    }
    //</editor-fold>

    /**
     * It should not be made protected or public as it is called in the constructor.
     */
    private void validate()
    {
        if (page == null && perPage != null)
        {
            throw new NullPointerException("Page cannot be null if perPage is not null");
        }
        else if (page != null && page < 0)
        {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        else if (perPage != null && perPage <= 0)
        {
            throw new IllegalArgumentException("PerPage cannot be zero or negative");
        }
    }
    @Override public int hashCode()
    {
        return (page == null ? 0 : page.hashCode()) ^
                (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return MessageListKey.class.isInstance(other) && equals(MessageListKey.class.cast(other));
    }

    public boolean equals(MessageListKey other)
    {
        return (other != null) &&
                (page == null ? other.page == null : page.equals(other.page)) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    @Override public int compareTo(MessageListKey another)
    {
        if (another == null)
        {
            return 1;
        }

        int pageCompare = page == null ? (another.page == null ? 0 : -1) : page.compareTo(another.page);
        if (pageCompare != 0)
        {
            return pageCompare;
        }

        return perPage == null ? (another.perPage == null ? 0 : -1) : perPage.compareTo(another.perPage);
    }

    @Override public Integer getPage()
    {
        return page;
    }

    public MessageListKey next()
    {
        return new MessageListKey(this.page + 1, perPage);
    }

    @Override public String toString()
    {
        return "SecurityListType{" +
                "page=" + page +
                ", perPage=" + perPage +
                '}';
    }
}
