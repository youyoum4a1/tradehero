package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;

public class PortfolioPopupMenuOnSubscribe implements Observable.OnSubscribe<MenuOwnedPortfolioId>
{
    @NotNull private final Context context;
    @NotNull private final View anchor;
    @NotNull private final Set<MenuOwnedPortfolioId> menuOwnedPortfolioIds;

    //<editor-fold desc="Constructors">
    public PortfolioPopupMenuOnSubscribe(
            @NotNull Context context,
            @NotNull View anchor,
            @NotNull Set<MenuOwnedPortfolioId> menuOwnedPortfolioIds)
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
                    @Override public boolean onMenuItemClick(android.view.MenuItem menuItem)
                    {
                        subscriber.onNext((MenuOwnedPortfolioId) menuItem.getTitle());
                        return true;
                    }
                });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()
        {
            @Override public void onDismiss(PopupMenu popupMenu)
            {
                subscriber.onCompleted();
                popupMenu.setOnDismissListener(null);
                popupMenu.setOnMenuItemClickListener(null);
            }
        });
        popupMenu.show();
    }
}
