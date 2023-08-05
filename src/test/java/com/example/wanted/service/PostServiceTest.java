package com.example.wanted.service;

import com.example.wanted.domain.constants.MemberType;
import com.example.wanted.domain.model.Member;
import com.example.wanted.domain.model.Post;
import com.example.wanted.domain.repository.PostRepository;
import com.example.wanted.dto.PostDto;
import com.example.wanted.dto.PostForm;
import com.example.wanted.exception.ErrorCode;
import com.example.wanted.exception.PostException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    Member member = Member.builder()
            .id(1L)
            .email("abcde@gmail.com")
            .password("12345678")
            .type(MemberType.USER)
            .build();

    @Test
    void 게시물_생성_성공() {
        //given
        PostForm form = PostForm.builder()
                .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                .build();
        given(postRepository.save(any()))
                .willReturn(Post.builder()
                        .id(1L)
                        .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                        .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                        .writer(member)
                        .build());
        //when
        PostDto dto = postService.create(form, member);
        //then
        verify(postRepository, times(1)).save(any());
        assertEquals(1L, dto.getPostId());
        assertEquals(form.getTitle(), dto.getTitle());
        assertEquals(form.getContent(), dto.getContent());
        assertEquals(1L, dto.getWriterId());
        assertTrue(dto.getIsReadersPost());
    }

    @Test
    void 게시물_수정_성공() {
        //given
        PostForm form = PostForm.builder()
                .title("논란 이어지는 잼버리, 종교계가 구원 나섰다")
                .content("제대로 준비되지 않은 행사 진행으로 여러 논란을 낳고 있는 세계스카우트잼버리 대회를 위해 종교계가 나섰다.")
                .build();
        given(postRepository.findById(any()))
                .willReturn(Optional.of(Post.builder()
                        .id(1L)
                        .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                        .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                        .writer(member)
                        .build()));
        //when
        PostDto dto = postService.update(1L, form, member);
        //then
        assertEquals(1L, dto.getPostId());
        assertEquals(form.getTitle(), dto.getTitle());
        assertEquals(form.getContent(), dto.getContent());
        assertEquals(1L, dto.getWriterId());
        assertTrue(dto.getIsReadersPost());
    }

    @Test
    void 게시물_수정_실패_게시물이_존재하지_않음() {
        //given
        PostForm form = PostForm.builder()
                .title("논란 이어지는 잼버리, 종교계가 구원 나섰다")
                .content("제대로 준비되지 않은 행사 진행으로 여러 논란을 낳고 있는 세계스카우트잼버리 대회를 위해 종교계가 나섰다.")
                .build();
        given(postRepository.findById(any())).willReturn(Optional.empty());
        //when
        PostException exception = assertThrows(PostException.class,
                () -> postService.update(1L, form, member));
        //then
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 게시물_수정_실패_작성자가_아님() {
        //given
        Member writer = Member.builder()
                .id(2L)
                .build();
        PostForm form = PostForm.builder()
                .title("논란 이어지는 잼버리, 종교계가 구원 나섰다")
                .content("제대로 준비되지 않은 행사 진행으로 여러 논란을 낳고 있는 세계스카우트잼버리 대회를 위해 종교계가 나섰다.")
                .build();
        given(postRepository.findById(any()))
                .willReturn(Optional.of(Post.builder()
                        .id(1L)
                        .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                        .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                        .writer(writer)
                        .build()));
        //when
        PostException exception = assertThrows(PostException.class,
                () -> postService.update(1L, form, member));
        //then
        assertEquals(ErrorCode.MEMBER_NOT_WRITER, exception.getErrorCode());
    }

}