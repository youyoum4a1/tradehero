package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import rx.Observable;
import rx.subjects.PublishSubject;

public class FollowerListItemAdapter extends ArrayAdapter<Object>
{
    private static final int VIEW_TYPE_DTO = 0;
    private static final int VIEW_TYPE_ACTION = 1;
    public static final String ITEM_CALL_TO_ACTION = "CallToAction";

    @LayoutRes protected final int followerResId;
    @LayoutRes protected final int actionResId;
    @NonNull private final PublishSubject<UserAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public FollowerListItemAdapter(
            @NonNull Context context,
            @LayoutRes int followerResId,
            @LayoutRes int actionResId)
    {
        super(context, followerResId);
        this.followerResId = followerResId;
        this.actionResId = actionResId;
        this.userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        return super.getItem(position) instanceof FollowerListItemView.DTO
                ? VIEW_TYPE_DTO
                : VIEW_TYPE_ACTION;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflate(position, parent);
        }
        if (convertView instanceof FollowerListItemView)
        {
            ((FollowerListItemView) convertView).display((FollowerListItemView.DTO) getItem(position));
        }
        return convertView;
    }

    @NonNull protected View inflate(int position, ViewGroup parent)
    {
        View view = LayoutInflater.from(getContext()).inflate(getLayoutRes(position), parent, false);
        if (view instanceof FollowerListItemView)
        {
            ((FollowerListItemView) view).getUserActionObservable().subscribe(userActionSubject);
        }
        if (view instanceof FollowerListCallToActionItemView)
        {
            ((FollowerListCallToActionItemView) view).getTradeClickedObservable().subscribe(userActionSubject);
        }
        return view;
    }

    @LayoutRes protected int getLayoutRes(int position)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_DTO:
                return followerResId;

            case VIEW_TYPE_ACTION:
                return actionResId;

            default:
                throw new IllegalArgumentException("Unhandled view type " + getItemViewType(position));
        }
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) == VIEW_TYPE_DTO;
    }

    @NonNull public Observable<UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    public interface UserAction
    {
    }
}
