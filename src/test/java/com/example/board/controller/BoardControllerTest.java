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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import com.example.board.entity.Board;
import java.util.List;
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
    void viewDisplaysRequestedBoard() throws Exception {
        Board board = new Board("제목", "내용", "작성자", "test-password");
        when(boardService.findById(1L)).thenReturn(board);
        mockMvc.perform(get("/board/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/view"))
                .andExpect(model().attribute("board", board));
    }

    @Test
    void viewReturnsNotFoundForMissingBoard() throws Exception {
        mockMvc.perform(get("/board/view/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listDefaultsToFirstPage() throws Exception {
        when(boardService.count()).thenReturn(0L);
        when(boardService.findPage(1)).thenReturn(List.of());
        mockMvc.perform(get("/board/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list"))
                .andExpect(model().attribute("pageNumber", 1))
                .andExpect(model().attribute("totalPages", 0L))
                .andExpect(model().attribute("boardList", List.of()));
        verify(boardService).findPage(1);
    }

    @Test
    void listPassesRequestedPageAndCalculatesTotalPages() throws Exception {
        List<Board> boards = List.of(new Board("제목", "내용", "작성자", "test-password"));
        when(boardService.count()).thenReturn(31L);
        when(boardService.findPage(2)).thenReturn(boards);
        mockMvc.perform(get("/board/list").param("pageNumber", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageNumber", 2))
                .andExpect(model().attribute("totalPages", 3L))
                .andExpect(model().attribute("boardList", boards));
        verify(boardService).findPage(2);
    }

    @Test
    void listRejectsInvalidPage() throws Exception {
        for (String page : List.of("0", "-1", "2147483647", "abc")) {
            mockMvc.perform(get("/board/list").param("pageNumber", page))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(boardService);
    }

    @Test
    @DisplayName("게시글 작성 요청은 저장 서비스를 호출하고 목록으로 이동한다")
    void writeBoard() throws Exception {
        mockMvc.perform(post("/board/write")
                        .param("title", "첫 번째 게시글")
                        .param("content", "JPA 공부 중입니다.")
                        .param("writer", "작성자")
                        .param("password", "test-password"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/board/list"));

        verify(boardService).save("첫 번째 게시글", "JPA 공부 중입니다.", "작성자", "test-password");
    }

    @Test
    @DisplayName("제목이 누락되면 400 응답을 반환하고 저장하지 않는다")
    void writeWithoutTitle() throws Exception {
        mockMvc.perform(post("/board/write")
                        .param("content", "내용만 전달합니다.")
                        .param("writer", "작성자")
                        .param("password", "test-password"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(boardService);
    }

    @Test
    @DisplayName("내용이 누락되면 400 응답을 반환하고 저장하지 않는다")
    void writeWithoutContent() throws Exception {
        mockMvc.perform(post("/board/write")
                        .param("title", "제목만 전달합니다.")
                        .param("writer", "작성자")
                        .param("password", "test-password"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(boardService);
    }
}
