package dihoon.bulletinboardback.service;

import dihoon.bulletinboardback.domain.Post;
import dihoon.bulletinboardback.domain.User;
import dihoon.bulletinboardback.dto.AddPostRequest;
import dihoon.bulletinboardback.dto.UpdatePostRequest;
import dihoon.bulletinboardback.exception.PostNotFoundException;
import dihoon.bulletinboardback.exception.UnauthorizedAccessException;
import dihoon.bulletinboardback.repository.PostRepository;
import dihoon.bulletinboardback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(int page, int size, String sortString) {

        List<Sort.Order> orders = new ArrayList<>();

        String[] sortArray = sortString.split(",");
        for (String s : sortArray) {
            String[] sortParts = s.split(":");
            String field = sortParts[0];
            String order = sortParts.length > 1 ? sortParts[1] : "asc";
            orders.add(new Sort.Order(Sort.Direction.fromString(order), field));
        }

        Sort sort = Sort.by(orders);

        Pageable pageable = PageRequest.of(page, size, sort);

        return postRepository.findAll(pageable);
    }

    private List<Sort.Order> parseSort(String[] sort) {
        return Arrays.stream(sort)
                .map(sortParam -> {
                    String[] parts = sortParam.split(",");
                    if (parts.length == 2 && parts[1].equalsIgnoreCase("desc")) {
                        return Sort.Order.desc(parts[0]);
                    } else if (parts.length == 2 && parts[1].equalsIgnoreCase("asc")) {
                        return Sort.Order.asc(parts[0]);
                    } else if (parts.length == 1) {
                        return Sort.Order.asc(parts[0]);
                    }
                    return null;
                })
                .filter(order -> order != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public Post updatePost(long postId, UpdatePostRequest updateData) {
        if (!isAuthorizedToAccessPost(postId)) throw new UnauthorizedAccessException("User not authorized to update this post");

        Post post = getPostById(postId);

        post.setContent(updateData.getContent());
        postRepository.save(post);
        return post;
    }

    @Transactional
    public long deletePost(long postId) {
        if (!isAuthorizedToAccessPost(postId)) throw new UnauthorizedAccessException("User not authorized to delete this post");

        if (!postRepository.existsById(postId)) throw new PostNotFoundException("Post not found");

        postRepository.deleteById(postId);

        return postId;
    }

    private boolean isAuthorizedToAccessPost(long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));

        Post post = getPostById(postId);

        return user.getUserId() == post.getUser().getUserId();
    }

}
