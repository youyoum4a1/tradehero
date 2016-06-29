package com.androidth.general.fragments.competition;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.R;
import com.androidth.general.adapters.DTOAdapterNew;
import com.androidth.general.fragments.competition.zone.AbstractCompetitionZoneListItemView;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneDTO;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

public class CompetitionZoneListItemAdapter extends DTOAdapterNew<CompetitionZoneDTO>
{
    public static final int ITEM_TYPE_ADS = 0;
    public static final int ITEM_TYPE_HEADER = 2;
    public static final int ITEM_TYPE_PORTFOLIO = 5;
    public static final int ITEM_TYPE_ZONE_ITEM = 3;
    public static final int ITEM_TYPE_LEADERBOARD = 4;
    public static final int ITEM_TYPE_LEGAL_MENTIONS = 1;
    public static final int ITEM_TYPE_LOADING = 6;
    public static final int ITEM_TYPE_PRIZE_POOL = 7;
    public static final int ITEM_TYPE_WIZARD = 8;

    @NonNull private final Integer[] viewTypeToResId;
    @NonNull protected final PublishSubject<AbstractCompetitionZoneListItemView.UserAction> userActionSubject;
    @NonNull List<Pair<Integer, CompetitionZoneDTO>> elements;

    //<editor-fold desc="Constructors">
    public CompetitionZoneListItemAdapter(
            @NonNull Context context,
            @LayoutRes int zoneItemLayoutResId,
            @LayoutRes int adsResId,
            @LayoutRes int headerResId,
            @LayoutRes int prizeResId,
            @LayoutRes int portfolioResId,
            @LayoutRes int leaderboardResId,
            @LayoutRes int legalResId,
            @LayoutRes int wizardResId)
    {
        super(context, zoneItemLayoutResId);

        this.viewTypeToResId = new Integer[9];
        this.viewTypeToResId[ITEM_TYPE_ADS] = adsResId;
        this.viewTypeToResId[ITEM_TYPE_HEADER] = headerResId;
        this.viewTypeToResId[ITEM_TYPE_PRIZE_POOL] = prizeResId;
        this.viewTypeToResId[ITEM_TYPE_PORTFOLIO] = portfolioResId;
        this.viewTypeToResId[ITEM_TYPE_ZONE_ITEM] = layoutResourceId;
        this.viewTypeToResId[ITEM_TYPE_LEADERBOARD] = leaderboardResId;
        this.viewTypeToResId[ITEM_TYPE_LEGAL_MENTIONS] = legalResId;
        this.viewTypeToResId[ITEM_TYPE_LOADING] = R.layout.loading_item;
        this.viewTypeToResId[ITEM_TYPE_WIZARD] = wizardResId;

        this.userActionSubject = PublishSubject.create();
        this.elements = new ArrayList<>();
    }
    //</editor-fold>

    public CompetitionZoneListItemAdapter(
            @NonNull Context context,
            @LayoutRes int zoneItemLayoutResId,
            @LayoutRes int adsResId,
            @LayoutRes int headerResId,
            @LayoutRes int legalResId)
    {
        super(context, zoneItemLayoutResId);

        this.viewTypeToResId = new Integer[3];
        this.viewTypeToResId[ITEM_TYPE_ADS] = adsResId;
        this.viewTypeToResId[ITEM_TYPE_HEADER] = headerResId;
        this.viewTypeToResId[ITEM_TYPE_LEGAL_MENTIONS] = legalResId;

        this.userActionSubject = PublishSubject.create();
        this.elements = new ArrayList<>();
    }

    @NonNull public Observable<AbstractCompetitionZoneListItemView.UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    public void setElements(@NonNull List<Pair<Integer, CompetitionZoneDTO>> elements)
    {
        this.elements = elements;
        this.clear();
        List<CompetitionZoneDTO> list = new ArrayList<>();
        for (Pair<Integer, CompetitionZoneDTO> pair : elements)
        {
            list.add(pair.second);
        }
        this.addAll(list);
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public int getViewTypeCount()
    {
        return this.viewTypeToResId.length;
    }

    @Override public int getItemViewType(int position)
    {
        return elements.get(position).first;
    }

    @Override @LayoutRes public int getViewResId(int position)
    {
        return this.viewTypeToResId[getItemViewType(position)];
    }

    @Override public long getItemId(int position)
    {
        CompetitionZoneDTO item = getItem(position);
        return item != null ? item.hashCode() : position;
    }

    @NonNull @Override protected View inflate(int position, ViewGroup viewGroup)
    {
        View view = super.inflate(position, viewGroup);
        if (view instanceof AbstractCompetitionZoneListItemView)
        {
            ((AbstractCompetitionZoneListItemView) view).getUserActionObservable().subscribe(userActionSubject);
        }
        else if (view instanceof AdView)
        {
            ((AdView) view).getUserActionObservable().subscribe(userActionSubject);
        }
        return view;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        int viewType = getItemViewType(position);
        return viewType != ITEM_TYPE_HEADER &&
                viewType != ITEM_TYPE_LEGAL_MENTIONS &&
                viewType != ITEM_TYPE_LOADING;
    }
}
