package com.example.hks.controller;

import com.example.hks.req.DataReqVO;
import com.example.hks.service.ClippyService;
import com.example.hks.utils.DataResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RequestMapping("/api")
@RestController
public class ClippyController {

    @Resource
    ClippyService clippyService;

    //用户提交问题
    @PostMapping("/submit_question")
    public DataResult sendDao(@RequestBody DataReqVO dataReqVO) {
        DataResult result = DataResult.success();
        clippyService.submitQuestion(dataReqVO);
        return result;
    }

    //用户回答问题
    @PostMapping("/answer_question")
    public DataResult userDao(@RequestBody DataReqVO dataReqVO) {
        DataResult result = DataResult.success();
        clippyService.answerQuestion(dataReqVO);
        return result;
    }

}
