package dihoon.bulletinboardback.service;

import dihoon.bulletinboardback.constant.Role;
import dihoon.bulletinboardback.domain.User;
import dihoon.bulletinboardback.dto.AddUserRequest;
import dihoon.bulletinboardback.dto.UserDto;
import dihoon.bulletinboardback.exception.InvalidEmailException;
import dihoon.bulletinboardback.exception.UserAlreadyExistsException;
import dihoon.bulletinboardback.repository.UserRepository;
import dihoon.bulletinboardback.utils.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection<SimpleGrantedAuthority> authority = new ArrayList<>();
        authority.add(new SimpleGrantedAuthority(user.getRole()));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authority);
    }

    public long save(AddUserRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String role = request.getRole() == null ? Role.USER.name() : request.getRole();

        if (!EmailValidator.isValidEmail(email)) {
            throw new InvalidEmailException("Invalid email");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = userRepository.save(User.builder()
                .email(email)
                .password(new BCryptPasswordEncoder().encode(password))
                .role(role)
                .build());

        return user.getUserId();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public void deleteByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        long userId = user.getUserId();

        userRepository.deleteById(userId);
    }

    public void updateRefreshToken(String email, String refreshToken) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getEmail())
                .build();
    }
}
