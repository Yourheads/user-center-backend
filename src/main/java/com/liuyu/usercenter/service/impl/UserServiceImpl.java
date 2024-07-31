package com.liuyu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liuyu.usercenter.common.ErrorCode;
import com.liuyu.usercenter.exception.BusinessException;
import com.liuyu.usercenter.model.domain.User;
import com.liuyu.usercenter.service.UserService;
import com.liuyu.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.liuyu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 12980
* @description 针对表【user】的数据库操作Service实现
* @createDate 2022-12-28 16:36:46
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Resource
    UserMapper userMapper;
    /**
     *盐值，混淆密码
     */
    private static final String SALT = "liuyu";
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String studyNumber) {
        //1.校验
        if (StringUtils.isAllBlank(userAccount,userPassword,checkPassword,studyNumber)){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"账号小于四位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"密码小于八位");
        }
        if (studyNumber.length() > 5){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"学号大于五位");
        }
        //账户不能包含特殊字符
        String validPattern = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"账号不能包含特殊字符");
        }
        //密码和校验密码是否相同
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"两次输入密码不同");
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"账号不能重复");
        }
        //学号是否重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("studyNumber",studyNumber);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"学号不能重复");
        }
        //2.加密
        String encodedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encodedPassword);
        user.setStudyNumber(studyNumber);
        boolean save = this.save(user);
        if (!save){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAllBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"账号不能小于四位");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"密码小于八位");
        }
        //账户不能包含特殊字符
        String validPattern = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"账号不能包含特殊字符");
        }
        //2.加密
        String encodedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.校验账号和密码是否正确
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encodedPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"账号或者密码错误");
        }
        //4.用户脱敏
        User safetyUser = this.getSafetyUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


    @Override
    public User getSafetyUser(User user){
        if (user == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setStudyNumber(user.getStudyNumber());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setLabels(user.getLabels());
        return safetyUser;
    }

    @Override
    public List<User> searchUsersByLabels(List<String> labelNameList){
        if(CollectionUtils.isEmpty(labelNameList)){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE);
        }
        //方式一：sql查询
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        //拼接and查询
//        for (String labelName : labelNameList){
//            queryWrapper.like("labels",labelName);
//        }
//        List<User> users = userMapper.selectList(queryWrapper);
//        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
        //方式二：内存查询
        //1.先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> users = userMapper.selectList(queryWrapper);
        //2.在内存中判断是否包含要求的标签
        return users.stream().filter(user -> {
            String labelsStr = user.getLabels();
//            if (StringUtils.isBlank(labelsStr)){
//                return false;
//            }
            Gson gson = new Gson();
            Type listType = new TypeToken<Set<String>>(){}.getType();
            Set<String> labelNames = gson.fromJson(labelsStr, listType);
            labelNames = Optional.ofNullable(labelNames).orElse(new HashSet<String>());
            for (String labelName : labelNameList) {
                if (!labelNames.contains(labelName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());

    }

    @Override
    public Long searchUserByUserAccount(String userAccount) {
        if(StringUtils.isBlank(userAccount)){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE);
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount",userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            throw new BusinessException(ErrorCode.PAEAMS_ERROE,"用户不存在");
        }
        //4.用户脱敏
        User safetyUser = this.getSafetyUser(user);
        return safetyUser.getId();
    }

    @Override
    public int updateUserByUserAccount(String username, String userAccount, Integer gender, String phone, String email, String studyNumber) {
        if (StringUtils.isAllBlank(username,userAccount,String.valueOf(gender),phone,email,studyNumber)){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE);
        }
        User user = new User();
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
//        userUpdateWrapper.set("username",username);
//        userUpdateWrapper.set("userAccount",userAccount);
//        userUpdateWrapper.set("gender",gender);
//        userUpdateWrapper.set("phone",phone);
//        userUpdateWrapper.set("email",email);
//        userUpdateWrapper.set("studyNumber",studyNumber);
        user.setUsername(username);
        user.setUserAccount(userAccount);
        user.setGender(gender);
        user.setPhone(phone);
        user.setEmail(email);
        user.setStudyNumber(studyNumber);
        userUpdateWrapper.eq("userAccount",userAccount);
        int update = userMapper.update(user,userUpdateWrapper);
        return 1;
    }

}




