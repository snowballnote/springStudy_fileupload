package com.example.fileupload.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.fileupload.dto.Board;
import com.example.fileupload.dto.Boardfile;

@Mapper
public interface BoardMapper { // -> @Repasitory ? implements BoardMapper
	// 입력

	int insertBoard(Board b);
	int insertBoardfile(Boardfile bf);
}
