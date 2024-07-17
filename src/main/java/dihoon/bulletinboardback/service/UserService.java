package dihoon.bulletinboardback.service;

import dihoon.bulletinboardback.constant.Role;
import dihoon.bulletinboardback.domain.User;
import dihoon.bulletinboardback.dto.AddUserRequest;
import dihoon.bulletinboardback.exception.InvalidEmailException;
import dihoon.bulletinboardback.exception.UserAlreadyExistsException;
import dihoon.bulletinboardback.repository.UserRepository;
import dihoon.bulletinboardback.utils.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
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

        return userRepository.save(User.builder()
                .email(email)
                .password(new BCryptPasswordEncoder().encode(password))
                .role(role)
                .build()).getId();
    }

    public void updateRefreshToken(String email, String refreshToken) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }
}
