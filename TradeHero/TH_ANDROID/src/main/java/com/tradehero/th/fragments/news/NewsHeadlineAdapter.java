package com.tradehero.th.fragments.news;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class NewsHeadlineAdapter extends ArrayDTOAdapter<NewsItemDTOKey, NewsHeadlineViewLinear>
{

    public Integer[] backgrounds = null;
    private Integer[] backgroundsArr = null;

    @Nullable private SecurityId securityId = null;

    public NewsHeadlineAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        setItems(new ArrayList<NewsItemDTOKey>());
        loadBackground();
    }

    private void loadBackground()
    {
        TypedArray array = null;
        Integer[] backgroundResArray = null;
        try
        {
            array = context.getResources().obtainTypedArray(R.array.news_item_background_list);
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
    public void addItems(List<NewsItemDTOKey> data)
    {
        super.addItems(data);
        setBackgroundsArray();
    }

    @Override
    public void addItems(NewsItemDTOKey[] items)
    {
        super.addItems(items);
        setBackgroundsArray();
    }

    @Override
    public void addAll(Object[] items)
    {
        super.addAll(items);
        setBackgroundsArray();
    }

    @Override
    public void remove(Object object)
    {
        super.remove(object);
        setBackgroundsArray();
    }

    @Override
    public void addAll(Collection collection)
    {
        super.addAll(collection);
        setBackgroundsArray();
    }

    @Override
    public void add(Object object)
    {
        super.add(object);
        setBackgroundsArray();
    }

    @Override
    public void addItem(NewsItemDTOKey item)
    {
        super.addItem(item);
        setBackgroundsArray();
    }

    @Override
    public void setItems(List<NewsItemDTOKey> items)
    {
        super.setItems(items);
        setBackgroundsArray();
    }

    public void setSecurityId(SecurityId securityId)
    {
        this.securityId = securityId;
    }

    @Override
    protected void fineTune(final int position, NewsItemDTOKey dto, final NewsHeadlineViewLinear dtoView)
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
