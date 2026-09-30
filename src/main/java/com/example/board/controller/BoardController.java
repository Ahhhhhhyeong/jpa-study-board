package com.example.board.controller;

import com.example.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller 
public class BoardController {
    
    private final BoardService boardService;

    BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // save board
    @PostMapping("/board/write")
    public String write(@RequestParam String title, @RequestParam  String content) {
        boardService.save(title, content);

        return "redirect:/board/list";
    }
    
}
