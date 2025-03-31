package com.example.hks.exception.code;


public enum BaseResponseCode implements ResponseCodeInterface {

    SUCCESS(0, "success"),
    SYSTEM_ERROR(501, "系统异常，请稍后重试"),

    DATA_ERROR(401, "参数错误"),
    VALIDATOR_ERROR(402, "参数验证异常"),
    ;

    /**
     * 响应码
     */
    private int code;

    /**
     * 提示
     */
    private String msg;

    BaseResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
