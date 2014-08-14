package com.tradehero.th.api.users.signup;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormTestHelper;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class LoginSignUpFormDTOFactoryTest
{
    @Inject UserFormTestHelper userFormTestHelper;
    @Inject Provider<LoginSignUpFormDTOFactory> loginSignUpFormDTOFactoryProvider;

    @Test public void knownChildUserFormsShouldNotCrashFactory()
            throws InstantiationException, IllegalAccessException
    {
        LoginSignUpFormDTOFactory signUpFormDTOFactory = loginSignUpFormDTOFactoryProvider.get();
        List<UserFormDTO> allForms = userFormTestHelper.createListAllSubclassInstances();
        List<LoginSignUpFormDTO> created = new ArrayList<>();
        for (UserFormDTO form : allForms)
        {
            created.add(signUpFormDTOFactory.create(form));
        }
        assertThat(created.size()).isEqualTo(allForms.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void basicUserFormThrowsIllegal()
    {
        loginSignUpFormDTOFactoryProvider.get().create(new UserFormDTO());
    }
}
