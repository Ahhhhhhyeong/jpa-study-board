package com.example.board.controller;

import com.example.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@Controller 
public class BoardController {
    
    private final BoardService boardService;

    BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // call board
    @GetMapping("/board")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    


    // save board
    @PostMapping("/board/write")
    public String write(@RequestParam String title, @RequestParam  String content) {
        boardService.save(title, content);

        return "redirect:/board/list";
    }
    
}
