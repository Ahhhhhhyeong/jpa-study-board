package com.example.board.controller;

import com.example.board.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BoardService boardService;

    @Test
    @DisplayName("게시글 작성 요청은 저장 서비스를 호출하고 목록으로 이동한다")
    void writeBoard() throws Exception {
        mockMvc.perform(post("/board/write")
                        .param("title", "첫 번째 게시글")
                        .param("content", "JPA 공부 중입니다."))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/board/list"));

        verify(boardService).save("첫 번째 게시글", "JPA 공부 중입니다.");
    }

    @Test
    @DisplayName("제목이 누락되면 400 응답을 반환하고 저장하지 않는다")
    void writeWithoutTitle() throws Exception {
        mockMvc.perform(post("/board/write")
                        .param("content", "내용만 전달합니다."))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(boardService);
    }

    @Test
    @DisplayName("내용이 누락되면 400 응답을 반환하고 저장하지 않는다")
    void writeWithoutContent() throws Exception {
        mockMvc.perform(post("/board/write")
                        .param("title", "제목만 전달합니다."))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(boardService);
    }
}
