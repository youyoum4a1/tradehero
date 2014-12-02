package com.tradehero.th.fragments.news;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class NewsHeadlineAdapter extends ArrayDTOAdapter<NewsItemCompactDTO, NewsHeadlineViewLinear>
{

    public Integer[] backgrounds = null;
    private Integer[] backgroundsArr = null;

    @Nullable private SecurityId securityId = null;

    public NewsHeadlineAdapter(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
        setItems(new ArrayList<>());
        loadBackground();
    }

    private void loadBackground()
    {
        TypedArray array = null;
        Integer[] backgroundResArray = null;
        try
        {
            array = getContext().getResources().obtainTypedArray(R.array.news_item_background_list);
            int len = array.length();
            backgroundResArray = new Integer[len];
            for (int i = 0; i < len; i++)
            {
                backgroundResArray[i] = array.getResourceId(i, 0);
            }
            backgrounds = backgroundResArray;
        }
        catch (Exception e)
        {
            Timber.e("loadBackground error", e);
        }
        finally
        {
            if (array != null)
            {
                array.recycle();
            }
        }
    }

    private void setBackgroundsArray()
    {
        int count = getCount();
        backgroundsArr = new Integer[count];
    }

    public int getBackgroundRes(int res)
    {
        return backgroundsArr[res];
    }

    @Override
    public void setItems(@NonNull List<NewsItemCompactDTO> items)
    {
        super.setItems(items);
        setBackgroundsArray();
    }

    public void setSecurityId(SecurityId securityId)
    {
        this.securityId = securityId;
    }

    @Override
    protected void fineTune(final int position, NewsItemCompactDTO dto, final NewsHeadlineViewLinear dtoView)
    {
        dtoView.linkWithSecurityId(securityId);
        try
        {
            if (backgroundsArr[position] != null)
            {
                dtoView.setNewsBackgroundResource(backgroundsArr[position]);
            }
            else
            {
                int index = dto.id % backgrounds.length;
                backgroundsArr[position] = backgrounds[index];
                dtoView.setNewsBackgroundResource(backgrounds[index]);
            }
        }
        catch (OutOfMemoryError e)
        {
            Timber.e(e, null);
        }
    }
}
