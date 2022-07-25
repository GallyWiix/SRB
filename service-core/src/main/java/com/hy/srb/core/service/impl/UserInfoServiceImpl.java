package com.hy.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hy.common.exception.Assert;
import com.hy.common.result.ResponseEnum;
import com.hy.common.util.MD5;
import com.hy.srb.base.util.JwtUtils;
import com.hy.srb.core.mapper.UserAccountMapper;
import com.hy.srb.core.mapper.UserLoginRecordMapper;
import com.hy.srb.core.pojo.entity.UserAccount;
import com.hy.srb.core.pojo.entity.UserInfo;
import com.hy.srb.core.mapper.UserInfoMapper;
import com.hy.srb.core.pojo.entity.UserLoginRecord;
import com.hy.srb.core.pojo.query.UserInfoQuery;
import com.hy.srb.core.pojo.vo.LoginVO;
import com.hy.srb.core.pojo.vo.RegisterVO;
import com.hy.srb.core.pojo.vo.UserInfoVO;
import com.hy.srb.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author jeremy
 * @since 2022-07-14
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {

        //判断用户是否已被注册
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",registerVO.getMobile());
        Integer count = baseMapper.selectCount(userInfoQueryWrapper);
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);
        //插入用户信息记录:user_info
        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        userInfo.setHeadImg("https://guli-filehy.oss-cn-hangzhou.aliyuncs.com/ok.jpg");
        baseMapper.insert(userInfo);
        //插入用户账户记录:user_account
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();
        //判断用户是否存在

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper
                .eq("mobile",mobile)
                .eq("user_type",userType);

        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);

        Assert.notNull(userInfo,ResponseEnum.LOGIN_MOBILE_ERROR);

        //密码是否正确

        Assert.equals(MD5.encrypt(password),userInfo.getPassword(),ResponseEnum.LOGIN_PASSWORD_ERROR);

        //用户是否被禁言
        Assert.equals(userInfo.getStatus(),UserInfo.STATUS_NORMAL,ResponseEnum.LOGIN_LOKED_ERROR);

        //记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);
        //生成token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());

        //组装userinfovo
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setToken(token);
        userInfoVO.setName(userInfoVO.getName());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        userInfoVO.setMobile(mobile);
        userInfoVO.setUserType(userType);

        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery) {
        if(userInfoQuery == null){
            return baseMapper.selectPage(pageParam,null);
        }
        String mobile = userInfoQuery.getMobile();
        Integer status = userInfoQuery.getStatus();
        Integer userType = userInfoQuery.getUserType();

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper
                .eq(StringUtils.isNotBlank(mobile),"mobile",mobile)
                .eq(status != null,"status",status)
                .eq(userType != null,"user_type",userType);
        return baseMapper.selectPage(pageParam,userInfoQueryWrapper);
    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public boolean checkMobile(String mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(userInfoQueryWrapper);
        return count > 0;
    }
}
