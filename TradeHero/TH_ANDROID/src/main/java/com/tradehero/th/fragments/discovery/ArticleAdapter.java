package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.utils.GraphicUtil;
import rx.Observable;
import rx.subjects.PublishSubject;

public class ArticleAdapter extends ArrayDTOAdapter<AbstractDiscussionCompactItemViewLinear.DTO, ArticleItemView>
{
    @NonNull private final PublishSubject<UserDiscussionAction> userActionSubject;

    public ArticleAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
        this.userActionSubject = PublishSubject.create();
    }

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
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
        ((ArticleItemView) view).getUserActionObservable().subscribe(userActionSubject);
        return view;
    }
}
