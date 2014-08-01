package com.tradehero.th.fragments.contestcenter;

import com.tradehero.th.api.competition.ProviderDTO;
import org.jetbrains.annotations.NotNull;

public class ContestCenterActiveFragment extends ContestCenterBaseFragment
{
    @Override public void recreateAdapter()
    {
        setContestCenterScreen(android.R.id.list);
        contestListAdapter = createAdapter();
        if (providerDTOs != null)
        {
            for (@NotNull ProviderDTO providerDTO : providerDTOs)
            {
                //if (!providerDTO.isUserEnrolled)//requirement changed again,need show joined contest in active tab page
                //{
                    contestListAdapter.add(new ProviderContestPageDTO(providerDTO));
                    if (providerDTO.vip != null && providerDTO.vip)
                    {
                        contestListAdapter.add(new EmptyHeadLineDTO());
                    }
                //}
            }
        }

        contestListView.setAdapter(contestListAdapter);
    }

    @Override public ContestCenterTabType getCCTabType()
    {
        return ContestCenterTabType.ACTIVE;
    }
}
