package com.tradehero.th.fragments.trade;

import android.text.Editable;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.competition.ProviderCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import org.junit.Test;
import org.robolectric.Robolectric;

import static com.tradehero.THRobolectric.runBgUiTasks;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractTransactionDialogFragmentTestBase
{
    protected static final int CASH_BALANCE = 100000;

    @Inject UserProfileCache userProfileCache;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject SecurityPositionDetailCache securityPositionDetailCache;

    @Inject CurrentUserId currentUserId;

    protected SecurityId securityId;
    protected PortfolioId portfolioId;
    protected QuoteDTO quoteDTO;
    protected DashboardActivity activity;
    protected AbstractTransactionDialogFragment abstractTransactionDialogFragment;

    public void setUp() throws InterruptedException
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
        mockUserProfileDTO.fbLinked = true;
        mockUserProfileDTO.twLinked = false;
        mockUserProfileDTO.displayName = "mockedName";
        userProfileCache.put(currentUserId.toUserBaseKey(), mockUserProfileDTO);

        //TODO create MockObjects
        SecurityCompactDTO mockSecurityCompactDTO = new SecurityCompactDTO();
        mockSecurityCompactDTO.id = sId;
        mockSecurityCompactDTO.name = "Security Name";
        PositionDTOCompactList mockPositionsDTOCompactList = new PositionDTOCompactList();
        PortfolioDTO mockPortfolioDTO = new PortfolioDTO();
        mockPortfolioDTO.id = 94;
        mockPortfolioDTO.userId = 20;
        mockPortfolioDTO.cashBalance = CASH_BALANCE;
        mockPortfolioDTO.currencyDisplay = "US$";
        mockPortfolioDTO.currencyISO = "USD";
        ProviderCompactDTOList mockProviderCompactsDTOList = new ProviderCompactDTOList();
        int firstTradeAllTime = 0;

        SecurityPositionDetailDTO mockPositionDetailDTO =
                new SecurityPositionDetailDTO(
                        mockSecurityCompactDTO,
                        mockPositionsDTOCompactList,
                        mockPortfolioDTO,
                        mockProviderCompactsDTOList,
                        firstTradeAllTime);

        securityCompactCache.put(securityId, mockSecurityCompactDTO);
        securityPositionDetailCache.put(securityId, mockPositionDetailDTO);

        activity = Robolectric.setupActivity(DashboardActivity.class);
        abstractTransactionDialogFragment
                = AbstractTransactionDialogFragment.newInstance(securityId, portfolioId, quoteDTO, isBuy());
        abstractTransactionDialogFragment.show(activity.getFragmentManager(), "Test");

        runBgUiTasks(3);
    }

    @Test
    public void shouldGenerateTransactionFormDTOWithComments()
    {
        String comment = "Super awesome stock! 50% discount!!!";
        Editable editable = mock(Editable.class);
        when(editable.toString()).thenReturn(comment);

        abstractTransactionDialogFragment.unSpannedComment = editable;

        TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();

        assertThat(transactionFormDTO).isNotNull();
        assertThat(transactionFormDTO.tradeComment).isEqualTo(comment);
    }

    @Test
    public void shouldGenerateTransactionFormDTOWithoutComments()
    {
        TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();

        assertThat(transactionFormDTO).isNotNull();
        assertThat(transactionFormDTO.tradeComment).isNullOrEmpty();
    }

    public void tearDown()
    {
        abstractTransactionDialogFragment.dismiss();
    }

    abstract boolean isBuy();
}
