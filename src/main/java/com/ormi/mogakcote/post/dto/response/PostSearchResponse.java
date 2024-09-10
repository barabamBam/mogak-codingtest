package com.ormi.mogakcote.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostSearchResponse {
	private Long id;
	private String title;
	private String content;
	private List<String> algorithms;
	private String nickname;
	private Integer viewCnt;
	private LocalDateTime createdAt;
}
