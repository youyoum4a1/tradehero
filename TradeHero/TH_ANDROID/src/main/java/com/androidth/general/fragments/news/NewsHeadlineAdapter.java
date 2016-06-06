package com.androidth.general.fragments.news;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.R;
import com.androidth.general.adapters.ArrayDTOAdapter;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.androidth.general.models.discussion.UserDiscussionAction;
import com.androidth.general.utils.GraphicUtil;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class NewsHeadlineAdapter extends ArrayDTOAdapter<AbstractDiscussionCompactItemViewLinear.DTO, NewsHeadlineViewLinear>
{
    public Integer[] backgrounds = null;
    private Integer[] backgroundsArr = null;

    @NonNull private final PublishSubject<UserDiscussionAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public NewsHeadlineAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
        this.userActionSubject = PublishSubject.create();
        setItems(new ArrayList<AbstractDiscussionCompactItemViewLinear.DTO>());
        loadBackground();
    }
    //</editor-fold>

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
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
    public void setItems(@NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> items)
    {
        super.setItems(items);
        setBackgroundsArray();
    }

    @Override
    protected void fineTune(final int position, AbstractDiscussionCompactItemViewLinear.DTO dto, final NewsHeadlineViewLinear dtoView)
    {
        try
        {
            if (backgroundsArr[position] != null)
            {
                dtoView.setNewsBackgroundResource(backgroundsArr[position]);
            }
            else
            {
                int index = dto.viewHolderDTO.discussionDTO.id % backgrounds.length;
                backgroundsArr[position] = backgrounds[index];
                dtoView.setNewsBackgroundResource(backgrounds[index]);
            }
        }
        catch (OutOfMemoryError e)
        {
            Timber.e(e, null);
        }
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        View view = super.getView(position, convertView, viewGroup);
        GraphicUtil.setEvenOddBackground(position, view);
        return view;
    }

    @NonNull @Override protected View inflate(int position, ViewGroup viewGroup)
    {
        View view = super.inflate(position, viewGroup);
        ((NewsHeadlineViewLinear) view).getUserActionObservable().retry().subscribe(userActionSubject);
        return view;
    }
}
