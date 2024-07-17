package dihoon.bulletinboardback.controller;

import dihoon.bulletinboardback.api.UserApi;
import dihoon.bulletinboardback.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "User API 입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController implements UserApi {
    private final UserService userService;
}
