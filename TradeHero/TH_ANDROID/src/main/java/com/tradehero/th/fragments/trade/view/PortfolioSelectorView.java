package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import rx.Observable;

public class PortfolioSelectorView extends RelativeLayout
{
    @InjectView(R.id.portfolio_selected) TextView selectedPortfolio;
    @Nullable private OwnedPortfolioId defaultPortfolioId;
    @Nullable private MenuOwnedPortfolioId defaultMenuPortfolioId;
    @Nullable private MenuOwnedPortfolioId currentMenu;
    @NonNull private final Set<MenuOwnedPortfolioId> usedMenuOwnedPortfolioIds;

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

    public void addMenuOwnedPortfolioId(@NonNull MenuOwnedPortfolioId menuOwnedPortfolioId)
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

    @NonNull public Observable<MenuOwnedPortfolioId> createMenuObservable()
    {
        return Observable.create(
                new PortfolioPopupMenuOnSubscribe(
                        getContext(),
                        selectedPortfolio,
                        usedMenuOwnedPortfolioIds))
                .doOnNext(menuOwnedPortfolioId -> {
                    currentMenu = menuOwnedPortfolioId;
                    display();
                });
    }
}
