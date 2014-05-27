package com.tradehero.th.models.share;

abstract class BaseShareDestination implements ShareDestination
{
    @Override public int hashCode()
    {
        return Integer.valueOf(getNameResId()).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof ShareDestination) &&
                Integer.valueOf(getNameResId()).equals(((ShareDestination) other).getNameResId());
    }
}
