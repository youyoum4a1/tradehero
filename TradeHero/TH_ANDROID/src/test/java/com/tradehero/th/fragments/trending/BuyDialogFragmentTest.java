package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.tradehero.RobolectricMavenTestRunner;
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
    public void testCashLeftShouldEqual100k()
    {
        String cashLeft = abstractTransactionDialogFragment.getCashLeft();

        assertThat(cashLeft).isEqualTo("US$ 100,000");
    }

    @Test
    public void testMaxValue()
    {
        SeekBar s = abstractTransactionDialogFragment.getSeekBar();

        assertThat(s.getMax()).isEqualTo(3225);
    }

    @Test
    public void testTradeValueOnMaxSliderValue()
    {
        SeekBar s = abstractTransactionDialogFragment.getSeekBar();

        int val = s.getMax();

        abstractTransactionDialogFragment.setTransactionQuantity(val);

        String tv = abstractTransactionDialogFragment.getValueText();
        assertThat(tv).isEqualTo("US$ 99,975");

        Integer qTv = abstractTransactionDialogFragment.getQuantity();
        assertThat(qTv).isEqualTo(val);

        String lTv = abstractTransactionDialogFragment.getCashLeft();
        assertThat(lTv).isEqualTo("US$ 25");
    }

    @Test
    public void testConfirmButtonShouldBeDisabled()
    {
        Button btn = abstractTransactionDialogFragment.getConfirmButton();

        assertThat(btn.isEnabled()).isEqualTo(false);

        abstractTransactionDialogFragment.setTransactionQuantity(0);

        assertThat(btn.isEnabled()).isEqualTo(false);
    }

    @Test
    public void testConfirmButtonShouldBeEnabled()
    {
        Button btn = abstractTransactionDialogFragment.getConfirmButton();

        assertThat(btn.isEnabled()).isEqualTo(false);

        abstractTransactionDialogFragment.setTransactionQuantity(10);

        assertThat(btn.isEnabled()).isEqualTo(true);
    }

    //TODO test the - value when quote = null
    //TODO test the dialog is onPause/Resume
    //TODO test the title
    //TODO test the price Info
    //TODO test the value in the center
    //TODO test the quickbuttonPrice
    //TODO test the quickButtonPrice disabled when cash is not enough
    //TODO test the quickButtonPrice and Value and Quantity
    //TODO test quickPrice and Seekbar at the same time
    //TODO test the transaction order
    //TODO test the Comments
    //TODO test the SocialLink
}
