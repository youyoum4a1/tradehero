package com.tradehero.th.fragments.trending;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.fragments.trade.view.QuickPriceButton;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.utils.THSignedNumber;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class BuyDialogFragmentTest extends AbstractTransactionDialogFragmentTest
{
    @Inject Context context; //Dummy inject

    @Before @Override
    public void setUp()
    {
        super.setUp();
    }

    @After @Override
    public void tearDown()
    {
        super.tearDown();
    }

    @Override boolean isBuy()
    {
        return true;
    }

    @Test
    public void testCashLeftShouldEqualCashBalance()
    {
        String cashLeft = abstractTransactionDialogFragment.getCashShareLeft();

        assertThat(cashLeft).isEqualTo(getCashLeft(0));
    }

    @Test
    public void testMaxValue()
    {
        SeekBar s = abstractTransactionDialogFragment.getSeekBar();

        Double priceCcy = abstractTransactionDialogFragment.getPriceCcy();
        int max = (int) Math.floor(CASH_BALANCE / priceCcy);

        assertThat(s.getMax()).isEqualTo(max);
    }

    @Test
    public void testTradeValueOnRandomSliderValue()
    {
        SeekBar s = abstractTransactionDialogFragment.getSeekBar();

        int max = s.getMax();
        int min = 10;
        for (int i = 0; i < 50; i++)
        {
            int val = min + (int) (Math.random() * ((max - min) + 1));

            s.setProgress(val);
            assertThat(s.getProgress()).isEqualTo(val);
            assertThat(s.getProgress()).isGreaterThan(0);

            Double value = val * abstractTransactionDialogFragment.getPriceCcy();

            assertThat(abstractTransactionDialogFragment.getTradeValueText()).isEqualTo(getSignedNumberString(value));
            assertThat(abstractTransactionDialogFragment.getQuantity()).isEqualTo(val);
            assertThat(abstractTransactionDialogFragment.getQuantityString()).isEqualTo(String.valueOf(val));
            assertThat(abstractTransactionDialogFragment.getCashShareLeft()).isEqualTo(getCashLeft(value));
            assertThat(abstractTransactionDialogFragment.getConfirmButton().isEnabled()).isEqualTo(true);

            TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
            assertThat(transactionFormDTO.quantity).isEqualTo(val);
        }
    }

    @Test
    public void testConfirmButtonShouldBeDisabled()
    {
        Button btn = abstractTransactionDialogFragment.getConfirmButton();

        assertThat(btn.isEnabled()).isEqualTo(false);

        abstractTransactionDialogFragment.getSeekBar().setProgress(0);

        assertThat(btn.isEnabled()).isEqualTo(false);
    }

    @Test
    public void testConfirmButtonShouldBeEnabled()
    {
        Button btn = abstractTransactionDialogFragment.getConfirmButton();

        assertThat(btn.isEnabled()).isEqualTo(false);

        SeekBar s = abstractTransactionDialogFragment.getSeekBar();

        int max = s.getMax();

        int rand = new Random().nextInt(max);
        abstractTransactionDialogFragment.getSeekBar().setProgress(rand);

        assertThat(btn.isEnabled()).isEqualTo(true);
    }

    @Test
    public void testTitleShouldMatchSecurityName()
    {
        String title = abstractTransactionDialogFragment.getTitle();
        assertThat(title).isEqualTo("Security Name");
    }

    @Test
    public void testValueShouldMatch()
    {
        abstractTransactionDialogFragment.getSeekBar().setProgress(500);
        assertThat(abstractTransactionDialogFragment.getQuantity()).isEqualTo(500);
        assertThat(abstractTransactionDialogFragment.getQuantityString()).isEqualTo(String.valueOf(500));
    }

    @Test
    public void testQuickButtonPriceShouldUpdateDialog()
    {
        QuickPriceButtonSet quickPriceButtonSet = abstractTransactionDialogFragment.getQuickPriceButtonSet();
        List<QuickPriceButton> list = quickPriceButtonSet.getButtons();

        SeekBar seekBar = abstractTransactionDialogFragment.getSeekBar();
        Double priceCcy = abstractTransactionDialogFragment.getPriceCcy();
        for (QuickPriceButton quickPriceButton : list)
        {
            quickPriceButton.performClick();

            double price = quickPriceButton.getPrice();
            int qty = (int) Math.floor(price / priceCcy);
            Double value = (qty * abstractTransactionDialogFragment.getPriceCcy());

            assertThat(seekBar.getProgress()).isEqualTo(qty);
            assertThat(abstractTransactionDialogFragment.getQuantity()).isEqualTo(qty);
            assertThat(abstractTransactionDialogFragment.getQuantityString()).isEqualTo(String.valueOf(qty));
            assertThat(abstractTransactionDialogFragment.getTradeValueText()).isEqualTo(getSignedNumberString(value));
            assertThat(abstractTransactionDialogFragment.getCashShareLeft()).isEqualTo(getCashLeft(value));
            assertThat(abstractTransactionDialogFragment.getConfirmButton().isEnabled()).isEqualTo(true);

            TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
            assertThat(transactionFormDTO.quantity).isEqualTo(qty);
        }
    }

    public void testQuickButtonPriceSetShouldPartiallyBeDisabled()
    {
        //TODO
    }

    private String getCashLeft(double transactionValue)
    {
        double cashLeft = CASH_BALANCE - transactionValue; //Since we starts with 100 000

        return getSignedNumberString(cashLeft);
    }

    private String getSignedNumberString(double value)
    {
        THSignedNumber thTradeValue =
                new THSignedNumber(THSignedNumber.TYPE_MONEY, value, THSignedNumber.WITHOUT_SIGN,
                        "US$");
        return thTradeValue.toString();
    }

    //TODO test the transaction buy/sell order
    public void shouldReturnNullTransactionFormDTOWhenQuoteIsNull()
    {

    }

    public void shouldReturnNullTransactionFormDTOWhenPortfolioIsNull()
    {

    }

    @Test
    public void shouldGenerateTransactionFormDTOWithComments()
    {
        String comment = "Super awesome stock! 50% discount!!!";

        EditText mComments = abstractTransactionDialogFragment.getCommentView();

        mComments.setText(comment);

        TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();

        assertThat(transactionFormDTO).isNotNull();
        assertThat(transactionFormDTO.tradeComment).isEqualTo(comment);
    }

    @Test
    public void shouldGenerateTransactionFormDTOWithoutComments()
    {
        String comment = "";

        EditText mComments = abstractTransactionDialogFragment.getCommentView();

        mComments.setText(comment);

        TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();

        assertThat(transactionFormDTO).isNotNull();
        assertThat(transactionFormDTO.tradeComment).isEmpty();
    }

    @Test
    public void testSocialShareIsOnByDefault()
    {
        assertThat(abstractTransactionDialogFragment.getFacebookShareButton().isChecked()).isEqualTo(true);
    }

    @Test
    public void testSocialShareIsOffByDefault()
    {
        assertThat(abstractTransactionDialogFragment.getTwitterShareButton().isChecked()).isEqualTo(false);
    }

    @Test
    public void testSocialShareShouldChangeStateAfterClick()
    {
        abstractTransactionDialogFragment.getFacebookShareButton().setPressed(true);

        abstractTransactionDialogFragment.getFacebookShareButton().performClick();

        TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
        assertThat(abstractTransactionDialogFragment.getFacebookShareButton().isChecked()).isEqualTo(false);
        assertThat(transactionFormDTO.publishToFb).isEqualTo(false);

        abstractTransactionDialogFragment.getFacebookShareButton().performClick();

        transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
        assertThat(abstractTransactionDialogFragment.getFacebookShareButton().isChecked()).isEqualTo(true);
        assertThat(transactionFormDTO.publishToFb).isEqualTo(true);
    }

    @Test
    public void shouldAskForSocialLinking()
    {
        abstractTransactionDialogFragment.getLinkedInShareButton().setPressed(true);
        abstractTransactionDialogFragment.getLinkedInShareButton().performClick();

        AlertDialog alertDialog = abstractTransactionDialogFragment.getSocialLinkingDialog();

        assertThat(alertDialog).isNotNull();

        assertThat(alertDialog.isShowing()).isEqualTo(true);

        //dismiss the dialog for now
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();

        assertThat(alertDialog.isShowing()).isEqualTo(false);

        assertThat(abstractTransactionDialogFragment.getSocialLinkingDialog()).isNull();

        //Test whether the social link is turned off
        assertThat(abstractTransactionDialogFragment.getLinkedInShareButton().isChecked()).isEqualTo(false);

        TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
        assertThat(transactionFormDTO.publishToLi).isEqualTo(false);

    }

    public void testSocialIsOnAfterLinkSuccessful()
    {
        //TODO test if the user click link now!
    }

    //TODO test the value when quote = null
    //TODO test the subtitle - price Info
}
