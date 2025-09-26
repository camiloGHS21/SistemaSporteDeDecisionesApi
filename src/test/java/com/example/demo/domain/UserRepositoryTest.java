package com.example.demo.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByName_thenReturnUser() {
        // given
        User user = new User();
        user.setNombre_usuario("testuser");
        user.setEmail("test@example.com");
        user.setContrasena_hash("password");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByNombreUsuario(user.getNombre_usuario());

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getNombre_usuario()).isEqualTo(user.getNombre_usuario());
    }

}
