package com.liuyu.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuyu.usercenter.common.BaseResponse;
import com.liuyu.usercenter.common.ErrorCode;
import com.liuyu.usercenter.exception.BusinessException;
import com.liuyu.usercenter.model.domain.User;
import com.liuyu.usercenter.model.request.UserLoginRequest;
import com.liuyu.usercenter.model.request.UserRegisterRequest;
import com.liuyu.usercenter.model.request.UserUpdateRequest;
import com.liuyu.usercenter.service.UserService;
import com.liuyu.usercenter.utils.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.liuyu.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.liuyu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"参数为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String studyNumber = userRegisterRequest.getStudyNumber();
        if (StringUtils.isAllBlank(userAccount,userPassword,checkPassword,studyNumber)){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"方框内容不能为空");
        }
        long id = userService.userRegister(userAccount, userPassword, checkPassword, studyNumber);
        return ResponseUtils.success(id);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAllBlank(userAccount,userPassword)){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"方框内容不能为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResponseUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"参数为空");
        }
        int logout = userService.userLogout(request);
        return ResponseUtils.success(logout);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw  new BusinessException(ErrorCode.NOT_LOGIN,"请先登录");
        }
        Long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResponseUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        if (!hasAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"没有管理员权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> users = userService.list(queryWrapper);
        List<User> safteyUsers = new ArrayList<>();
        users.forEach(user -> {
            User safetyUser = userService.getSafetyUser(user);
            safteyUsers.add(safetyUser);
        });
        return ResponseUtils.success(safteyUsers);
    }

    @PostMapping("/deletebyid")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request){
        if (!hasAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"没有管理员权限");
        }
        if (id <= 0){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"参数错误");
        }
        boolean byId = userService.removeById(id);
        return ResponseUtils.success(byId);
    }

    @PostMapping("/deletebyaccount")
    public BaseResponse<Boolean> deleteUserByuserAccount(@RequestBody String userAccount,HttpServletRequest request){
        if (!hasAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"没有管理员权限");
        }
        if (userAccount == null){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"参数错误");
        }
        Long id = userService.searchUserByUserAccount(userAccount);
        if (id == null){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"用户不存在");
        }
        boolean byId = userService.removeById(id);
        return ResponseUtils.success(byId);
    }

    @PostMapping("/updateuserbyaccount")
    public BaseResponse<Integer> updateUserByAccount(@RequestBody UserUpdateRequest updateRequest){
        if (updateRequest == null){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"参数为空");
        }
        String username = updateRequest.getUsername();
        String userAccount = updateRequest.getUserAccount();
        Integer gender = updateRequest.getGender();
        String phone = updateRequest.getPhone();
        String email = updateRequest.getEmail();
        String studyNumber = updateRequest.getStudyNumber();
        if (StringUtils.isAllBlank(username,userAccount,Integer.toString(gender),phone,email,studyNumber)){
            throw  new BusinessException(ErrorCode.PAEAMS_ERROE,"方框内容不能为空");
        }
        int update = userService.updateUserByUserAccount(username, userAccount, gender, phone, email, studyNumber);
        return ResponseUtils.success(update);
    }

    /**
     * 判断是否有管理员权限
     */
    private boolean hasAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
