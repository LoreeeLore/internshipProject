package com.studlabs.dao;

import com.studlabs.bll.model.User;

import java.util.Optional;

public interface UserDao {

    Optional<User> findByUsername(String username);

}
