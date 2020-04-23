package com.studlabs.dao;

import com.studlabs.bll.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    //just mock for now
    private List<User> users = Arrays.asList(new User("u", "user"), new User("username", "admin"));

    @Override
    public Optional<User> findByUsername(String username) {
        logger.info("getting user with username {}", username);

        List<User> user = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .collect(Collectors.toList());

        return user.size() > 0 ? Optional.of(user.get(0)) : Optional.empty();
    }
}
