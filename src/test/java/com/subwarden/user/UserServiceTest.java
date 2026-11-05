package com.subwarden.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String RAW_PASSWORD = "plain-test-password";

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldStoreHashInsteadOfRawPassword() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.register("anna@example.com", RAW_PASSWORD);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getPasswordHash()).isNotEqualTo(RAW_PASSWORD);
        assertThat(passwordEncoder.matches(RAW_PASSWORD, saved.getPasswordHash())).isTrue();
    }

    @Test
    void shouldNormalizeEmailBeforeSaving() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.register("   Anna@Example.COM ", RAW_PASSWORD);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("anna@example.com");
    }

    @Test
    void shouldRejectAlreadyRegisteredEmail() {
        when(userRepository.existsByEmail("anna@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("Anna@example.com", RAW_PASSWORD))
            .isInstanceOf(EmailAlreadyRegisteredException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldProduceDifferentHashesForSamePassword() {
        String first = passwordEncoder.encode(RAW_PASSWORD);
        String second = passwordEncoder.encode(RAW_PASSWORD);

        assertThat(first).isNotEqualTo(second);
        assertThat(passwordEncoder.matches(RAW_PASSWORD, first)).isTrue();
        assertThat(passwordEncoder.matches(RAW_PASSWORD, second)).isTrue();
    }
}
