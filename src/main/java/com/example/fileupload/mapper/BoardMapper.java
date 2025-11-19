package com.example.fileupload.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.fileupload.dto.Board;
import com.example.fileupload.dto.Boardfile;

@Mapper
public interface BoardMapper { // -> @Repasitory ? implements BoardMapper
	// 게시글 번호로 모든 첨부 파일 레코드 일괄 삭제
    int removeBoardfileByBoardNo(int boardNo);
	
	// 게시글 삭제
	int removeBoard(int boardNo);
	
	// 게시글 제목 수정
    int updateBoard(Board b);
	
	// 파일 삭제
	int removeBoardfile(int fileNo);
	Boardfile selectBoardfileOne(int fileNo);
	
	// 상세보기
	Board selectBoardOne(int boardNo);
	List<Boardfile> selectBoardfileListByBoardOne(int boardNo);
	
	// 목록 - boardList // 페이징 생략 나중에 하기
	List<Board> selectBoardList();
	
	// 입력
	int insertBoard(Board b);
	int insertBoardfile(Boardfile bf);
}
