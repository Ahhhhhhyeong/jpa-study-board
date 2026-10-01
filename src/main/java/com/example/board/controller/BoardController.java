package com.example.board.controller;

import com.example.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.board.entity.Board;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;



@Controller 
public class BoardController {
    
    private final BoardService boardService;

    BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    


    @GetMapping("/board/write")
    public String writeForm() {
        return "board/write";
    }

    @GetMapping({"/board", "/board/list"})
    public String list(@RequestParam(defaultValue = "1") int pageNumber, Model model) {
        if (pageNumber < 1 || pageNumber > Integer.MAX_VALUE / BoardService.PAGE_SIZE + 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 페이지 번호를 입력하세요.");
        }
        long totalCount = boardService.count();
        long totalPages = totalCount / BoardService.PAGE_SIZE
                + (totalCount % BoardService.PAGE_SIZE == 0 ? 0 : 1);
        model.addAttribute("boardList", boardService.findPage(pageNumber));
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", totalPages);
        return "board/list";
    }

    // save board
    @GetMapping("/board/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Board board = boardService.findById(id);
        if (board == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다.");
        }
        model.addAttribute("board", board);
        return "board/view";
    }

    // save board
    @PostMapping("/board/write")
    public String write(@RequestParam String title, @RequestParam String content,
                        @RequestParam String writer, @RequestParam String password) {
        boardService.save(title, content, writer, password);

        return "redirect:/board/list";
    }
    
}
