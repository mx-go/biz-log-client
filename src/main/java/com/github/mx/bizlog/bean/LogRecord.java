package com.github.mx.bizlog.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * Create by max on 2021/02/27
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogRecord {

    private String appName;

    @NotBlank(message = "bizId required")
    @Length(max = 200, message = "bizId max length is 200")
    private String bizId;

    @NotBlank(message = "operatorId required")
    @Length(max = 63, message = "operatorId max length is 63")
    private String operatorId;

    private Boolean success;

    @NotBlank(message = "opAction required")
    @Length(max = 511, message = "opAction max length is 511")
    private String action;

    private String category;
    private Date createTime;

    private String content;
}
