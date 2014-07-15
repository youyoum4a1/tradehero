package com.tradehero.th.fragments.trending;

import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.trade.AbstractTransactionDialogFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

public abstract class AbstractTransactionDialogFragmentTest
{

    @Inject UserProfileCache userProfileCache;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject SecurityPositionDetailCache securityPositionDetailCache;

    @Inject CurrentUserId currentUserId;

    protected SecurityId securityId;
    protected PortfolioId portfolioId;
    protected QuoteDTO quoteDTO;
    protected DashboardActivity activity;
    protected AbstractTransactionDialogFragment abstractTransactionDialogFragment;

    public void setUp()
    {
        int sId = 92;

        currentUserId.set(20);
        securityId = new SecurityId("EXC", "SYMBOL");
        portfolioId = new PortfolioId(94);
        quoteDTO = new QuoteDTO();

        quoteDTO.toUSDRate = 1.24;
        quoteDTO.ask = 25.00;
        quoteDTO.bid = 25.00;
        quoteDTO.currencyDisplay = "SG$";
        quoteDTO.currencyISO = "SGD";
        quoteDTO.securityId = sId;

        UserProfileDTO mockUserProfileDTO = new UserProfileDTO();
        userProfileCache.put(currentUserId.toUserBaseKey(), mockUserProfileDTO);

        //TODO create MockObjects
        SecurityCompactDTO mockSecurityCompactDTO = new SecurityCompactDTO();
        mockSecurityCompactDTO.id = sId;

        PortfolioCompactDTO mockPortfolioCompactDTO = new PortfolioCompactDTO();
        mockPortfolioCompactDTO.cashBalance = 100000;
        PositionDTOCompactList mockPositionsDTOCompactList = new PositionDTOCompactList();
        PortfolioDTO mockPortfolioDTO = new PortfolioDTO();
        ProviderDTOList mockProvidersDTOList = new ProviderDTOList();
        int firstTradeAllTime = 0;

        SecurityPositionDetailDTO mockPositionDetailDTO =
                new SecurityPositionDetailDTO(mockSecurityCompactDTO, mockPositionsDTOCompactList, mockPortfolioDTO, mockProvidersDTOList,
                        firstTradeAllTime);

        securityCompactCache.put(securityId, mockSecurityCompactDTO);
        portfolioCompactCache.put(portfolioId, mockPortfolioCompactDTO);
        securityPositionDetailCache.put(securityId, mockPositionDetailDTO);

        activity = Robolectric.setupActivity(DashboardActivity.class);
        abstractTransactionDialogFragment
                = AbstractTransactionDialogFragment.newInstance(securityId, portfolioId, quoteDTO, isBuy());
        abstractTransactionDialogFragment.show(activity.getSupportFragmentManager(), "Test");
    }

    public void tearDown()
    {
        abstractTransactionDialogFragment.dismiss();
    }

    abstract boolean isBuy();


}
