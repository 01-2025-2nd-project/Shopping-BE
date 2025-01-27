package com.github.farmplus.repository.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserApiResponse { // API 응답 메시지 일관성 있게 처리
    private int code;
    private String message;
    private boolean check;

    public UserApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.check = false;
    }

    public UserApiResponse (int code, String message, boolean check) {
        this.code = code;
        this.message = message;
        this.check = check;
    }
    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
