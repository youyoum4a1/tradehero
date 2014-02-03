package com.tradehero.th.models.market;

import com.tradehero.th.api.market.ExchangeDTO;
import java.util.Comparator;

/**
 * Created by xavier on 2/3/14.
 */
public class ExchangeDTODescriptionNameComparator implements Comparator<ExchangeDTO>
{
    public static final String TAG = ExchangeDTODescriptionNameComparator.class.getSimpleName();

    @Override public int compare(ExchangeDTO lhs, ExchangeDTO rhs)
    {
        if (lhs == null)
        {
            return rhs == null ? 0 : 1;
        }

        if (rhs == null)
        {
            return -1;
        }

        int descCompare = compareDesc(lhs, rhs);
        if (descCompare != 0)
        {
            return descCompare;
        }

        return compareName(lhs, rhs);
    }

    /**
     * It assumes parameters are not null
     * @param lhs
     * @param rhs
     * @return
     */
    protected int compareDesc(ExchangeDTO lhs, ExchangeDTO rhs)
    {
        if (lhs.desc == null)
        {
            return rhs.desc == null ? 0 : 1;
        }
        if (rhs.desc == null)
        {
            return -1;
        }
        return lhs.desc.compareTo(rhs.desc);
    }

    /**
     * It assumes parameters are not null
     * @param lhs
     * @param rhs
     * @return
     */
    protected int compareName(ExchangeDTO lhs, ExchangeDTO rhs)
    {
        if (lhs.name == null)
        {
            return rhs.name == null ? 0 : 1;
        }
        if (rhs.name == null)
        {
            return -1;
        }
        return lhs.name.compareTo(rhs.name);
    }
}
