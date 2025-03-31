package com.example.hks.req;

import lombok.Data;

@Data
public class DataReqVO {
    private String address;
    private String question;
    private Integer questionId;
    private String answer;
    private String sign;
}
