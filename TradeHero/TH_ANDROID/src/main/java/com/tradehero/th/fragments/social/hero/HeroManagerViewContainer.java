package com.tradehero.th.fragments.social.hero;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;

/**
 * Created by xavier on 12/16/13.
 */
public class HeroManagerViewContainer
{
    public static final String TAG = HeroManagerViewContainer.class.getSimpleName();

    public final TextView followCreditCount;
    public final ImageView icnCoinStack;
    public final ImageButton btnBuyMore;
    public final ProgressBar progressBar;
    public final ListView heroListView;


    public HeroManagerViewContainer(View view)
    {
        super();

        //progressBar = (ProgressBar) view.findViewById(android.R.id.empty);
        progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
        followCreditCount = (TextView) view.findViewById(R.id.manage_heroes_follow_credit_count);
        icnCoinStack = (ImageView) view.findViewById(R.id.icn_credit_quantity);
        btnBuyMore = (ImageButton) view.findViewById(R.id.btn_buy_more);
        heroListView = (ListView) view.findViewById(R.id.heros_list);
    }

    public void displayFollowCount(UserProfileDTO userProfileDTO)
    {
        if (this.followCreditCount != null)
        {
            if (userProfileDTO != null)
            {
                this.followCreditCount.setText(String.format("+%.0f", userProfileDTO.ccBalance));
            }
        }
    }

    public void displayCoinStack(UserProfileDTO userProfileDTO)
    {
        if (this.icnCoinStack != null)
        {
            if (userProfileDTO != null)
            {
                this.icnCoinStack.getDrawable().setLevel((int) (double)
                        (userProfileDTO.ccBalance == null ? 0d : userProfileDTO.ccBalance));
            }
        }
    }
}
