package com.tradehero.th.fragments.settings.photo;

import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ChooseImageFromDTOFactory
{
    @Inject public ChooseImageFromDTOFactory()
    {
        super();
    }

    public List<ChooseImageFromDTO> getAll()
    {
        List<ChooseImageFromDTO> all = new ArrayList<>();
        all.add(new ChooseImageFromDTO(R.string.user_profile_choose_image_from_camera));
        all.add(new ChooseImageFromDTO(R.string.user_profile_choose_image_from_library));
        return all;
    }
}
