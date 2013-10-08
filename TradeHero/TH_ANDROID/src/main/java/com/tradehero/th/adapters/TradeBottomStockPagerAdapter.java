package com.tradehero.th.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.trade.StockInfoFragment;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 12:42 PM To change this template use File | Settings | File Templates. */
public class TradeBottomStockPagerAdapter extends FragmentPagerAdapter implements DTOView<SecurityPositionDetailDTO>
{
    public static final String TAG = TradeBottomStockPagerAdapter.class.getSimpleName();

    private SecurityPositionDetailDTO securityPositionDetailDTO;

    // This feels like a HACK
    private Map<Integer, WeakReference<Fragment>> fragments = new HashMap<>();

    //<editor-fold desc="Constructors">
    public TradeBottomStockPagerAdapter(FragmentManager fm)
    {
        super(fm);
        THLog.d(TAG, "constructor");
    }

    public TradeBottomStockPagerAdapter(FragmentManager fm, SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        super(fm);
        THLog.d(TAG, "constructor with dto");
        this.securityPositionDetailDTO = securityPositionDetailDTO;
    }
    //</editor-fold>

    @Override public void destroyItem(ViewGroup container, int position, Object object)
    {
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public void display(SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        this.securityPositionDetailDTO = securityPositionDetailDTO;
        if (securityPositionDetailDTO != null)
        {
            for(WeakReference<Fragment> fragment: fragments.values())
            {
                if (fragment != null && fragment.get() != null)
                {
                    ((DTOView<SecurityCompactDTO>) fragment.get()).display(securityPositionDetailDTO.security);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override public int getCount()
    {
        return 3;
    }

    @Override public Fragment getItem(int i)
    {
        THLog.d(TAG, "getItem " + i);
        Fragment fragment = null;
        if (fragments.containsKey(i) && fragments.get(i) != null && fragments.get(i).get() != null)
        {
            THLog.d(TAG, "has item " + i);
            fragment = fragments.get(i).get();
        }
        else
        {
            THLog.d(TAG, "create item " + i);
            switch(i)
            {
                case 0:
                    // graph view
                    // TEMP
                    fragment = new StockInfoFragment();
                    break;
                case 1:
                    fragment = new StockInfoFragment();
                    break;
                case 2:
                    // news view
                    // TEMP
                    fragment = new StockInfoFragment();
                    break;
                default:
                    throw new IllegalArgumentException("Cannot handle i=" + i);
            }
            if (securityPositionDetailDTO != null)
            {
                ((DTOView<SecurityCompactDTO>) fragment).display(securityPositionDetailDTO.security);
            }
            fragments.put(i, new WeakReference<>(fragment));
        }

        return fragment;
    }


}
