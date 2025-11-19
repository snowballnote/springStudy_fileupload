package com.example.fileupload.dto;

import lombok.Data;

@Data
public class Boardfile {
	private int fileNo;
	private int boardNo;
	private String filename;
	private String originName;
	private long fileSize;
	private String fileType;
	private String fileExtension; // 확장자
}
