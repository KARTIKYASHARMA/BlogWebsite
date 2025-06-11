package net.sample.wordpress.dto;

import lombok.Data;

@Data
public class BlogDTO {
    private Long blogId;
    private String title;
    private String content;
    private Long userId;
}
