package dihoon.bulletinboardback.service;

import dihoon.bulletinboardback.domain.Post;
import dihoon.bulletinboardback.domain.User;
import dihoon.bulletinboardback.dto.AddPostRequest;
import dihoon.bulletinboardback.repository.PostRepository;
import dihoon.bulletinboardback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post createPost(AddPostRequest request) {
        String title = request.getTitle();
        String content = request.getContent();
        long userId = request.getUserId();

        if (title == null || title == "") throw new RuntimeException("Title is missing");

        User user = userRepository.findById(userId)
                        .orElseThrow(()-> new RuntimeException("User not found"));

        Post post = Post.builder().title(title).content(content).user(user).build();

        postRepository.save(post);

        return post;
    }
}
