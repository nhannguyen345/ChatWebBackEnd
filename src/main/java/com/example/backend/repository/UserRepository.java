package com.example.backend.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
        Optional<User> findByEmail(String email);

        Optional<User> findByUsername(String username);

        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.birthdate = :birthdate, u.phone = :phone, u.address = :address WHERE u.id = :userId")
        int updateUserPersonalInfo(@Param("birthdate") LocalDate birthdate, @Param("phone") String phone,
                        @Param("address") String address, @Param("userId") int userId);

        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.fbLink = :fbLink, u.instaLink = :instaLink, u.twitterLink = :twitterLink WHERE u.id = :userId")
        int updateUserSocialInfo(@Param("fbLink") String fbLink, @Param("instaLink") String instaLink,
                        @Param("twitterLink") String twitterLink, @Param("userId") int userId);

        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.password = :password WHERE u.id = :userId")
        int updateUserPassword(@Param("password") String password, @Param("userId") int userId);

        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.avatarUrl = :avatarUrl WHERE u.id = :userId")
        int updateUserAvatar(@Param("userId") int userId, @Param("avatarUrl") String avatarUrl);
}
