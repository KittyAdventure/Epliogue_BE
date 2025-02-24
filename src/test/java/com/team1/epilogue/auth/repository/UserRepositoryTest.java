package com.team1.epilogue.auth.repository;

import com.team1.epilogue.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        // given
        User user = User.builder()
                .userId("repoUser")
                .password("encrypted")
                .nickname("repoNick")
                .name("Repo User")
                .birthdate("1990-01-01")
                .email("repo@example.com")
                .phone("010-1111-2222")
                .profilePhoto("http://example.com/photo.jpg")
                .build();
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByUserId("repoUser");

        // then
        assertTrue(found.isPresent());
        assertEquals("repo@example.com", found.get().getEmail());
    }

    @Test
    public void testExistsByMethods() {
        // given
        User user = User.builder()
                .userId("repoUser2")
                .password("encrypted")
                .nickname("repoNick2")
                .name("Repo User 2")
                .birthdate("1991-01-01")
                .email("repo2@example.com")
                .phone("010-3333-4444")
                .profilePhoto("http://example.com/photo2.jpg")
                .build();
        userRepository.save(user);

        // when & then
        assertTrue(userRepository.existsByUserId("repoUser2"));
        assertTrue(userRepository.existsByEmail("repo2@example.com"));
    }
}
