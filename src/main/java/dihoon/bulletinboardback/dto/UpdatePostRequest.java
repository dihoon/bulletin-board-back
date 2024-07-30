package dihoon.bulletinboardback.dto;

import dihoon.bulletinboardback.utils.HtmlUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePostRequest {
    private String title;
    private String content;

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
}
