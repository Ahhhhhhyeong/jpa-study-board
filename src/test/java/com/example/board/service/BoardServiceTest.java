package com.example.board.service;

import com.example.board.repository.BoardRepository;
import com.example.board.entity.Board;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class BoardServiceTest {
    private final BoardRepository repository = mock(BoardRepository.class);
    private final BoardService service = new BoardService(repository);

    @Test
    void deletesOnlyWhenPasswordMatches() {
        Board board = new Board("제목", "내용", "작성자", "secret");
        when(repository.findById(1L)).thenReturn(board);

        assertTrue(service.delete(1L, "secret"));
        verify(repository).delete(board);
    }

    @Test
    void rejectsIncorrectOrEmptyPasswordsWithoutDeleting() {
        when(repository.findById(1L)).thenReturn(new Board("제목", "내용", "작성자", "secret"));

        assertFalse(service.delete(1L, "wrong"));
        assertFalse(service.delete(1L, ""));
        assertFalse(service.delete(1L, "   "));
        assertFalse(service.delete(1L, null));
        verify(repository, never()).delete(any());
    }

    @Test
    void doesNotDeleteBoardsWithoutStoredPassword() {
        when(repository.findById(1L)).thenReturn(new Board("제목", "내용", "작성자", null));
        assertFalse(service.delete(1L, "secret"));
        verify(repository, never()).delete(any());
    }

    @Test
    void missingBoardCannotBeDeleted() {
        assertThrows(NoSuchElementException.class, () -> service.delete(999L, "secret"));
        verify(repository, never()).delete(any());
    }

    @Test
    void firstPageStartsAtZero() {
        service.findPage(1);
        verify(repository).findPage(0, 15);
    }

    @Test
    void thirdPageSkipsThirtyBoards() {
        service.findPage(3);
        verify(repository).findPage(30, 15);
    }

    @Test
    void rejectsInvalidPageNumbers() {
        assertThrows(IllegalArgumentException.class, () -> service.findPage(0));
        assertThrows(IllegalArgumentException.class, () -> service.findPage(-1));
        assertThrows(IllegalArgumentException.class, () -> service.findPage(Integer.MAX_VALUE));
        verifyNoInteractions(repository);
    }
}
