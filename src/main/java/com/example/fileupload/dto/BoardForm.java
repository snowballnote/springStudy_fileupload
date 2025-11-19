package com.example.fileupload.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class BoardForm {
	private String title;
	private List<MultipartFile> boardfile;
}
