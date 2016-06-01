package com.ayondo.academy.api.discussion;

import com.ayondo.academy.api.discussion.form.MessageCreateFormDTOTest;
import dagger.Module;

@Module(
        injects = {
                MessageCreateFormDTOTest.class
        },
        complete = false,
        library = true
)
public class ApiDiscussionTestModule
{
}
