package com.example.board.service;

import com.example.board.repository.BoardRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class BoardServiceTest {
    private final BoardRepository repository = mock(BoardRepository.class);
    private final BoardService service = new BoardService(repository);

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
