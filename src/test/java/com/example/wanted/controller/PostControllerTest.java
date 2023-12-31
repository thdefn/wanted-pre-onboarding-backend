package com.example.wanted.controller;

import com.example.wanted.domain.constants.MemberType;
import com.example.wanted.domain.model.Member;
import com.example.wanted.dto.PostDto;
import com.example.wanted.dto.PostForm;
import com.example.wanted.mockuser.WithMockCustomUser;
import com.example.wanted.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {
    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOKEN = "Bearer token";

    Member member = Member.builder()
            .id(1L)
            .email("abcde@gmail.com")
            .password("12345678")
            .type(MemberType.USER)
            .build();

    @Test
    @WithMockCustomUser
    void 게시물_생성_성공() throws Exception {
        //given
        PostForm form = PostForm.builder()
                .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                .build();
        given(postService.create(any(), any()))
                .willReturn(PostDto.builder()
                        .postId(1L)
                        .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                        .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                        .writerId(1L)
                        .createdAt("1분 전")
                        .isReadersPost(true)
                        .build());
        //when
        //then
        mockMvc.perform(post("/posts")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andExpect(jsonPath("$.writerId").value(1L))
                .andExpect(jsonPath("$.title").value(form.getTitle()))
                .andExpect(jsonPath("$.content").value(form.getContent()))
                .andExpect(jsonPath("$.isReadersPost").value(true))
                .andExpect(jsonPath("$.createdAt").value("1분 전"))
                .andExpect(jsonPath("$.nickName").value("익명"));
    }

    @Test
    @WithMockCustomUser
    void 게시물_생성_실패_1자에서_150자의_제목이어야_함() throws Exception {
        //given
        PostForm form = PostForm.builder()
                .title("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.\n" +
                        "\n" +
                        "5일 영화진흥위원회 통합권전산망에 따르면 ‘밀수’는 개봉 11일째인 이날 300만 관객을 돌파했다.\n" +
                        "\n" +
                        "‘밀수’는 2023년 개봉 한국 영화 중 ‘범죄도시3’ 이후 첫 번째로 300만을 넘겼다.\n" +
                        "\n" +
                        "지난달 26일 나온 밀수는 개봉 나흘 만에 100만 관객을, 일주일 만에 200만 관객을 각각 돌파하며 관객의 사랑을 받고 있다.\n" +
                        "\n" +
                        "300만 관객 돌파 소식과 함께 공개된 주역들의 300만 돌파 감사 인사도 화제다.\n" +
                        "\n" +
                        "‘밀수’는 류승완 감독의 신작으로, 1970년대 한 바닷가 도시를 배경으로 펼쳐지는 해녀들의 밀수 범죄를 다룬 영화다. 바다의 평범한 사람들 앞에 일생일대의 큰 판이 벌어지면서 휘말리는 해양 범죄 활극이다.\n")
                .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                .build();
        //when
        //then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser
    void 게시물_생성_실패_1자에서_1000자의_내용이어야_함() throws Exception {
        //given
        PostForm form = PostForm.builder()
                .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                .content(" ")
                .build();
        //when
        //then
        mockMvc.perform(post("/posts")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser
    void 게시물_수정_성공() throws Exception {
        //given
        PostForm form = PostForm.builder()
                .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                .build();
        given(postService.update(anyLong(), any(), any()))
                .willReturn(PostDto.builder()
                        .postId(1L)
                        .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                        .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                        .writerId(1L)
                        .isReadersPost(true)
                        .createdAt("1분 전")
                        .build());
        //when
        //then
        mockMvc.perform(put("/posts/{postId}", 1L)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andExpect(jsonPath("$.writerId").value(1L))
                .andExpect(jsonPath("$.title").value(form.getTitle()))
                .andExpect(jsonPath("$.content").value(form.getContent()))
                .andExpect(jsonPath("$.isReadersPost").value(true))
                .andExpect(jsonPath("$.createdAt").value("1분 전"))
                .andExpect(jsonPath("$.nickName").value("익명"));
    }

    @Test
    @WithMockCustomUser
    void 게시물_수정_실패_1자에서_150자의_제목이어야_함() throws Exception {
        //given
        PostForm form = PostForm.builder()
                .title("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.\n" +
                        "\n" +
                        "5일 영화진흥위원회 통합권전산망에 따르면 ‘밀수’는 개봉 11일째인 이날 300만 관객을 돌파했다.\n" +
                        "\n" +
                        "‘밀수’는 2023년 개봉 한국 영화 중 ‘범죄도시3’ 이후 첫 번째로 300만을 넘겼다.\n" +
                        "\n" +
                        "지난달 26일 나온 밀수는 개봉 나흘 만에 100만 관객을, 일주일 만에 200만 관객을 각각 돌파하며 관객의 사랑을 받고 있다.\n" +
                        "\n" +
                        "300만 관객 돌파 소식과 함께 공개된 주역들의 300만 돌파 감사 인사도 화제다.\n" +
                        "\n" +
                        "‘밀수’는 류승완 감독의 신작으로, 1970년대 한 바닷가 도시를 배경으로 펼쳐지는 해녀들의 밀수 범죄를 다룬 영화다. 바다의 평범한 사람들 앞에 일생일대의 큰 판이 벌어지면서 휘말리는 해양 범죄 활극이다.\n")
                .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                .build();
        //when
        //then
        mockMvc.perform(put("/posts/{postId}", 1L)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser
    void 게시물_수정_실패_1자에서_1000자의_내용이어야_함() throws Exception {
        //given
        PostForm form = PostForm.builder()
                .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                .content(" ")
                .build();
        //when
        //then
        mockMvc.perform(put("/posts/{postId}", 1L)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockCustomUser
    void 게시물_리스트_조회_성공() throws Exception {
        //given
        Page<PostDto> dto = new PageImpl<>(
                List.of(PostDto.builder()
                                .postId(3L)
                                .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                                .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                                .writerId(1L)
                                .nickName("익명")
                                .createdAt("2분 전")
                                .build(),
                        PostDto.builder()
                                .postId(2L)
                                .title("논란 이어지는 잼버리, 종교계가 구원 나섰다")
                                .content("제대로 준비되지 않은 행사 진행으로 여러 논란을 낳고 있는 세계스카우트잼버리 대회를 위해 종교계가 나섰다.")
                                .writerId(1L)
                                .nickName("익명")
                                .createdAt("1일 전")
                                .build(),
                        PostDto.builder()
                                .postId(1L)
                                .title("극초음속 미사일 장착 시작하는 美 줌월트급 구축함 [최현호의 무기인사이드]")
                                .content("지난 1일(현지시각), 미 해군의 스텔스 구축함 USS 줌왈트가 극초음속 미사일 발사 장비 장착을 위해 샌디에이고를 출발해 미시시피주 파스카굴라로 출발했다.")
                                .writerId(2L)
                                .nickName("익명")
                                .createdAt("1일 전")
                                .build()));
        given(postService.read(anyInt())).willReturn(dto);
        //when
        //then
        mockMvc.perform(get("/posts")
                        .param("page", "0")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].postId").value(3L))
                .andExpect(jsonPath("$.content.[0].writerId").value(1L))
                .andExpect(jsonPath("$.content.[0].title").value("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파"))
                .andExpect(jsonPath("$.content.[0].content").value("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다."))
                .andExpect(jsonPath("$.content.[0].nickName").value("익명"))
                .andExpect(jsonPath("$.content.[0].createdAt").value("2분 전"));
    }

    @Test
    @WithMockCustomUser
    void 게시물_상세_조회_성공() throws Exception {
        //given
        given(postService.detail(anyLong(), any()))
                .willReturn(PostDto.builder()
                        .postId(1L)
                        .title("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파")
                        .content("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다.")
                        .writerId(1L)
                        .nickName("익명")
                        .isReadersPost(true)
                        .createdAt("1분 전")
                        .build());
        //when
        //then
        mockMvc.perform(get("/posts/{postId}", 1L)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andExpect(jsonPath("$.writerId").value(1L))
                .andExpect(jsonPath("$.title").value("류승완 신작 ‘밀수’, 개봉 11일 만에 300만 돌파"))
                .andExpect(jsonPath("$.content").value("무더위기 기승을 부리는 올여름 시원한 바다가 배경인 영화 ‘밀수’가 300만 관객을 돌파했다."))
                .andExpect(jsonPath("$.isReadersPost").value(true))
                .andExpect(jsonPath("$.createdAt").value("1분 전"))
                .andExpect(jsonPath("$.nickName").value("익명"));
    }

    @Test
    @WithMockCustomUser
    void 게시물_삭제_성공() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(delete("/posts/{postId}", 1L)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}