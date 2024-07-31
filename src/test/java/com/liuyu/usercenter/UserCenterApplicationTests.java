package com.liuyu.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.util.List;

@SpringBootTest
class UserCenterApplicationTests {

    @Test
    void contextLoads() {
        String SALT = "liuyu";
        String encodedPassword = DigestUtils.md5DigestAsHex((SALT + "aaaa").getBytes());
        System.out.println(encodedPassword);
    }



}
