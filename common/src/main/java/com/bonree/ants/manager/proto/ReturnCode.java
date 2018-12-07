package com.bonree.ants.manager.proto;

public enum ReturnCode {

    OK(2000), // 成功
    FILE_NOT_EXIST(30001),
    FILE_QUEUE_FULL(30002);

    private final int code;

    private ReturnCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ReturnCode valueOf(int code) {
        ReturnCode[] returnCodes = ReturnCode.values();
        for (ReturnCode returnCode : returnCodes) {
            if (returnCode.getCode() == code) {
                return returnCode;
            }
        }
        return null;
    }

}
