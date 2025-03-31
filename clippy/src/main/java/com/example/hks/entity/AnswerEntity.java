package com.example.hks.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("answer")
public class AnswerEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("question_id")
    private Integer questionId;

    private String address;

    @TableField("answer")
    private String answer;

}
