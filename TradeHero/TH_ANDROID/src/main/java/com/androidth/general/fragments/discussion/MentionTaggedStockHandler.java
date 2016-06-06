package com.androidth.general.fragments.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;
import com.androidth.general.common.fragment.HasSelectedItem;
import com.androidth.general.common.text.RichTextCreator;
import com.androidth.general.common.utils.EditableUtil;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.users.AllowableRecipientDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserSearchResultDTO;
import javax.inject.Inject;
import timber.log.Timber;

public class MentionTaggedStockHandler
{
    public static final String SECURITY_TAG_FORMAT = "[$%s](tradehero://security/%d_%s)";
    public static final String MENTIONED_FORMAT = "<@@%s,%d@>";

    @NonNull RichTextCreator parser;
    @Nullable EditText discussionPostContent;
    @Nullable private HasSelectedItem hasSelectedItemFragment;

    //<editor-fold desc="Constructors">
    @Inject public MentionTaggedStockHandler(@NonNull CurrentUserId currentUserId)
    {
        this.parser = new RichTextCreator(currentUserId);
    }
    //</editor-fold>

    public void setDiscussionPostContent(@Nullable EditText discussionPostContent)
    {
        this.discussionPostContent = discussionPostContent;
    }

    public void setHasSelectedItemFragment(
            @Nullable HasSelectedItem hasSelectedItemFragment)
    {
        this.hasSelectedItemFragment = hasSelectedItemFragment;
    }

    public void collectSelection()
    {
        HasSelectedItem hasSelectedItemFragmentCopy = hasSelectedItemFragment;
        if (hasSelectedItemFragmentCopy != null)
        {
            hasSelectedItemFragment = null;
            Object selected = hasSelectedItemFragmentCopy.getSelectedItem();
            if (selected instanceof UserSearchResultDTO)
            {
                onMentioned((UserSearchResultDTO) selected);
            }
            else if (selected instanceof AllowableRecipientDTO)
            {
                onMentioned((AllowableRecipientDTO) selected);
            }
            else if (selected instanceof SecurityCompactDTO)
            {
                onTagged((SecurityCompactDTO) selected);
            }
            else if (selected != null)
            {
                throw new IllegalArgumentException("Unhandled selected type: " + selected);
            }
        }
    }

    public void onMentioned(@NonNull UserSearchResultDTO userSearchResultDTO)
    {
        handleExtraText(String.format(MENTIONED_FORMAT, userSearchResultDTO.userthDisplayName, userSearchResultDTO.userId));
    }

    public void onMentioned(@NonNull AllowableRecipientDTO allowableRecipientDTO)
    {
        handleExtraText(String.format(MENTIONED_FORMAT, allowableRecipientDTO.user.displayName, allowableRecipientDTO.user.id));
    }

    public void onTagged(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        String exchangeSymbol = securityCompactDTO.getExchangeSymbol();
        String exchangeSymbolUrl = exchangeSymbol.replace(':', '_');
        handleExtraText(String.format(SECURITY_TAG_FORMAT, exchangeSymbol, securityCompactDTO.id, exchangeSymbolUrl));
    }

    private void handleExtraText(@NonNull String extraText)
    {
        EditText discussionPostContentCopy = discussionPostContent;
        if (discussionPostContentCopy != null)
        {
            Editable editable = discussionPostContentCopy.getText();

            String nonMarkUpText = extraText;
            if (!editable.toString().isEmpty())
            {
                int start = discussionPostContent.getSelectionStart();
                int end = discussionPostContent.getSelectionEnd();
                editable = editable.replace(start, end, extraText);
                nonMarkUpText = EditableUtil.unSpanText(editable).toString();
            }

            Timber.d("Original text: %s", nonMarkUpText);
            discussionPostContent.setText(parser.load(nonMarkUpText).create(), TextView.BufferType.SPANNABLE);
            discussionPostContent.setSelection(discussionPostContent.length());
        }
    }
}
