package com.lukuqi.newone.Bean;

import java.util.List;

/**
 * 返回数据的基本格式
 * Created by mr.right on 2016/3/28.
 */
public class UserBase<T> {
    private String code;
    private List<T> message;
    private String result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<T> getMessage() {
        return message;
    }

    public void setMessage(List<T> message) {
        this.message = message;
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
