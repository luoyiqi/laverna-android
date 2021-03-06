package com.github.android.lvrn.lvrnproject.persistent.repository.core;

import android.support.annotation.NonNull;

import com.github.android.lvrn.lvrnproject.persistent.entity.Profile;
import com.github.android.lvrn.lvrnproject.persistent.repository.BasicRepository;

import java.util.List;

/**
 * @author Vadim Boitsov <vadimboitsov1@gmail.com>
 */

public interface ProfileRepository extends BasicRepository<Profile> {

    /**
     * A method which retrieves all profiles from a database.
     * @return a list of profiles.
     */
    @NonNull
    List<Profile> getAll();
}
