package com.liuyu.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -6908244328777238097L;
    private String userAccount;
    private String userPassword;
}
