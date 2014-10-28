package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.functions.Action1;

public class PortfolioSelectorView extends RelativeLayout
{
    @InjectView(R.id.portfolio_selected) TextView selectedPortfolio;
    @Nullable private OwnedPortfolioId defaultPortfolioId;
    @Nullable private MenuOwnedPortfolioId defaultMenuPortfolioId;
    @Nullable private MenuOwnedPortfolioId currentMenu;
    @NotNull private Set<MenuOwnedPortfolioId> usedMenuOwnedPortfolioIds;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PortfolioSelectorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        usedMenuOwnedPortfolioIds = new TreeSet<>();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setDefaultPortfolioId(@Nullable OwnedPortfolioId defaultPortfolioId)
    {
        this.defaultPortfolioId = defaultPortfolioId;
    }

    @Nullable public MenuOwnedPortfolioId getCurrentMenu()
    {
        return currentMenu;
    }

    public void addMenuOwnedPortfolioId(@NotNull MenuOwnedPortfolioId menuOwnedPortfolioId)
    {
        if (defaultPortfolioId == null)
        {
            defaultPortfolioId = new OwnedPortfolioId(menuOwnedPortfolioId);
        }
        if (defaultPortfolioId.equals(new OwnedPortfolioId(menuOwnedPortfolioId)))
        {
            defaultMenuPortfolioId = menuOwnedPortfolioId;
            currentMenu = menuOwnedPortfolioId;
        }
        if (currentMenu == null)
        {
            if (defaultMenuPortfolioId != null)
            {
                currentMenu = defaultMenuPortfolioId;
            }
            else
            {
                currentMenu = menuOwnedPortfolioId;
            }
        }
        this.usedMenuOwnedPortfolioIds.add(menuOwnedPortfolioId);
        display();
    }

    public void display()
    {
        selectedPortfolio.setText(currentMenu);
        setVisibility(usedMenuOwnedPortfolioIds.size() > 1 ? View.VISIBLE : View.GONE);
    }

    @NotNull public Observable<MenuOwnedPortfolioId> createMenuObservable()
    {
        return Observable.create(
                new PortfolioPopupMenuOnSubscribe(
                        getContext(),
                        selectedPortfolio,
                        usedMenuOwnedPortfolioIds))
                .doOnNext(new Action1<MenuOwnedPortfolioId>()
                {
                    @Override public void call(MenuOwnedPortfolioId menuOwnedPortfolioId)
                    {
                        currentMenu = menuOwnedPortfolioId;
                        display();
                    }
                });
    }

    public boolean defaultMenuIsNotDefaultPortfolio()
    {
        return defaultPortfolioId != null
                && defaultMenuPortfolioId != null
                && !defaultPortfolioId.equals(new OwnedPortfolioId(defaultMenuPortfolioId));
    }
}
