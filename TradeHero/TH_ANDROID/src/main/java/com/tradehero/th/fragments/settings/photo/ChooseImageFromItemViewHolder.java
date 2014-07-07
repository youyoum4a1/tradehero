package com.tradehero.th.fragments.settings.photo;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import org.jetbrains.annotations.NotNull;

public class ChooseImageFromItemViewHolder implements DTOView<ChooseImageFromDTO>
{
    @InjectView(R.id.text_choose_from) TextView chooseFromText;
    @NotNull private final Resources resources;
    private ChooseImageFromDTO chooseImageFromDTO;

    //<editor-fold desc="Constructors">
    public ChooseImageFromItemViewHolder(@NotNull Resources resources, View view)
    {
        super();
        initViews(view);
        this.resources = resources;
    }
    //</editor-fold>

    public void initViews(View view)
    {
        ButterKnife.inject(this, view);
    }

    @Override public void display(ChooseImageFromDTO dto)
    {
        linkWith(dto, true);
    }

    public void linkWith(ChooseImageFromDTO dto, boolean andDisplay)
    {
        this.chooseImageFromDTO = dto;
        if (andDisplay)
        {
            dispayText();
        }
    }

    public void dispayText()
    {
        if (chooseFromText != null)
        {
            if (chooseImageFromDTO == null)
            {
                chooseFromText.setText(R.string.na);
            }
            else
            {
                chooseFromText.setText(chooseImageFromDTO.getTitle(resources));
            }
        }
    }
}
