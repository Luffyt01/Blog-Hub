package com.project.bloghub.post_service.event;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreatedEvent {

    Long creatorId;
    String content;
    Long postId;
}
