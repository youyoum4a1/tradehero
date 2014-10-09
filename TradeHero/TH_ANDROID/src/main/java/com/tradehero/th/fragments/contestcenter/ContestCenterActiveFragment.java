package com.tradehero.th.fragments.contestcenter;

import android.content.Context;
import com.tradehero.th.api.competition.ProviderDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ContestCenterActiveFragment extends ContestCenterBaseFragment
{
    @Inject Context doNotRemoveOrItFails;

    @Override public void recreateAdapter()
    {
        setContestCenterScreen(android.R.id.list);
        contestListAdapter = createAdapter();
        if (providerDTOs != null)
        {
            for (@NotNull ProviderDTO providerDTO : providerDTOs)
            {
                    contestListAdapter.add(new ProviderContestPageDTO(providerDTO));
                    if (providerDTO.vip != null && providerDTO.vip)
                    {
                        contestListAdapter.add(new EmptyHeadLineDTO());
                    }
            }
        }

        contestListView.setAdapter(contestListAdapter);
    }

    @Override @NotNull public ContestCenterTabType getCCTabType()
    {
        return ContestCenterTabType.ACTIVE;
    }
}
