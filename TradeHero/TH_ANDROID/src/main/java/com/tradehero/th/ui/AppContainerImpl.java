package com.tradehero.th.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.special.residemenu.ResideMenu;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.widget.reside.THResideMenuItemImpl;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

import static butterknife.ButterKnife.findById;

public class AppContainerImpl implements AppContainer
{
    private final ResideMenu resideMenu;
    private final ResideMenuItemClickListener resideMenuItemClickListener;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final UserProfileCacheRx userProfileCache;
    private Activity activity;

    @Inject public AppContainerImpl(
            ResideMenu resideMenu,
            ResideMenuItemClickListener resideMenuItemClickListener,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache)
    {
        this.resideMenu = resideMenu;
        this.resideMenuItemClickListener = resideMenuItemClickListener;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
    }

    @Override public ViewGroup wrap(final Activity activity)
    {
        this.activity = activity;
        activity.setContentView(R.layout.dashboard_with_bottom_bar);

        try
        {
            resideMenu.setBackground(R.drawable.login_bg_1);
        } catch (OutOfMemoryError e)
        {
            Timber.e(e, "Failed to load parallax_bg");
            resideMenu.setBackgroundResource(R.color.tradehero_reside_menu_bg);
        }
        resideMenu.attachTo((ViewGroup) activity.getWindow().getDecorView());
        LayoutInflater.from(activity).inflate(R.layout.residemenu_footer, resideMenu.getFooter(), true);

        currentUserId.getKeyObservable()
                .filter(new Func1<Integer, Boolean>()
                {
                    @Override public Boolean call(Integer userId)
                    {
                        return userId > 0;
                    }
                })
                .distinctUntilChanged()
                .flatMap(new Func1<Integer, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(Integer userId)
                    {
                        return userProfileCache.getOne(new UserBaseKey(userId))
                                .map(new PairGetSecond<UserBaseKey, UserProfileDTO>());
                    }
                })
                .map(new Func1<UserProfileDTO, Collection<RootFragmentType>>()
                {
                    @Override public Collection<RootFragmentType> call(UserProfileDTO userProfileDTO)
                    {
                        Collection<RootFragmentType> menus = new LinkedHashSet<>(RootFragmentType.forResideMenu());
                        if (userProfileDTO != null && userProfileDTO.isAdmin)
                        {
                            menus.add(RootFragmentType.ADMIN_SETTINGS);
                        }
                        return menus;
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Collection<RootFragmentType>>>()
                {
                    @Override public Observable<? extends Collection<RootFragmentType>> call(Throwable throwable)
                    {
                        return Observable.just(RootFragmentType.forResideMenu());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Collection<RootFragmentType>>()
                        {
                            @Override public void call(Collection<RootFragmentType> rootFragmentTypes)
                            {
                                List<View> menuItems = new ArrayList<>();
                                for (RootFragmentType tabType : rootFragmentTypes)
                                {
                                    View menuItem = createMenuItemFromTabType(activity, tabType);
                                    menuItem.setOnClickListener(resideMenuItemClickListener);
                                    menuItems.add(menuItem);
                                }
                                resideMenu.setMenuListener(new CustomOnMenuListener());
                                resideMenu.setMenuItems(menuItems);
                            }
                        },
                        new TimberOnErrorAction("Failed to load menus"));

        // only enable swipe from right to left
        resideMenu.setEnableSwipeLeftToRight(false);
        resideMenu.setEnableSwipeRightToLeft(true);

        return findById(activity, android.R.id.content);
    }

    class CustomOnMenuListener implements ResideMenu.OnMenuListener
    {
        @Override public void openMenu()
        {
            DeviceUtil.dismissKeyboard(activity);
            if (activity instanceof ResideMenu.OnMenuListener && !activity.isFinishing())
            {
                ((ResideMenu.OnMenuListener) activity).openMenu();
            }
        }

        @Override public void closeMenu()
        {
            if (activity instanceof ResideMenu.OnMenuListener && !activity.isFinishing())
            {
                ((ResideMenu.OnMenuListener) activity).closeMenu();
            }
        }
    }

    /**
     * TODO this is a hack due to time constraint
     */
    private View createMenuItemFromTabType(@NonNull Context context, @NonNull RootFragmentType tabType)
    {
        View created;
        if (tabType.hasCustomView())
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            created = inflater.inflate(tabType.viewResId, null);
            if (created instanceof TextResideMenuItem)
            {
                ((TextResideMenuItem) created).setTitle(tabType.stringResId);
            }
        }
        else
        {
            THResideMenuItemImpl resideMenuItem = new THResideMenuItemImpl(context, tabType.drawableResId, tabType.stringResId);
            resideMenuItem.setIcon(tabType.drawableResId);
            resideMenuItem.setTitle(tabType.stringResId);
            created = resideMenuItem;
        }
        created.setTag(tabType);

        //Add the background selector
        created.setBackgroundResource(R.drawable.basic_transparent_selector);

        return created;
    }
}
