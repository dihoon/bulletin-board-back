package dihoon.bulletinboardback.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserInfoResponse {
    private final String email;
    private final String role;
}
