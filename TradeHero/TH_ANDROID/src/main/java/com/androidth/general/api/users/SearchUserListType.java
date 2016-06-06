package com.androidth.general.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SearchUserListType extends UserListType
{
    //<editor-fold desc="Fields">
    @Nullable public final String searchString;
    @Nullable public final Integer page;
    @Nullable public final Integer perPage;
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public SearchUserListType(
            @Nullable String searchString,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        this.searchString = searchString;
        this.page = page;
        this.perPage = perPage;
    }
    //</editor-fold>

    @Override @Nullable public Integer getPage()
    {
        return page;
    }

    @Override public int hashCode()
    {
        return (searchString == null ? 0 : searchString.hashCode()) ^
                (page == null ? 0 : page.hashCode()) ^
                (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equalFields(@NonNull UserListType other)
    {
        return other instanceof SearchUserListType
                && equalFields((SearchUserListType) other);
    }

    public boolean equalFields(@NonNull SearchUserListType other)
    {
        return (searchString == null ? other.searchString == null : searchString.equals(other.searchString)) &&
                (page == null ? other.page == null : page.equals(other.page)) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(@NonNull UserListType other)
    {
        if (!(other instanceof SearchUserListType))
        {
            return SearchUserListType.class.getName().compareTo(((Object) other).getClass().getName());
        }

        SearchUserListType searchUserListType = (SearchUserListType) other;

        int stringCompare = searchString.compareTo(searchUserListType.searchString);
        if (stringCompare != 0)
        {
            return stringCompare;
        }
        int pageCompare = page.compareTo(searchUserListType.page);
        if (pageCompare != 0)
        {
            return pageCompare;
        }
        return perPage.compareTo(searchUserListType.perPage);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "SearchUserListType{" +
                "searchString='" + searchString + '\'' +
                ", page=" + page +
                ", perPage=" + perPage +
                '}';
    }
}
