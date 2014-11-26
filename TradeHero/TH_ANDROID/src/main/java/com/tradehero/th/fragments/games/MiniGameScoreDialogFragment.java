package com.tradehero.th.fragments.games;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.maurycy.ScaleImageView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.games.MiniGameScoreResponseDTO;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.models.number.THSignedNumber;
import javax.inject.Inject;

public class MiniGameScoreDialogFragment extends BaseDialogFragment
    implements DTOView<MiniGameScoreResponseDTO>
{
    @Inject Picasso picasso;

    @InjectView(R.id.banner) ScaleImageView logo;
    @InjectView(R.id.score) TextView scoreView;
    @InjectView(R.id.reward) TextView rewardView;
    @InjectView(R.id.description) TextView descriptionView;

    @Nullable protected MiniGameScoreResponseDTO scoreResponseDTO;

    @NonNull public static MiniGameScoreDialogFragment newInstance()
    {
        Bundle args = new Bundle();
        MiniGameScoreDialogFragment scoreDialog = new MiniGameScoreDialogFragment();
        scoreDialog.setArguments(args);
        return scoreDialog;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_mini_game_score, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        display();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void display(@Nullable MiniGameScoreResponseDTO scoreResponseDTO)
    {
        this.scoreResponseDTO = scoreResponseDTO;
        display();
    }

    protected void display()
    {
        if (scoreResponseDTO != null)
        {
            if (logo != null && scoreResponseDTO.imageUrl != null)
            {
                picasso.load(scoreResponseDTO.imageUrl)
                        .into(logo);
            }
            if (scoreView != null)
            {
                scoreView.setText(THSignedNumber.builder(scoreResponseDTO.score).build().toString());
            }
            if (rewardView != null)
            {
                rewardView.setText(getString(
                        R.string.game_reward_value,
                        THSignedNumber.builder(scoreResponseDTO.virtualDollars).build().toString()));
            }
            if (descriptionView != null && scoreResponseDTO.displayHtmlText != null)
            {
                descriptionView.setText(Html.fromHtml(scoreResponseDTO.displayHtmlText));
            }
        }
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick({android.R.id.button1, android.R.id.button2})
    protected void playAgainClicked(View view)
    {
        dismiss();
    }
}
