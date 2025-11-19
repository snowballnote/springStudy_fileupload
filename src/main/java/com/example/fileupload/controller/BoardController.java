package com.example.fileupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.fileupload.dto.Board;
import com.example.fileupload.dto.BoardForm;
import com.example.fileupload.service.BoardService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoardController {
	@Autowired
	BoardService boardService;
	// 게시글 삭제 액션
	@PostMapping("/removeBoard")
	public String removeBoard(HttpSession session, int boardNo) {
		log.info("게시글 삭제 요청: BoardNo={}", boardNo);
			
		try {
			String path = session.getServletContext().getRealPath("/upload/");
				
			// Service 호출(삭제 처리)
			boardService.removeBoard(boardNo, path);
				
			// 삭제 성공 시 해당 게시글 상세 페이지로 리다이렉트
			return "redirect:/boardList";
				
		} catch (Exception e) {
			log.error("파일 삭제 처리 중 오류 발생:  BoardNo={}", boardNo, e);
			// 오류 발생 시 목록 페이지로 리다이렉트 (실패 시 상세 페이지로 돌아가기 어려움)
			return "redirect:/boardOne?boardNo=" + boardNo;
		}
	}
	
	// 게시글 제목 수정 폼
	// 입력 폼
	@GetMapping("/updateBoard")
	public String updateBoard(Model model, int boardNo) {
		// boardNo를 사용하여 기존 게시글 정보 조회
	    // (boardService.getBoardOne(int boardNo) 메서드가 존재한다고 가정)
	    Board existingBoard = boardService.getBoardOne(boardNo);
	    
	    // 조회된 정보를 모델에 담아 뷰로 전달
	    model.addAttribute("board", existingBoard);
		
		return "updateBoard";
	}
	
	@PostMapping("/updateBoard")
	public String updateBoard(Board b) {
		try{
			int row = boardService.updateBoard(b);
			
			if (row == 1) {
	            log.info("게시글 수정 성공: boardNo={}", b.getBoardNo());
	        } else {
	            log.warn("게시글 수정 실패 (영향 받은 행 없음): boardNo={}", b.getBoardNo());

	        }
			
	        return "redirect:/boardOne?boardNo=" + b.getBoardNo();
		}catch(Exception e) {
			log.error("게시글 수정 중 오류 발생: boardNo={}", b.getBoardNo(), e);

	        return "redirect:/boardList";
		}
	}
	
	// 파일 삭제 액션
	@PostMapping("/removeFile")
	public String removeFile(HttpSession session, int fileNo) {
		log.info("파일 삭제 요청: fileNo={}", fileNo);
		
		try {
			String path = session.getServletContext().getRealPath("/upload/");
			
			// Service 메서드를 한 번만 호출하고, 반환 값(boardNo)을 저장
			int boardNo = boardService.removeFile(fileNo, path);
			
			// 삭제 성공 시 해당 게시글 상세 페이지로 리다이렉트
			return "redirect:/boardOne?boardNo=" + boardNo;
			
		} catch (Exception e) {
			log.error("파일 삭제 처리 중 오류 발생: fileNo={}", fileNo, e);
			// 오류 발생 시 목록 페이지로 리다이렉트 (실패 시 상세 페이지로 돌아가기 어려움)
			return "redirect:/boardList"; 
		}
	}
	
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
		
		return "redirect:/boardList";		
	}
	
	// 입력 폼
	@GetMapping("/addBoard")
	public String addBoard() {
		return "addBoard";
	}
}
