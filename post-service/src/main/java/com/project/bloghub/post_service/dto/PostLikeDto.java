package com.project.bloghub.post_service.dto;

import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class PostLikeDto {
    private Long id;
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;
}
