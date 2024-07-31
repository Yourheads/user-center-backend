package com.liuyu.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 8027524691618947788L;
    private String username;
    private String userAccount;
    private Integer gender;
    private String phone;
    private String email;
    private String studyNumber;
}
