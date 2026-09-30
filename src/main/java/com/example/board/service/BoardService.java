package com.example.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;

@Service 
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    @Transactional 
    public void save(String title, String content) {
        Board board = new Board(title, content);

        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public Board findById(Long id){
        return boardRepository.findById(id);
    }
}
