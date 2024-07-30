package dihoon.bulletinboardback.dto;

import dihoon.bulletinboardback.utils.HtmlUtils;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "PostDtoBuilder")
public class PostDto {
    private long postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long userId;
    private String userEmail;

    public void setTitle(String title) {
        this.title = HtmlUtils.encodeHtml(title);
    }

    public void setContent(String content) {
        this.content = HtmlUtils.encodeHtml(content);
    }

    public String getTitle() {
        return HtmlUtils.decodeHtml(title);
    }

    public String getContent() {
        return HtmlUtils.decodeHtml(content);
    }

    public static class PostDtoBuilder {
        private String title;
        private String content;

        public PostDtoBuilder title(String title) {
            this.title = HtmlUtils.encodeHtml(title);
            return this;
        }

        public PostDtoBuilder content(String content) {
            this.content = HtmlUtils.encodeHtml(content);
            return this;
        }
    }
}