package com.proiect.colectiv.service;

import com.proiect.colectiv.model.User;
import com.proiect.colectiv.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class);

    private UserRepository userRepository;

    @Autowired
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public Optional<User> findById(Long id) {

        return userRepository.findById(id);
    }

    public User findOne(String username) {
        return userRepository.getUserByUsername(username);
    }


    public User save(User user) {
        Optional<User> optionalUser;

        optionalUser = userRepository.findUserByUsernameOrEmail(user.getUsername(), user.getEmail());


        User user1 = optionalUser.isPresent() ? null : userRepository.save(user);

        return ofNullable(user1)
                .orElseThrow(() -> new EntityExistsException("User already exist: " + user.getUsername()));
    }

    public User update(User user) {
        User user1 = userRepository.save(user);

        return ofNullable(user1)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find user with ID: " + user.getId()));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User updateUserPassword(String email,String password){

        User user=userRepository.findByEmail(email);
        userRepository.updateUserPassword(user.getId(),password);

        return userRepository.findByEmail(user.getEmail());
    }
}
