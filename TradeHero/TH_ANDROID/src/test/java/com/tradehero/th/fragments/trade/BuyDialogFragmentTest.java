package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import com.tradehero.THRobolectric;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.trade.view.QuickPriceButton;
import com.tradehero.th.fragments.trade.view.QuickPriceButtonSet;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BuyDialogFragmentTest extends AbstractTransactionDialogFragmentTestBase
{
    @Inject Context context; //Dummy inject

    @Before @Override
    public void setUp() throws InterruptedException
    {
        super.setUp();
        THRobolectric.setupActivity(DashboardActivityExtended.class).inject(this);
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

    @Test public void shouldNotShowWithQuoteNoAskNoBid()
    {
        QuoteDTO quoteDTO = mock(QuoteDTO.class);
        quoteDTO.ask = null;
        quoteDTO.bid = null;
        assertThat(BuyStockFragment.canShowDialog(quoteDTO, true)).isFalse();
        assertThat(BuyStockFragment.canShowDialog(quoteDTO, false)).isFalse();
    }

    @Test public void shouldShowOnlyBuyWithQuoteNoBid()
    {
        QuoteDTO quoteDTO = mock(QuoteDTO.class);
        quoteDTO.ask = 3d;
        quoteDTO.bid = null;
        assertThat(BuyStockFragment.canShowDialog(quoteDTO, true)).isTrue();
        assertThat(BuyStockFragment.canShowDialog(quoteDTO, false)).isFalse();
    }

    @Test public void shouldShowOnlySellWithQuoteNoAsk()
    {
        QuoteDTO quoteDTO = mock(QuoteDTO.class);
        quoteDTO.ask = null;
        quoteDTO.bid = 2d;
        assertThat(BuyStockFragment.canShowDialog(quoteDTO, true)).isFalse();
        assertThat(BuyStockFragment.canShowDialog(quoteDTO, false)).isTrue();
    }

    @Test public void shouldShowBothWithQuoteOk()
    {
        QuoteDTO quoteDTO = mock(QuoteDTO.class);
        quoteDTO.ask = 3d;
        quoteDTO.bid = 2d;
        assertThat(BuyStockFragment.canShowDialog(quoteDTO, true)).isTrue();
        assertThat(BuyStockFragment.canShowDialog(quoteDTO, false)).isTrue();
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
        SeekBar s = abstractTransactionDialogFragment.mSeekBar;

        Double priceCcy = abstractTransactionDialogFragment.getPriceCcy();
        int max = (int) Math.floor(CASH_BALANCE / priceCcy);

        assertThat(s.getMax()).isEqualTo(max);
    }

    @Test
    public void testTradeValueOnRandomSliderValue() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        SeekBar s = abstractTransactionDialogFragment.mSeekBar;

        int max = s.getMax();
        int min = 10;
        for (int i = 0; i < 10; i++)
        {
            int val = new Random().nextInt((max - min)) + min;

            this.performUserSetProgress(s, val);
            assertThat(s.getProgress()).isEqualTo(val);
            assertThat(s.getProgress()).isGreaterThan(0);

            Double value = val * abstractTransactionDialogFragment.getPriceCcy();

            assertThat(abstractTransactionDialogFragment.getTradeValueText()).isEqualTo(getSignedNumberString(value));
            assertThat(abstractTransactionDialogFragment.getQuantity()).isEqualTo(val);

            assertThat(abstractTransactionDialogFragment.getQuantityString()).isEqualTo(String.valueOf(val));
            assertThat(abstractTransactionDialogFragment.getCashShareLeft()).isEqualTo(getCashLeft(value));
            assertThat(abstractTransactionDialogFragment.mConfirm.isEnabled()).isEqualTo(true);

            TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
            assertThat(transactionFormDTO.quantity).isEqualTo(val);
        }
    }

    @Test
    public void testConfirmButtonShouldBeDisabled() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        Button btn = abstractTransactionDialogFragment.mConfirm;

        assertThat(btn.isEnabled()).isEqualTo(false);

        this.performUserSetProgress(abstractTransactionDialogFragment.mSeekBar, 0);

        assertThat(btn.isEnabled()).isEqualTo(false);
    }

    @Test
    public void testConfirmButtonShouldBeEnabled() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        Button btn = abstractTransactionDialogFragment.mConfirm;

        assertThat(btn.isEnabled()).isEqualTo(false);

        SeekBar s = abstractTransactionDialogFragment.mSeekBar;
        s.setPressed(true);

        int max = s.getMax();

        int rand = new Random().nextInt(max) + 1;

        this.performUserSetProgress(abstractTransactionDialogFragment.mSeekBar, rand);

        assertThat(btn.isEnabled()).isEqualTo(true);
    }

    @Test
    public void testTitleShouldMatchSecurityName()
    {
        String title = abstractTransactionDialogFragment.mStockNameTextView.getText().toString();
        assertThat(title).isEqualTo("Security Name");
    }

    @Test
    public void testValueShouldMatch() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        this.performUserSetProgress(abstractTransactionDialogFragment.mSeekBar, 500);
        assertThat(abstractTransactionDialogFragment.getQuantity()).isEqualTo(500);
        assertThat(abstractTransactionDialogFragment.getQuantityString()).isEqualTo(String.valueOf(500));
    }

    @Test
    public void testQuickButtonPriceShouldUpdateDialog()
    {
        QuickPriceButtonSet quickPriceButtonSet = abstractTransactionDialogFragment.mQuickPriceButtonSet;
        List<QuickPriceButton> list = quickPriceButtonSet.findButtons();

        SeekBar seekBar = abstractTransactionDialogFragment.mSeekBar;
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
            assertThat(abstractTransactionDialogFragment.mConfirm.isEnabled()).isEqualTo(true);

            TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
            assertThat(transactionFormDTO.quantity).isEqualTo(qty);
        }
    }

    public void testQuickButtonPriceSetShouldPartiallyBeDisabled()
    {
        //TODO
    }

    private void performUserSetProgress(SeekBar mSeekBar, int newProgress)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        Method privateSetProgressMethod = null;
        privateSetProgressMethod = ProgressBar.class.getDeclaredMethod("setProgress", Integer.TYPE, Boolean.TYPE);
        privateSetProgressMethod.setAccessible(true);
        privateSetProgressMethod.invoke(mSeekBar, newProgress, true);
    }

    private String getCashLeft(double transactionValue)
    {
        double cashLeft = CASH_BALANCE - transactionValue;

        return getSignedNumberString(cashLeft);
    }

    private String getSignedNumberString(double value)
    {
        THSignedNumber thTradeValue = THSignedMoney.builder(value)
                .withOutSign()
                .currency("US$")
                .build();
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
    public void testSocialShareIsOnByDefault()
    {
        assertThat(mBtnShareFb.isChecked()).isEqualTo(true);
    }

    @Test
    public void testSocialShareIsOffByDefault()
    {
        assertThat(mBtnShareTw.isChecked()).isEqualTo(false);
    }

    @Test
    public void testSocialShareShouldChangeStateAfterClick()
    {
        mBtnShareFb.setPressed(true);

        mBtnShareFb.performClick();

        TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
        assertThat(mBtnShareFb.isChecked()).isEqualTo(false);
        assertThat(transactionFormDTO.publishToFb).isEqualTo(false);

        mBtnShareFb.performClick();

        transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
        assertThat(mBtnShareFb.isChecked()).isEqualTo(true);
        assertThat(transactionFormDTO.publishToFb).isEqualTo(true);
    }

    @Test
    public void testShouldAskForSocialLinking()
    {
        mBtnShareLn.setPressed(true);
        mBtnShareLn.performClick();

        // Now that the DialogInterface is kept within the Observable chain, we do not
        // easily have access to it

/*        AlertDialog alertDialog = abstractTransactionFragment.mSocialLinkingDialog;

        assertThat(alertDialog).isNotNull();

        assertThat(alertDialog.isShowing()).isEqualTo(true);

        //dismiss the dialog for now
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();

        assertThat(alertDialog.isShowing()).isEqualTo(false);

        assertThat(abstractTransactionFragment.mSocialLinkingDialog).isNull();

        //Test whether the social link is turned off
        assertThat(mBtnShareLn.isChecked()).isEqualTo(false);

        TransactionFormDTO transactionFormDTO = abstractTransactionFragment.getBuySellOrder();
        assertThat(transactionFormDTO.publishToLi).isEqualTo(false); */
    }

    public void testShouldCallServerForLinkWhenLinkNowIsClicked()
    {
        //TODO test if the user click link now!
    }

    @Test
    public void testSocialIsOnAfterLinkSuccessful()
    {
        CompoundButton btnLinkedIn = mBtnShareLn;

        TransactionFormDTO transactionFormDTO = abstractTransactionDialogFragment.getBuySellOrder();
        assertThat(transactionFormDTO.publishToLi).isEqualTo(false);
        assertThat(btnLinkedIn.isChecked()).isEqualTo(false);
        assertThat(abstractTransactionDialogFragment.isSocialLinked(SocialNetworkEnum.LN).toBlocking().first()).isEqualTo(false);

        UserProfileDTO newUserProfileDTO = userProfileCache.getCachedValue(currentUserId.toUserBaseKey());
        newUserProfileDTO.liLinked = true;

        // Now we use Observable
        /*abstractTransactionFragment.onSuccessSocialLink(newUserProfileDTO, SocialNetworkEnum.LN);

        transactionFormDTO = abstractTransactionFragment.getBuySellOrder();
        assertThat(transactionFormDTO.publishToLi).isEqualTo(true);
        assertThat(btnLinkedIn.isChecked()).isEqualTo(true);
        assertThat(abstractTransactionFragment.isSocialLinked(SocialNetworkEnum.LN)).isEqualTo(true);*/
    }

    @Test
    public void testQuantityEditedShouldUpdateDialog()
    {
        EditText edt = abstractTransactionDialogFragment.mQuantityEditText;

        assertThat(edt.getText().toString()).isEqualTo("0");

        for (int i = 0; i < 10; i++)
        {
            int randInt = new Random().nextInt(abstractTransactionDialogFragment.mSeekBar.getMax()) + 1;
            Double value = randInt * abstractTransactionDialogFragment.getPriceCcy();

            edt.setText(String.valueOf(randInt));

            assertThat(edt.getText().toString()).isEqualTo(String.valueOf(randInt));
            assertThat(abstractTransactionDialogFragment.mSeekBar.getProgress()).isEqualTo(randInt);
            assertThat(abstractTransactionDialogFragment.mSeekBar.getProgress()).isEqualTo(randInt);
            assertThat(abstractTransactionDialogFragment.getTradeValueText()).isEqualTo(getSignedNumberString(value));
            assertThat(abstractTransactionDialogFragment.getCashShareLeft()).isEqualTo(getCashLeft(value));
        }
    }

    @Test
    public void testQuantityShouldReturnsZeroOnInvalidInput()
    {
        EditText edt = abstractTransactionDialogFragment.mQuantityEditText;

        assertThat(edt.getText().toString()).isEqualTo("0");

        List<String> invalidInputs = new ArrayList<>();

        invalidInputs.add("Autobots!");
        invalidInputs.add("-+-");
        invalidInputs.add("(╯°□°)╯︵ ┻━┻");
        invalidInputs.add("2xe2;['\\],.");
        invalidInputs.add("(҂‾ ▵‾)︻デ═一 \\(˚▽˚’!)/");
        invalidInputs.add("      ");
        invalidInputs.add("-1");
        invalidInputs.add("0.123");

        for (String invalid : invalidInputs)
        {
            edt.setText(invalid);
            assertThat(edt.getText().toString()).isEqualTo(String.valueOf(0));
            assertThat(abstractTransactionDialogFragment.mSeekBar.getProgress()).isEqualTo(0);
            assertThat(abstractTransactionDialogFragment.mSeekBar.getProgress()).isEqualTo(0);
            assertThat(abstractTransactionDialogFragment.getTradeValueText()).isEqualTo(getSignedNumberString(0));
            assertThat(abstractTransactionDialogFragment.getCashShareLeft()).isEqualTo(getCashLeft(0));
        }
    }

    @Test
    public void testQuantityShouldReturnMaxValueOnGreaterInput()
    {
        int max = abstractTransactionDialogFragment.mSeekBar.getMax();

        Double value = max * abstractTransactionDialogFragment.getPriceCcy();

        int moreThanMax = max + new Random().nextInt(100);

        EditText edt = abstractTransactionDialogFragment.mQuantityEditText;

        edt.setText(String.valueOf(moreThanMax));

        assertThat(edt.getText().toString()).isEqualTo(String.valueOf(max));
        assertThat(abstractTransactionDialogFragment.mSeekBar.getProgress()).isEqualTo(max);
        assertThat(abstractTransactionDialogFragment.mSeekBar.getProgress()).isEqualTo(max);
        assertThat(abstractTransactionDialogFragment.getTradeValueText()).isEqualTo(getSignedNumberString(value));
        assertThat(abstractTransactionDialogFragment.getCashShareLeft()).isEqualTo(getCashLeft(value));
    }

    @Test
    public void testWhenSelectedCursorIsOnTheEnd()
    {
        EditText edt = abstractTransactionDialogFragment.mQuantityEditText;
        edt.clearFocus();
        edt.requestFocus();
        edt.performClick();

        assertThat(edt.getSelectionEnd()).isEqualTo(edt.getText().length());
        assertThat(edt.getSelectionStart()).isEqualTo(edt.getText().length());
    }

    @Test
    public void testWhenQuantityEditedCursorIsOnTheEnd()
    {
        EditText edt = abstractTransactionDialogFragment.mQuantityEditText;
        int val = new Random().nextInt(abstractTransactionDialogFragment.mSeekBar.getMax()) + 1;
        edt.setText(String.valueOf(val));

        assertThat(edt.getSelectionEnd()).isEqualTo(edt.getText().length());
        assertThat(edt.getSelectionStart()).isEqualTo(edt.getText().length());
    }

    @Test
    public void testGenerateSharingOptionsHaveCorrectName()
    {
        SharingOptionsEvent sharingOptionsEvent = abstractTransactionDialogFragment.getSharingOptionEvent();

        assertThat(sharingOptionsEvent.getName()).isEqualTo(AnalyticsConstants.Trade_Buy);

        Map<String, String> map = sharingOptionsEvent.getAttributes();

        for (Map.Entry<String, String> val : map.entrySet())
        {
            assertThat(val.getKey()).isNotNull();
            assertThat(val.getValue()).isNotNull();
        }
    }

    @Test
    public void testGenerateSharingOptionsHaveCorrectPriceSelectionMethod()
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        String key = "lastSelectBy";
        String value = getMapValueFromSharingOptionsEvent(key);

        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(AnalyticsConstants.DefaultPriceSelectionMethod);

        //Slider
        performUserSetProgress(abstractTransactionDialogFragment.mSeekBar, 300);
        value = getMapValueFromSharingOptionsEvent(key);
        assertThat(value).isEqualTo(AnalyticsConstants.Slider);

        //EditText
        //TODO find a better way since this one assumes that the user click the editText before typing
        abstractTransactionDialogFragment.mQuantityEditText.performClick();
        abstractTransactionDialogFragment.mQuantityEditText.setText(String.valueOf(400));
        value = getMapValueFromSharingOptionsEvent(key);
        assertThat(value).isEqualTo(AnalyticsConstants.ManualQuantityInput);

        //QuickSet
        abstractTransactionDialogFragment.mQuickPriceButtonSet.findButtons().get(1).performClick();
        value = getMapValueFromSharingOptionsEvent(key);
        assertThat(value).isEqualTo(AnalyticsConstants.MoneySelection);
    }

    private String getMapValueFromSharingOptionsEvent(String key)
    {
        SharingOptionsEvent sharingOptionsEvent = abstractTransactionDialogFragment.getSharingOptionEvent();
        Map<String, String> map = sharingOptionsEvent.getAttributes();

        return map.get(key);
    }

    @Test
    public void testGetRefCCYReturnsCorrectValue()
    {
        assertThat(abstractTransactionDialogFragment.getPriceCcy()).isEqualTo(quoteDTO.ask * quoteDTO.toUSDRate);
    }

    //TODO test the value when quote = null
    //TODO test the subtitle - price Info
}
