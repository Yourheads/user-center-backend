package com.liuyu.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuyu.usercenter.mapper.UserMapper;
import com.liuyu.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserServiceTest {
    @Resource
    UserService userService;
    @Resource
    UserMapper userMapper;
    @Test
    void testAdduser(){
        User user = new User();
        user.setUsername("liuyu");
        user.setUserAccount("456");
        user.setAvatarUrl("https://profile.csdnimg.cn/7/9/A/0_weixin_51053555");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("533");
        user.setEmail("535");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "liuyu";
        String userPassword = "";
        String checkPassword = "12345678";
        String studyNumber = "04982003056";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,studyNumber);
        Assertions.assertEquals(-1,result);
        userAccount = "lu";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,studyNumber);
        Assertions.assertEquals(-1,result);
        userAccount = "liuyu";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,studyNumber);
        Assertions.assertEquals(-1,result);
        userAccount = "456";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,studyNumber);
        Assertions.assertEquals(-1,result);
        userAccount = "lui yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword,studyNumber);
        Assertions.assertEquals(-1,result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword,studyNumber);
        Assertions.assertEquals(-1,result);
        userAccount = "liuyu";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,studyNumber);
        Assertions.assertTrue(result > 0);
    }
    @Test
    void testSelect(){
        String encodedPassword = DigestUtils.md5DigestAsHex(("liuyu"+"12345678").getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount","liuyu");
        queryWrapper.eq("userPassword",encodedPassword);
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);
    }

    @Test
    public void testsearchUsersByLabels(){
        List<String> list = Arrays.asList("java", "python");
        List<User> users = userService.searchUsersByLabels(list);
        Assertions.assertNotNull(users);
    }
    @Test
    public void testDeleteUser(){
        Long id = 1L;
        boolean result = userService.removeById(id);
        System.out.println(result);
    }

}