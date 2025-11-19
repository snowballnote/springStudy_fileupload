package com.example.fileupload.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fileupload.dto.BoardForm;
import com.example.fileupload.mapper.BoardMapper;

@Service
@Transactional
public class BoardService {
	@Autowired BoardMapper boardMapper;
	
	public void addBoard(BoardForm bf) {
		// 1) board 입력 - SQLExcetion 발생 - Transactional 동작
		// 2) boardfile들을 입력 - SQLExcetion 발생, 파일입력실패 row==0 - 강제로 예외로 발생 - Transactional 동작 
		// 3) 파일저장 - 실패시 강제로 예외를 발생 - Transactional 동작
	}
}
