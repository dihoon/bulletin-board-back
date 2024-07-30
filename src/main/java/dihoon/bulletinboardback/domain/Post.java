package dihoon.bulletinboardback.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long postId;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false,  updatable = false, scale = 0)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @JoinColumn(name = "user_id" ,nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private User user;

    @Builder
    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
