package com.example.wanted.service;

import com.example.wanted.domain.model.Member;
import com.example.wanted.domain.model.Post;
import com.example.wanted.domain.repository.PostRepository;
import com.example.wanted.dto.PostDto;
import com.example.wanted.dto.PostForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public PostDto create(PostForm form, Member member) {
        return PostDto.of(postRepository.save(Post.builder()
                .writer(member)
                .title(form.getTitle())
                .content(form.getContent()).build()), member.getId());
    }
}
