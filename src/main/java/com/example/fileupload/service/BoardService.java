package com.example.fileupload.service;

import java.io.File;
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
	
	// 게시글 제목 수정
	public int updateBoard(Board b) {
		// Board Mapper를 사용하여 게시글(title)을 수정하고,
	    // 영향을 받은 행의 수(1 또는 0)를 반환합니다.
	    return boardMapper.updateBoard(b);
	}
	
	// 파일삭제
	public int removeFile(int fileNo, String path) {
		// 파일 정보 조회
		Boardfile fileInfo = boardMapper.selectBoardfileOne(fileNo);
		log.debug("1. 파일 정보 조회 성공. boardNo: {}", fileInfo.getBoardNo());
		// 이 파일의 게시글 번호를 저장
	    int boardNo = fileInfo.getBoardNo();
	    
		// DB 레코드 삭제
	    int row = boardMapper.removeBoardfile(fileNo);
	    log.debug("2. DB 레코드 삭제 시도. row: {}", row); // row가 0 또는 1이 나와야 함
	    
	    if (row != 1) {
	        // DB 삭제 실패 시 강제 예외 발생 (트랜잭션 롤백 유도)
	        throw new RowZeroException(); 
	    }
	    
	    // 파일 시스템에서 실제 파일 삭제
	    // 저장된 파일 이름 (UUID)과 확장자를 합칩니다.
	    String fullFileName = fileInfo.getFilename() + "." + fileInfo.getFileExtension();
	    log.debug("3. 파일 시스템 경로 확인: {}", path + fullFileName);
	    File file = new File(path + fullFileName); // 전체 경로 생성
	    
	    if (file.exists()) {
	        if (file.delete()) {
	            log.info("파일 시스템에서 파일 삭제 성공: {}", fullFileName);
	        } else {
	            // 파일 시스템 삭제 실패는 RuntimeException을 던져 트랜잭션 롤백 유도
	            // (DB 삭제된 것도 되돌려야 안전합니다.)
	            log.error("파일 시스템에서 파일 삭제 실패: {}", fullFileName);
	            throw new RuntimeException("파일 시스템 삭제 중 오류 발생"); 
	        }
	    } else {
	        log.warn("파일 시스템에 파일이 존재하지 않아 삭제를 건너뜁니다: {}", fullFileName);
	    }
	    
	    return boardNo;
	}
	
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
