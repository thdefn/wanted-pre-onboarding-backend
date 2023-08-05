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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.wanted.exception.ErrorCode.POST_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
                        .createdAt(LocalDateTime.now())
                        .build());
        //when
        PostDto dto = postService.create(form, member);
        //then
        verify(postRepository, times(1)).save(any());
        assertEquals(1L, dto.getPostId());
        assertEquals(form.getTitle(), dto.getTitle());
        assertEquals(form.getContent(), dto.getContent());
        assertTrue(dto.getCreatedAt().contains("초 전"));
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
                        .createdAt(LocalDateTime.now())
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
        assertEquals(POST_NOT_FOUND, exception.getErrorCode());
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

    @Test
    void 게시물_리스트_조회_성공() {
        //given
        Member writer = Member.builder()
                .id(2L)
                .build();
        Page<Post> pages = new PageImpl<>(
                List.of(Post.builder()
                                .id(3L)
                                .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                                .content("금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는")
                                .createdAt(LocalDateTime.now())
                                .writer(writer)
                                .build(),
                        Post.builder()
                                .id(2L)
                                .title("논란 이어지는 잼버리, 종교계가 구원 나섰다")
                                .content("제대로 준비되지 않은 행사 진행으로 여러 논란을 낳고 있는 세계스카우트잼버리 대회를 위해 종교계가 나섰다.")
                                .createdAt(LocalDateTime.now())
                                .writer(writer)
                                .build(),
                        Post.builder()
                                .id(1L)
                                .title("극초음속 미사일 장착 시작하는 美 줌월트급 구축함 [최현호의 무기인사이드]")
                                .content("지난 1일(현지시각), 미 해군의 스텔스 구축함 USS 줌왈트가 극초음속 미사일 발사 장비 장착을 위해 샌디에이고를 출발해 미시시피주 파스카굴라로 출발했다.")
                                .createdAt(LocalDateTime.now())
                                .writer(writer)
                                .build()));
        given(postRepository.findPostByOrderByIdDesc(any())).willReturn(pages);
        //when
        Page<PostDto> dtos = postService.read(0);
        //then
        assertEquals(3L, dtos.getContent().get(0).getPostId());
        assertEquals(150, dtos.getContent().get(0).getContent().length());
        assertTrue(dtos.getContent().get(0).getCreatedAt().contains("초 전"));
        assertEquals(2L, dtos.getContent().get(0).getWriterId());
    }

    @Test
    void 게시물_상세_조회_성공_조회자가_게시물_작성자임() {
        //given
        Member writer = Member.builder()
                .id(2L)
                .build();
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(Post.builder()
                        .id(3L)
                        .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                        .content("금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는")
                        .createdAt(LocalDateTime.now())
                        .writer(writer)
                        .build()));
        //when
        PostDto dto = postService.detail(3L, writer);
        //then
        assertEquals(3L, dto.getPostId());
        assertNotEquals(150, dto.getContent().length());
        assertTrue(dto.getCreatedAt().contains("초 전"));
        assertEquals(2L, dto.getWriterId());
        assertTrue(dto.getIsReadersPost());
    }

    @Test
    void 게시물_상세_조회_성공_조회자가_게시물_작성자가_아님() {
        //given
        Member writer = Member.builder()
                .id(2L)
                .build();
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(Post.builder()
                        .id(3L)
                        .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                        .content("금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는")
                        .createdAt(LocalDateTime.now())
                        .writer(writer)
                        .build()));
        //when
        PostDto dto = postService.detail(3L, member);
        //then
        assertEquals(3L, dto.getPostId());
        assertNotEquals(150, dto.getContent().length());
        assertTrue(dto.getCreatedAt().contains("초 전"));
        assertEquals(2L, dto.getWriterId());
        assertFalse(dto.getIsReadersPost());
    }

    @Test
    void 게시물_상세_조회_실패() {
        //given
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when
        PostException exception = assertThrows(PostException.class,
                () -> postService.detail(3L, member));
        //then
        assertEquals(POST_NOT_FOUND, exception.getErrorCode());
    }

}