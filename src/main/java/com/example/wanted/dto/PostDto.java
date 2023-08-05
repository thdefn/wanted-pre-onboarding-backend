package com.example.wanted.dto;

import com.example.wanted.domain.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long postId;
    private String title;
    private String content;
    @Builder.Default
    private String nickName = "익명";
    private Long writerId;
    private Boolean isReadersPost;

    public static PostDto of(Post post, Long readerId) {
        return PostDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writerId(post.getWriter().getId())
                .isReadersPost(post.getWriter().isReader(readerId))
                .build();
    }
}
