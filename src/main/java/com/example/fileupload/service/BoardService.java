package com.example.fileupload.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.fileupload.dto.Board;
import com.example.fileupload.dto.BoardForm;
import com.example.fileupload.dto.Boardfile;
import com.example.fileupload.exception.RowZeroException;
import com.example.fileupload.mapper.BoardMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class BoardService {
	@Autowired BoardMapper boardMapper;

	// 상세보기
	public Board getBoardOne(int boardNo) {
		return boardMapper.selectBoardOne(boardNo);
	}
	// 상세보기(파일 목록)
	public List<Boardfile> getBoardfileList(int boardNo){
		return boardMapper.selectBoardfileListByBoardOne(boardNo);
	}
	
	// 목록보기 boardList
	public List<Board> getBoardList() {
		// 페이징 로직, ...
		return boardMapper.selectBoardList();
		
	}

	public void addBoard(BoardForm bf, String path) { // path: 업로드 파일이 저장될 위치
		// 1) board 입력 - SQLExcetion 발생 - Transactional 동작
		Board b = new Board();
		b.setTitle(bf.getTitle());
		int row = boardMapper.insertBoard(b);
		log.debug("row: " + row);
		log.debug("입력된 board_no: " + b.getBoardNo());
		if(row != 1) {
			throw new RowZeroException(); 
			// 사용자정의 예외를 생성 후 하여도 된다. RowZeroException extends RuntimeException: try-catch 강제하지 않음
		}
		
		// 2) boardfile들을 입력 - SQLExcetion 발생, 파일입력실패 row==0 - 강제로 예외로 발생 - Transactional 동작 
		// 하나이상의 파일이 존재한다면 - insert boardfile
		if(bf.getBoardfile().get(0).getSize() > 0) {
			for(MultipartFile mf : bf.getBoardfile()) {
				// 파일 이름에서 마지막 점의 index
				int idx = mf.getOriginalFilename().lastIndexOf(".");
				// 확장자만
				String extension = mf.getOriginalFilename().substring(idx+1);
				// 파일이름만
				String originName = mf.getOriginalFilename().substring(0, idx);
				// 새파일이름
				String filename = UUID.randomUUID().toString().replace("-", "");
				
				Boardfile boardfile = new Boardfile();
				boardfile.setBoardNo(b.getBoardNo());
				boardfile.setOriginName(mf.getOriginalFilename()); // ex) .png 삭제
				boardfile.setFileSize(mf.getSize());
				boardfile.setFileType(mf.getContentType());
				boardfile.setFilename(filename);
				boardfile.setFileExtension(extension);

				log.debug(boardfile.toString());
				
				int row2 = boardMapper.insertBoardfile(boardfile);
				if(row2 != 1) {
					throw new RowZeroException(); // RuntimeException: try-catch 강제하지 않음
				}
				// 3) 파일저장 - 실패시 강제로 예외를 발생 - Transactional 동작
				try {
					mf.transferTo(new File(path + filename + "." + extension));
				} catch (Exception e) {
					throw new RuntimeException(); // FileFailException extends RuntimeException
				}
				
				
			}
		}
	}
}
