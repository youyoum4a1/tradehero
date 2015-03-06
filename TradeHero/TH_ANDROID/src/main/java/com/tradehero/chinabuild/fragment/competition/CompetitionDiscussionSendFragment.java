package com.tradehero.chinabuild.fragment.competition;

import android.os.Bundle;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;

/**
 * Created by palmer on 15/3/6.
 */
public class CompetitionDiscussionSendFragment extends DiscussSendFragment {

    private UserCompetitionDTO userCompetitionDTO;
    private int competitionId;

    @Override public void onResume() {
        super.onResume();
        getCompetitionArguments();
        initPostContent();
    }

    @Override protected DiscussionFormDTO buildDiscussionFormDTO()
    {
        DiscussionFormDTO discussionFormDTO = super.buildDiscussionFormDTO();
        discussionFormDTO.inReplyToId = getCompetitionId();
        return discussionFormDTO;
    }
    @Override protected DiscussionType getDiscussionType()
    {
        return DiscussionType.COMPETITION;
    }

    private int getCompetitionId(){
        if(userCompetitionDTO!=null){
            return userCompetitionDTO.id;
        }else{
            return competitionId;
        }
    }

    private void initPostContent(){
        String competitionName = "";
        if(userCompetitionDTO!=null){
            competitionName = userCompetitionDTO.name;
        }
        discussionPostContent.setHint(getString(R.string.discussion_new_post_hint, competitionName));
    }

    private void getCompetitionArguments(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.userCompetitionDTO = (UserCompetitionDTO) bundle.getSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO);
            if (userCompetitionDTO != null) {
                competitionId = userCompetitionDTO.id;
            } else {
                this.competitionId = bundle.getInt(CompetitionDetailFragment.BUNDLE_COMPETITION_ID, 0);
            }
        }
    }
}
