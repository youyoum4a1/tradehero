package com.ayondo.academy.fragments.trade.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import com.ayondo.academy.models.portfolio.MenuOwnedPortfolioId;
import java.util.Set;
import rx.Observable;
import rx.Subscriber;

class PortfolioPopupMenuOnSubscribe implements Observable.OnSubscribe<MenuOwnedPortfolioId>
{
    @NonNull private final Context context;
    @NonNull private final View anchor;
    @NonNull private final Set<MenuOwnedPortfolioId> menuOwnedPortfolioIds;

    //<editor-fold desc="Constructors">
    public PortfolioPopupMenuOnSubscribe(
            @NonNull Context context,
            @NonNull View anchor,
            @NonNull Set<MenuOwnedPortfolioId> menuOwnedPortfolioIds)
    {
        this.context = context;
        this.anchor = anchor;
        this.menuOwnedPortfolioIds = menuOwnedPortfolioIds;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super MenuOwnedPortfolioId> subscriber)
    {
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        for (MenuOwnedPortfolioId menuOwnedPortfolioId : menuOwnedPortfolioIds)
        {
            popupMenu.getMenu().add(
                    Menu.NONE,
                    Menu.NONE,
                    Menu.NONE,
                    menuOwnedPortfolioId);
        }
        popupMenu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener()
                {
                    @Override public boolean onMenuItemClick(MenuItem menuItem)
                    {
                        subscriber.onNext((MenuOwnedPortfolioId) menuItem.getTitle());
                        return true;
                    }
                });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()
        {
            @Override public void onDismiss(PopupMenu popupMenu1)
            {
                subscriber.onCompleted();
                popupMenu1.setOnDismissListener(null);
                popupMenu1.setOnMenuItemClickListener(null);
            }
        });
        popupMenu.show();
    }
}
