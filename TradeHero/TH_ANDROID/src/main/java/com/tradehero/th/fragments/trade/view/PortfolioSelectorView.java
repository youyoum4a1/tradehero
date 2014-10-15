package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioIdList;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class PortfolioSelectorView extends RelativeLayout
{
    @InjectView(R.id.portfolio_selected) TextView selectedPortfolio;
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

    public void addMenuOwnedPortfolioIds(@NotNull MenuOwnedPortfolioIdList menuOwnedPortfolioIds)
    {
        for (MenuOwnedPortfolioId menu : menuOwnedPortfolioIds)
        {
            addMenuOwnedPortfolioId(menu);
        }
    }

    public void addMenuOwnedPortfolioId(@NotNull MenuOwnedPortfolioId menuOwnedPortfolioId)
    {
        if (currentMenu == null)
        {
            currentMenu = menuOwnedPortfolioId;
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
}
