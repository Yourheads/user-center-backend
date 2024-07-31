package com.liuyu.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 8027524691610047788L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String studyNumber;
}
