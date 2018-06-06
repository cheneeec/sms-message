package com.gongsj.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
public class NotifyContent {
    @Id
    private String id;
    private String content;
}
