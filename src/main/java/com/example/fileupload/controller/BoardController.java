package com.example.fileupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.fileupload.dto.BoardForm;
import com.example.fileupload.service.BoardService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoardController {
	@Autowired
	BoardService boardService;
	
	@GetMapping("/boardOne")
	public String boardOne(Model model, int boardNo) {
		model.addAttribute("board", boardService.getBoardOne(boardNo));
		model.addAttribute("boardfileList", boardService.getBoardfileList(boardNo));
		
		return "boardOne";
	}
	
	@GetMapping("/boardList")
	public String boardList(Model model) {
		model.addAttribute("boardList", boardService.getBoardList());
		return "boardList";
	}
	
	// 입력 액션
	@PostMapping("/addBoard")
	public String addBoard(HttpSession session, BoardForm bf) {
		log.debug("title: " + bf.getTitle());
		log.debug("boardfile.size: " + bf.getBoardfile().size());
		// bf.getBoardfile() -> List<MultipartFile>
		// bf.getBoardfile().get(n) -> MultipartFile
		log.debug("boardfile[0]: " + bf.getBoardfile().get(0).getSize());
		// 파일을 업로드 했는지 안했는지는 첫번째 파일의 사이즈, 이름, ..., 속성으로 확인이 필요하다.
		
		// 업로드 위치
		String path = session.getServletContext().getRealPath("/upload/");
		boardService.addBoard(bf, path);
		
		return "redirect:/ ";		
	}
	
	// 입력 폼
	@GetMapping("/addBoard")
	public String addBoard() {
		return "addBoard";
	}
}
