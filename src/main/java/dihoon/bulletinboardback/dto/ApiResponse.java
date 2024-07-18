package dihoon.bulletinboardback.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final String message;
    private final T data;
}
