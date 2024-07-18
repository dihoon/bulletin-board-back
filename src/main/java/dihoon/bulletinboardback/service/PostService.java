package dihoon.bulletinboardback.service;

import dihoon.bulletinboardback.domain.Post;
import dihoon.bulletinboardback.domain.User;
import dihoon.bulletinboardback.dto.AddPostRequest;
import dihoon.bulletinboardback.exception.PostNotFoundException;
import dihoon.bulletinboardback.repository.PostRepository;
import dihoon.bulletinboardback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post createPost(AddPostRequest request) {
        String title = request.getTitle();
        String content = request.getContent();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        if (title == null || title == "") throw new RuntimeException("Title is missing");

        User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(()-> new RuntimeException("User not found"));

        Post post = Post.builder().title(title).content(content).user(user).build();

        postRepository.save(post);

        return post;
    }

    @Transactional(readOnly = true)
    public Post getPostById(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new PostNotFoundException("Post not found"));
        return post;
    }
}
