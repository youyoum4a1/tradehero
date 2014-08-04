package com.tradehero.th.api.form;

import com.tradehero.RobolectricMavenTestRunner;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class UserFormTestHelperTest
{
    @Inject UserFormTestHelper userFormTestHelper;

    @Test public void testChildClassesCountOk()
            throws InstantiationException, IllegalAccessException
    {
        assertThat(userFormTestHelper.createListAllSubclasses().size()).isGreaterThanOrEqualTo(6);
    }

    @Test public void testChildClassesContainsFacebook()
            throws InstantiationException, IllegalAccessException
    {
        Set<Class<? extends UserFormDTO>> classes = userFormTestHelper.createListAllSubclasses();
        for (Class<? extends UserFormDTO> userClass : classes)
        {
            if (userClass.equals(FacebookUserFormDTO.class))
            {
                return;
            }
        }
        throw new IllegalStateException("Facebook not found");
    }

    @Test public void testChildClassesDoesNotContainBaseClass()
            throws InstantiationException, IllegalAccessException
    {
        Set<Class<? extends UserFormDTO>> classes = userFormTestHelper.createListAllSubclasses();
        for (Class<? extends UserFormDTO> userClass : classes)
        {
            if (userClass.equals(UserFormDTO.class))
            {
                throw new IllegalStateException("UserForm base found");
            }
        }
    }

    @Test public void testChildClassInstancesDoesNotContainNull()
            throws InstantiationException, IllegalAccessException
    {
        List<UserFormDTO> instances = userFormTestHelper.createListAllSubclassInstances();
        for (UserFormDTO instance : instances)
        {
            if (instance == null)
            {
                throw new IllegalStateException("Found null instance");
            }
        }
    }

    @Test public void testChildClassInstancesContainsFacebook()
            throws InstantiationException, IllegalAccessException
    {
        List<UserFormDTO> instances = userFormTestHelper.createListAllSubclassInstances();
        for (UserFormDTO instance : instances)
        {
            if (instance instanceof FacebookUserFormDTO)
            {
                return;
            }
        }
        throw new IllegalStateException("FacebookForm not found instance");
    }
}
