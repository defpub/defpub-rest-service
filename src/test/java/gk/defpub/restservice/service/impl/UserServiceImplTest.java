package gk.defpub.restservice.service.impl;

import gk.defpub.restservice.model.LoginUser;
import gk.defpub.restservice.model.Role;
import gk.defpub.restservice.model.User;
import gk.defpub.restservice.model.UserConfig;
import gk.defpub.restservice.repository.UserConfigRepository;
import gk.defpub.restservice.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * UserServiceImplTest class.
 * <p>
 * Date: Nov 12, 2018
 * <p>
 *
 * @author Gleb Kosteiko
 */
public class UserServiceImplTest {
    private static final String TEST_USER_ID = "testId";
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_ENCRYPTED_PASSWORD = "testEncPass";
    private static final String TEST_USERNAME_2 = "testUser2";
    private static final String TEST_PASSWORD = "test password";
    private User user;

    @Mock
    private BCryptPasswordEncoder bcryptEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserConfigRepository userConfigRepository;
    @InjectMocks
    private UserServiceImpl userService = new UserServiceImpl();

    @Before
    public void setUp() {
        initMocks(this);
        user = new User();
        user.setUsername(TEST_USERNAME);
        user.setEncryptedPassword(TEST_ENCRYPTED_PASSWORD);
        user.setRole(Role.ADMIN);
    }

    @Test
    public void testFindOne() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        User foundUser = userService.findOne(TEST_USERNAME);
        assertThat(foundUser.getUsername()).isEqualTo(TEST_USERNAME);
        verify(userRepository).findByUsername(TEST_USERNAME);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testSave() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername(TEST_USERNAME_2);
        loginUser.setPassword(TEST_PASSWORD);
        User user2 = new User();
        user2.setUsername(TEST_USERNAME_2);
        user2.setRole(Role.USER);
        when(userRepository.save(any(User.class))).thenReturn(user2);
        when(bcryptEncoder.encode(eq(TEST_PASSWORD))).thenCallRealMethod();

        User savedUser = userService.save(loginUser);
        assertThat(savedUser.getUsername()).isEqualTo(TEST_USERNAME_2);
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(any(User.class));
        verify(userConfigRepository).save(any(UserConfig.class));
        verifyNoMoreInteractions(userRepository, userConfigRepository);
    }

    @Test
    public void testFindAll() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.findAll();
        assertThat(allUsers.size()).isEqualTo(1);
        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testFindById() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(TEST_USER_ID);
        assertThat(foundUser).isNotNull();
        verify(userRepository).findById(TEST_USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testDelete() {
        userService.delete(TEST_USER_ID);
        verify(userRepository).deleteById(TEST_USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testLoadUserByUsername() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername(TEST_USERNAME);
        assertThat(userDetails).isNotNull();

        verify(userRepository).findByUsername(TEST_USERNAME);
        verifyNoMoreInteractions(userRepository);
    }
}
