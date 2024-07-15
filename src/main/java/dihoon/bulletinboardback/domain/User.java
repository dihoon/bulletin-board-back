package dihoon.bulletinboardback.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private long id;

    @Column(name= "email", unique = true, nullable = false)
    private String email;

    @Column(name= "password", unique = false, nullable = true)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "refreshToken")
    private String refreshToken;

    @Builder
    public User(String email, String password, String role, String refreshToken) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.refreshToken = refreshToken;
    }
}