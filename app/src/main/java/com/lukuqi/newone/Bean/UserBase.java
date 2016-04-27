package com.lukuqi.newone.Bean;

import java.util.List;

/**
 * Created by mr.right on 2016/3/28.
 */
public class UserBase {
    private String code;
    private List<UserInfo> message;
    private String result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<UserInfo> getMessage() {
        return message;
    }

    public void setMessage(List<UserInfo> message) {
        message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "UserBase{" +
                "code='" + code + '\'' +
                ", Message=" + message +
                ", result='" + result + '\'' +
                '}';
    }
}
