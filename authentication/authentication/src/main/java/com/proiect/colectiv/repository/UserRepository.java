package com.proiect.colectiv.repository;


import com.proiect.colectiv.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsernameOrEmail(String username,String email);
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    User getUserByUsername(String username);

    User findById(long id);

    User findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE User SET password = ?2 WHERE id = ?1")
    void updateUserPassword(long accountId, String encodedPassword);

}
