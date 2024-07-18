package dihoon.bulletinboardback.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class AddPostRequest {
    private final String title;
    private final String content;
}
