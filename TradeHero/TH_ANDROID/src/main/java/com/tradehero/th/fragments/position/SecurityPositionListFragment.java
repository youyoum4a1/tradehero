package com.tradehero.th.fragments.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.position.partial.PositionCompactDisplayDTO;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.persistence.position.PositionListCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction1;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class SecurityPositionListFragment extends PositionListFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID = SecurityPositionListFragment.class.getName() + ".securityId";
    private SecurityId securityId;
    @Inject PositionListCacheRx positionListCacheRx;

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    @NonNull private static SecurityId getSecurityId(@NonNull Bundle args)
    {
        Bundle securityBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID);
        if (securityBundle == null)
        {
            throw new NullPointerException("SecurityId needs to be passed on");
        }
        return new SecurityId(securityBundle);
    }

    @Override protected void initVariableFromArgs(Bundle args)
    {
        securityId = getSecurityId(getArguments());
        shownUser = currentUserId.toUserBaseKey();
    }

    @Override protected void fetchPositions()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                Observable.combineLatest(
                        securityCompactCache.getOne(securityId)
                                .map(new PairGetSecond<SecurityId, SecurityCompactDTO>()),
                        positionListCacheRx.get(securityId)
                                .map(new PairGetSecond<SecurityId, PositionDTOList>()),
                        new Func2<SecurityCompactDTO, PositionDTOList, List<Object>>()
                        {
                            @Override
                            public List<Object> call(SecurityCompactDTO securityCompactDTO, PositionDTOList positionDTOs)
                            {
                                List<Object> viewDtos = new ArrayList<>();
                                if (positionDTOs.isEmpty())
                                {
                                    viewDtos.add(new PositionNothingView.DTO(getResources(), false));
                                }
                                else
                                {
                                    for (PositionDTO positionDTO : positionDTOs)
                                    {
                                        viewDtos.add(new PositionCompactDisplayDTO(
                                                getResources(),
                                                securityCompactDTO,
                                                positionDTO));
                                    }
                                }
                                return viewDtos;
                            }
                        }))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<Object>>()
                        {
                            @Override public void call(List<Object> dtoList)
                            {
                                linkWith(dtoList);
                            }
                        }, new TimberOnErrorAction1("Error fetching positions")));
    }

    @Override protected void handleHeaderAction()
    {
        //Do nothing
    }

    @Override public void handleDialogGoToTrade(boolean andClose, @NonNull SecurityCompactDTO securityCompactDTO, @NonNull PositionDTO positionDTO,
            @NonNull OwnedPortfolioId applicableOwnedPortfolioId)
    {
        //Do nothing
    }

    @Override protected PositionItemAdapter createPositionItemAdapter()
    {
        return new PositionItemAdapter(
                getActivity(),
                getLayoutResIds(),
                currentUserId
        );
    }

    @NonNull @Override protected Map<Integer, Integer> getLayoutResIds()
    {
        Map<Integer, Integer> layoutResIds = super.getLayoutResIds();
        layoutResIds.put(PositionItemAdapter.VIEW_TYPE_POSITION_COMPACT, R.layout.position_compact_view);
        return layoutResIds;
    }

    @Override protected void refreshSimplePage()
    {
        positionListCacheRx.invalidate(securityId);
        positionListCacheRx.get(securityId);
    }
}
