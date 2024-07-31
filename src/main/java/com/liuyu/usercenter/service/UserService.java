package com.liuyu.usercenter.service;

import com.liuyu.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 12980
* @description 针对表【user】的数据库操作Service
* @createDate 2022-12-28 16:36:46
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @param studyNumber 学号
     * @return 新用户的ID
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String studyNumber);

    /**
     * 用户登陆
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 存储登陆状态
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    int userLogout(HttpServletRequest request);
    /**
     * 用户脱敏
     * @param user 脱敏之前的用户
     * @return 脱敏之后的用户
     */
    User getSafetyUser(User user);

    /**
     * 根据标签搜索用户
     * @param labelNameList
     * @return
     */
    List<User> searchUsersByLabels(List<String> labelNameList);

    /**
     * 根据账户名来搜索用户
     * @param userAccount 账户名
     * @return 查找到的账户名的id
     */
    Long searchUserByUserAccount(String userAccount);

    /**
     * 根据账户名来修改用户
     * @param username 昵称
     * @param userAccount 账户名
     * @param gender 性别
     * @param phone 电话
     * @param email 邮箱
     * @param studyNumber 学号
     * @return
     */
    int updateUserByUserAccount(String username,String userAccount,Integer gender,String phone,String email,String studyNumber);
}
