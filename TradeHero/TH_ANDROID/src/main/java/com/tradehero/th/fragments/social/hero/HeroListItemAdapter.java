package com.tradehero.th.fragments.social.hero;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

public class HeroListItemAdapter extends DTOAdapterNew<Object>
{
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_CALL_ACTION = 1;
    public static final int VIEW_TYPE_EMPTY_PLACEHOLDER = 2;

    public static final String DTO_CALL_ACTION = "CallToAction";
    public static final String DTO_EMPTY = "NoFollower";

    @LayoutRes private final int callToActionResId;
    @LayoutRes private final int emptyPlaceholderResId;

    @NonNull private PublishSubject<HeroListItemView.UserAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public HeroListItemAdapter(
            @NonNull Context context,
            @LayoutRes int heroLayoutResId,
            @LayoutRes int callToActionResId,
            @LayoutRes int emptyPlaceholderResId)
    {
        super(context, heroLayoutResId);
        this.callToActionResId = callToActionResId;
        this.emptyPlaceholderResId = emptyPlaceholderResId;
        this.userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull public static List<Object> createObjects(
            @NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserBaseKey followerId,
            @Nullable List<HeroDTO> heroDTOs)
    {
        List<Object> list = new ArrayList<>();
        if (heroDTOs == null || heroDTOs.size() == 0)
        {
            if (currentUserId.toUserBaseKey().equals(followerId))
            {
                list.add(DTO_CALL_ACTION);
            }
            else
            {
                list.add(DTO_EMPTY);
            }
        }
        else
        {
            for (HeroDTO heroDTO : heroDTOs)
            {
                list.add(new HeroListItemView.DTO(resources, currentUserId, followerId, heroDTO));
            }
        }
        return list;
    }

    @NonNull public Observable<HeroListItemView.UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public int getViewTypeCount()
    {
        return 3;
    }

    @Override public int getItemViewType(int position)
    {
        Object item = getItem(position);
        if (item instanceof HeroListItemView.DTO)
        {
            return VIEW_TYPE_ITEM;
        }
        else if (item.equals(DTO_CALL_ACTION))
        {
            return VIEW_TYPE_CALL_ACTION;
        }
        else if (item.equals(DTO_EMPTY))
        {
            return VIEW_TYPE_EMPTY_PLACEHOLDER;
        }
        else
        {
            throw new IllegalArgumentException("Unhandled item " + item);
        }
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflate(position, parent);
        }
        if (convertView instanceof HeroListItemView)
        {
            ((HeroListItemView) convertView).display((HeroListItemView.DTO) getItem(position));
        }
        return convertView;
    }

    @NonNull @Override protected View inflate(int position, ViewGroup viewGroup)
    {
        View view = super.inflate(position, viewGroup);
        if (view instanceof HeroListItemView)
        {
            ((HeroListItemView) view).getUserActionObservable().subscribe(userActionSubject);
        }
        return view;
    }

    @Override @LayoutRes public int getViewResId(int position)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_ITEM:
                return super.getViewResId(position);

            case VIEW_TYPE_CALL_ACTION:
                return callToActionResId;

            case VIEW_TYPE_EMPTY_PLACEHOLDER:
                return emptyPlaceholderResId;

            default:
                throw new IllegalArgumentException("Unhandled type " + getItemViewType(position));
        }
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) != VIEW_TYPE_EMPTY_PLACEHOLDER;
    }
}
