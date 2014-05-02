package com.tradehero.th.fragments.settings.photo;

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
        all.add(new ChooseImageFromCameraDTO());
        all.add(new ChooseImageFromLibraryDTO());
        return all;
    }
}
