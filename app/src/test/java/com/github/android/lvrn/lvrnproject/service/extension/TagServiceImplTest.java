package com.github.android.lvrn.lvrnproject.service.extension;

import com.github.android.lvrn.lvrnproject.BuildConfig;
import com.github.android.lvrn.lvrnproject.persistent.database.DatabaseManager;
import com.github.android.lvrn.lvrnproject.persistent.entity.Profile;
import com.github.android.lvrn.lvrnproject.persistent.repository.extension.impl.ProfileRepositoryImpl;
import com.github.android.lvrn.lvrnproject.persistent.repository.extension.impl.TagRepositoryImpl;
import com.github.android.lvrn.lvrnproject.service.extension.impl.ProfileServiceImpl;
import com.github.android.lvrn.lvrnproject.service.extension.impl.TagServiceImpl;
import com.github.android.lvrn.lvrnproject.service.form.ProfileForm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * @author Vadim Boitsov <vadimboitsov1@gmail.com>
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TagServiceImplTest {

    private Profile profile;

    private TagService tagService;

    @Before
    public void setUp() {
        DatabaseManager.initializeInstance(RuntimeEnvironment.application);

        ProfileService profileService = new ProfileServiceImpl(new ProfileRepositoryImpl());
        profileService.openConnection();
        profileService.create(new ProfileForm("Temp profile"));
        profile = profileService.getAll().get(0);
        profileService.closeConnection();

        tagService = new TagServiceImpl(new TagRepositoryImpl(), profileService);
    }

    @Test
    public void serviceShouldCreateEntity() {
//        tagService.create(new TagForm(profile.getId(), "simple_tag"));
    }

    @After
    public void finish() {
        tagService.closeConnection();
    }
}