package com.example.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;
import java.util.List;
import java.util.NoSuchElementException;

@Service 
public class BoardService {
    public static final int PAGE_SIZE = 15;
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    @Transactional 
    public void save(String title, String content, String writer, String password) {
        Board board = new Board(title, content, writer, password);

        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public List<Board> findPage(int pageNumber) {
        if (pageNumber < 1 || pageNumber > Integer.MAX_VALUE / PAGE_SIZE + 1) {
            throw new IllegalArgumentException("페이지 번호가 조회 가능한 범위를 벗어났습니다.");
        }
        int offset = (pageNumber - 1) * PAGE_SIZE;
        return boardRepository.findPage(offset, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    public long count() {
        return boardRepository.count();
    }

    @Transactional(readOnly = true)
    public Board findById(Long id){
        return boardRepository.findById(id);
    }

    @Transactional
    public boolean delete(Long id, String password) {
        Board board = boardRepository.findById(id);
        if (board == null) {
            throw new NoSuchElementException("게시글이 없습니다.");
        }
        if (password == null || password.isBlank() || !password.equals(board.getPassword())) {
            return false;
        }
        boardRepository.delete(board);
        return true;
    }
}
