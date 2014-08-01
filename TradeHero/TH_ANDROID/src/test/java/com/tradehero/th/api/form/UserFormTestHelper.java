package com.tradehero.th.api.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

public class UserFormTestHelper
{
    //<editor-fold desc="Constructors">
    @Inject public UserFormTestHelper()
    {
        super();
    }
    //</editor-fold>

    @NotNull public Set<Class<? extends UserFormDTO>> createListAllSubclasses()
            throws IllegalAccessException, InstantiationException
    {
        Reflections reflections = new Reflections(this.getClass().getPackage().getName());
        return reflections.getSubTypesOf(UserFormDTO.class);
    }

    @NotNull public List<UserFormDTO> createListAllSubclassInstances()
            throws IllegalAccessException, InstantiationException
    {
        List<UserFormDTO> all = new ArrayList<>();
        for (Class<? extends UserFormDTO> subType : createListAllSubclasses())
        {
            all.add(subType.newInstance());
        }
        return all;
    }
}
