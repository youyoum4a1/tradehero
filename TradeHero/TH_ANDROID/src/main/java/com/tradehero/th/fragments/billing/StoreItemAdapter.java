package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.R;
import java.util.HashMap;

/**
 * - InApp purchases
 *     - Virtual Dollars
 *     - Follow Credits
 *     - Stock Alerts
 *     - Reset Portfolio
 * - Manage Purchases
 *     - Manage Heroes
 *     - Manage Followers
 *     - Manage Stock Alerts
 * Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 7:08 PM To change this template use File | Settings | File Templates.
 */
public class StoreItemAdapter extends BaseAdapter
{
    public static final String TAG = StoreItemAdapter.class.getSimpleName();

    //<editor-fold desc="Known Positions">
    public static final int POSITION_IN_APP_PURCHASES = 0;
    public static final int POSITION_BUY_VIRTUAL_DOLLARS = 1;
    public static final int POSITION_BUY_FOLLOW_CREDITS = 2;
    public static final int POSITION_BUY_STOCK_ALERTS = 3;
    public static final int POSITION_BUY_RESET_PORTFOLIO = 4;
    public static final int POSITION_MANAGE_PURCHASES = 5;
    public static final int POSITION_MANAGE_HEROES = 6;
    public static final int POSITION_MANAGE_FOLLOWERS = 7;
    public static final int POSITION_MANAGE_STOCK_ALERTS = 8;
    //</editor-fold>

    //<editor-fold desc="View Types">
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_LIKE_BUTTON = 1;
    public static final int VIEW_TYPE_HAS_FURTHER = 2;
    //</editor-fold>

    protected final Context context;
    protected final LayoutInflater inflater;
    private HashMap<Integer, Integer> viewTypeToLayoutId;

    public StoreItemAdapter(Context context, LayoutInflater inflater)
    {
        super();
        this.context = context;
        this.inflater = inflater;
        buildViewTypeMap();
    }

    private void buildViewTypeMap()
    {
        viewTypeToLayoutId = new HashMap<>();
        viewTypeToLayoutId.put(VIEW_TYPE_HEADER, R.layout.store_item_header);
        viewTypeToLayoutId.put(VIEW_TYPE_LIKE_BUTTON, R.layout.store_item_like_button);
        viewTypeToLayoutId.put(VIEW_TYPE_HAS_FURTHER, R.layout.store_item_has_further);
    }

    @Override public int getCount()
    {
        return 9;
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public Object getItem(int i)
    {
        return null;
    }

    @Override public int getViewTypeCount()
    {
        return viewTypeToLayoutId.size();
    }

    @Override public int getItemViewType(int position)
    {
        int viewType;
        switch (position)
        {
            case POSITION_IN_APP_PURCHASES:
            case POSITION_MANAGE_PURCHASES:
                viewType = VIEW_TYPE_HEADER;
                break;
            case POSITION_BUY_VIRTUAL_DOLLARS:
            case POSITION_BUY_FOLLOW_CREDITS:
            case POSITION_BUY_STOCK_ALERTS:
            case POSITION_BUY_RESET_PORTFOLIO:
                viewType = VIEW_TYPE_LIKE_BUTTON;
                break;
            case POSITION_MANAGE_HEROES:
            case POSITION_MANAGE_FOLLOWERS:
            case POSITION_MANAGE_STOCK_ALERTS:
                viewType = VIEW_TYPE_HAS_FURTHER;
                break;
            default:
                throw new IllegalArgumentException("Unhandled position " + position);
        }
        return viewType;
    }

    public int getLayoutIdFromPosition(int position)
    {
        return viewTypeToLayoutId.get(getItemViewType(position));
    }

    @Override public View getView(int position, View view, ViewGroup viewGroup)
    {
        int layoutToInflate = getLayoutIdFromPosition(position);
        view = inflater.inflate(layoutToInflate, viewGroup, false);

        switch (getItemViewType(position))
        {
            case VIEW_TYPE_HEADER:
                view = fineTuneItemHeaderView(position, view, viewGroup);
                break;

            case VIEW_TYPE_LIKE_BUTTON:
                view = fineTuneItemLikeButton(position, view, viewGroup);
                break;

            case VIEW_TYPE_HAS_FURTHER:
                view = fineTuneItemHasFurther(position, view, viewGroup);
                break;

            default:
                throw new IllegalArgumentException("Unhandled position " + position);
        }
        return view;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) != VIEW_TYPE_HEADER;
    }

    //<editor-fold desc="Fine tune methods">
    private View fineTuneItemHeaderView(int position, View view, ViewGroup viewGroup)
    {
        int titleResId;
        switch (position)
        {
            case POSITION_IN_APP_PURCHASES:
                titleResId = R.string.store_header_in_app_purchases;
                break;
            case POSITION_MANAGE_PURCHASES:
                titleResId = R.string.store_header_manage_purchases;
                break;
            default:
                throw new IllegalArgumentException("Cannot handle position " + position);
        }
        ((StoreItemHeader) view).setTitleResId(titleResId);
        return view;
    }

    private View fineTuneItemLikeButton(int position, View view, ViewGroup viewGroup)
    {
        // TODO
        int titleResId;
        int iconResId;
        int buttonIconResId;
        switch (position)
        {
            case POSITION_BUY_VIRTUAL_DOLLARS:
                titleResId = R.string.store_buy_virtual_dollars;
                iconResId = R.drawable.icn_th_dollars;
                buttonIconResId = R.drawable.btn_buy_thd_large;
                break;
            case POSITION_BUY_FOLLOW_CREDITS:
                titleResId = R.string.store_buy_follow_credits;
                iconResId = R.drawable.icn_follow_credits;
                buttonIconResId = R.drawable.btn_buy_credits_large;
                break;
            case POSITION_BUY_STOCK_ALERTS:
                titleResId = R.string.store_buy_stock_alerts;
                iconResId = R.drawable.icn_stock_alert;
                buttonIconResId = R.drawable.btn_buy_stock_alerts;
                break;
            case POSITION_BUY_RESET_PORTFOLIO:
                titleResId = R.string.store_buy_reset_portfolio;
                iconResId = R.drawable.icn_reset_portfolio;
                buttonIconResId = R.drawable.btn_buy_reset_large;
                break;
            default:
                throw new IllegalArgumentException("Cannot handle position " + position);
        }
        ((StoreItemLikeButton) view).setTitleResId(titleResId);
        ((StoreItemLikeButton) view).setIconResId(iconResId);
        ((StoreItemLikeButton) view).setImageButtonResId(buttonIconResId);
        return view;
    }

    private View fineTuneItemHasFurther(int position, View view, ViewGroup viewGroup)
    {
        // TODO
        int titleResId;
        int iconResId;
        switch (position)
        {
            case POSITION_MANAGE_HEROES:
                titleResId = R.string.store_manage_heroes;
                iconResId = R.drawable.icn_follow_credits;
                break;
            case POSITION_MANAGE_FOLLOWERS:
                titleResId = R.string.store_manage_followers;
                iconResId = R.drawable.icn_view_followers;
                break;
            case POSITION_MANAGE_STOCK_ALERTS:
                titleResId = R.string.store_manage_stock_alerts;
                iconResId = R.drawable.icn_stock_alert;
                break;
            default:
                throw new IllegalArgumentException("Cannot handle position " + position);
        }
        ((StoreItemHasFurther) view).setTitleResId(titleResId);
        ((StoreItemHasFurther) view).setIconResId(iconResId);
        return view;
    }
    //</editor-fold>
}
