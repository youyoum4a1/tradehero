package com.ayondo.academy.fragments.contestcenter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.competition.ProviderDTO;
import javax.inject.Inject;

public class ContestCenterActiveFragment extends ContestCenterBaseFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override public void recreateAdapter()
    {
        setContestCenterScreen(android.R.id.list);
        contestListAdapter = createAdapter();
        if (providerDTOs != null)
        {
            for (ProviderDTO providerDTO : providerDTOs)
            {
                contestListAdapter.add(new ProviderContestPageDTO(providerDTO));
            }
        }

        contestListView.setAdapter(contestListAdapter);
    }

    @Override @NonNull public ContestCenterTabType getCCTabType()
    {
        return ContestCenterTabType.ACTIVE;
    }
}
