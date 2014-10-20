package com.tradehero.th.fragments.discussion;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.text.RichTextCreator;
import com.tradehero.common.utils.EditableUtil;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class MentionTaggedStockHandler
{
    public static final String SECURITY_TAG_FORMAT = "[$%s](tradehero://security/%d_%s)";
    public static final String MENTIONED_FORMAT = "<@@%s,%d@>";

    @NotNull EditableUtil editableUtil;
    @NotNull RichTextCreator parser;
    @Nullable EditText discussionPostContent;
    @Nullable private HasSelectedItem hasSelectedItemFragment;

    //<editor-fold desc="Constructors">
    @Inject public MentionTaggedStockHandler(
            @NotNull EditableUtil editableUtil,
            @NotNull RichTextCreator parser)
    {
        this.editableUtil = editableUtil;
        this.parser = parser;
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
            else if (selected instanceof SecurityCompactDTO)
            {
                onTagged((SecurityCompactDTO) selected);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled selected type: " + selected);
            }
        }
    }

    public void onMentioned(@NotNull UserSearchResultDTO userSearchResultDTO)
    {
        handleExtraText(String.format(MENTIONED_FORMAT, userSearchResultDTO.userthDisplayName, userSearchResultDTO.userId));
    }

    public void onTagged(@NotNull SecurityCompactDTO securityCompactDTO)
    {
        String exchangeSymbol = securityCompactDTO.getExchangeSymbol();
        String exchangeSymbolUrl = exchangeSymbol.replace(':', '_');
        handleExtraText(String.format(SECURITY_TAG_FORMAT, exchangeSymbol, securityCompactDTO.id, exchangeSymbolUrl));
    }

    private void handleExtraText(@NotNull String extraText)
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
                nonMarkUpText = editableUtil.unSpanText(editable).toString();
            }

            Timber.d("Original text: %s", nonMarkUpText);
            discussionPostContent.setText(parser.load(nonMarkUpText).create(), TextView.BufferType.SPANNABLE);
            discussionPostContent.setSelection(discussionPostContent.length());
        }
    }
}
