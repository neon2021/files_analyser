package com.neon.file.analyser.mongodb.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/11/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProcessInfo {
    @Id
    public String uuid;

}
