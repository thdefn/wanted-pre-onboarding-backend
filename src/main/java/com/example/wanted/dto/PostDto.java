package com.example.wanted.dto;

import com.example.wanted.domain.model.Post;
import com.example.wanted.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private Long postId;
    private String title;
    private String content;
    @Builder.Default
    private String nickName = "익명";
    private Long writerId;
    private Boolean isReadersPost;
    private String createdAt;

    public static PostDto of(Post post, Long readerId) {
        return PostDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writerId(post.getWriter().getId())
                .isReadersPost(post.getWriter().isReader(readerId))
                .createdAt(TimeUtil.calculateTerm(post.getCreatedAt()))
                .build();
    }

    public static PostDto of(Post post) {
        return PostDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent().length() > 150 ? post.getContent().substring(0, 147) + "..." : post.getContent())
                .writerId(post.getWriter().getId())
                .createdAt(TimeUtil.calculateTerm(post.getCreatedAt()))
                .build();
    }
}
