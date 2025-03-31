package com.example.hks.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.hks.entity.AnswerEntity;
import com.example.hks.entity.QuestionEntity;
import com.example.hks.exception.BusinessException;
import com.example.hks.exception.code.BaseResponseCode;
import com.example.hks.mapper.AnswerMapper;
import com.example.hks.mapper.QuestionMapper;
import com.example.hks.req.DataReqVO;
import com.example.hks.utils.Clippy;
import com.example.hks.utils.MetaMaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import javax.annotation.Resource;
import java.math.BigInteger;


@Service
@Slf4j
public class ClippyService {

    public String message = "Login";

    private static final String RPC_URL = "https://bsc-testnet.infura.io/v3/token"; // RPC URL

    private static final String PRIVATE_KEY = "";
    private static final String CONTRACT_ADDRESS = "0x2af70059Ce126e27265cbf3f4fE153723557b093";

    @Resource
    QuestionMapper questionMapper;

    @Resource
    AnswerMapper answerMapper;


    public void submitQuestion(DataReqVO dataReqVO) {
        if (StringUtils.isEmpty(dataReqVO.getAddress())
                || StringUtils.isEmpty(dataReqVO.getSign())
                || StringUtils.isEmpty(dataReqVO.getQuestion())
        ) {
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }

        boolean validate = MetaMaskUtil.validate(dataReqVO.getSign(), message,
                dataReqVO.getAddress());
        if (!validate) {
            throw new BusinessException(302, "签名验证失败");
        }

        //存数据库
        QuestionEntity question = new QuestionEntity();
        question.setAddress(dataReqVO.getAddress());
        question.setQuestion(dataReqVO.getQuestion());
        questionMapper.insert(question);

        boolean b = AIFilter();
        if (b) {
//        blockchainQuestion(dataReqVO.getAddress(), dataReqVO.getQuestion());
        }

    }

    public void answerQuestion(DataReqVO dataReqVO) {
        if (StringUtils.isEmpty(dataReqVO.getAddress())
                || StringUtils.isEmpty(dataReqVO.getSign())
                || StringUtils.isEmpty(dataReqVO.getAnswer())
        ) {
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }

        boolean validate = MetaMaskUtil.validate(dataReqVO.getSign(), message,
                dataReqVO.getAddress());
        if (!validate) {
            throw new BusinessException(302, "签名验证失败");
        }

        //存数据库
        AnswerEntity answer = new AnswerEntity();
        answer.setQuestionId(dataReqVO.getQuestionId());
        answer.setAnswer(dataReqVO.getAnswer());
        answer.setAddress(dataReqVO.getAddress());
        answerMapper.insert(answer);

        boolean b = AIFilter();
        if (b) {
            // blockchainAnswer(dataReqVO.getAddress(), dataReqVO.getQuestionId(), dataReqVO.getAnswer());
        }

    }


    public void blockchainQuestion(String userAddress, String content) {
        try {
            Clippy contract = blockchain();
            TransactionReceipt receipt = contract.addQuestion(userAddress, content).send();
            System.out.println("blockchainQuestion Transaction Hash: " + receipt.getTransactionHash());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(302, "上鏈失败");
        }
    }

    public void blockchainAnswer(String userAddress, Integer questionId, String content) {
        try {
            Clippy contract = blockchain();
            TransactionReceipt receipt = contract.addAnswer(userAddress, BigInteger.valueOf(questionId), content).send();
            System.out.println("blockchainAnswer Transaction Hash: " + receipt.getTransactionHash());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(302, "上鏈失败");
        }
    }


    public Clippy blockchain() {
        Web3j web3 = Web3j.build(new HttpService(RPC_URL));
        Credentials credentials = Credentials.create(PRIVATE_KEY);

        // 加载合约
        BigInteger gasPrice = Convert.toWei("5", Convert.Unit.GWEI).toBigInteger(); // 设置合理的 gas 价格
        BigInteger gasLimit = BigInteger.valueOf(300_000); // 预估 gas 限额

        // 使用 TransactionManager 处理交易
        TransactionManager txManager = new RawTransactionManager(web3, credentials);
        StaticGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

        // 假设你已经生成了 Java 合约 Wrapper 类（通过 web3j generate）
        return Clippy.load(CONTRACT_ADDRESS, web3, txManager, gasProvider);
    }

    /**
     * AI过滤精选 待完善
     */
    public static boolean AIFilter() {
        return false;
    }

}
