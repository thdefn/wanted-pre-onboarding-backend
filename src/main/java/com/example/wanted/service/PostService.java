package com.example.wanted.service;

import com.example.wanted.domain.model.Member;
import com.example.wanted.domain.model.Post;
import com.example.wanted.domain.repository.PostRepository;
import com.example.wanted.dto.PostDto;
import com.example.wanted.dto.PostForm;
import com.example.wanted.exception.ErrorCode;
import com.example.wanted.exception.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.example.wanted.domain.constants.Constants.PAGE_SIZE;
import static com.example.wanted.exception.ErrorCode.POST_NOT_FOUND;

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

    @Transactional
    public PostDto update(Long postId, PostForm form, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
        if (!Objects.equals(member.getId(), post.getWriter().getId()))
            throw new PostException(ErrorCode.MEMBER_NOT_WRITER);

        post.update(form);
        return PostDto.of(post, member.getId());
    }

    public Page<PostDto> read(int page) {
        return postRepository.findPostByOrderByIdDesc(PageRequest.of(page, PAGE_SIZE))
                .map(PostDto::of);
    }

    public PostDto detail(Long postId, Member member) {
        return PostDto.of(postRepository.findById(postId)
                        .orElseThrow(() -> new PostException(POST_NOT_FOUND)),
                member.getId());
    }

    @Transactional
    public void delete(Long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
        if (!Objects.equals(member.getId(), post.getWriter().getId()))
            throw new PostException(ErrorCode.MEMBER_NOT_WRITER);

        postRepository.delete(post);
    }
}
