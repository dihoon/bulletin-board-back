package dihoon.bulletinboardback.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = false, nullable = true)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column()
    private String refreshToken;

    @Builder
    public User(String email, String password, String role, String refreshToken) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}