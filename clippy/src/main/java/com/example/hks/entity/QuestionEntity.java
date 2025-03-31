package com.example.hks.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("question")
public class QuestionEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("question")
    private String question;

    private String address;

}
