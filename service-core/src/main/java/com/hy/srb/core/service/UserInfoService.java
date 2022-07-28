package com.hy.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hy.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hy.srb.core.pojo.query.UserInfoQuery;
import com.hy.srb.core.pojo.vo.LoginVO;
import com.hy.srb.core.pojo.vo.RegisterVO;
import com.hy.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author jeremy
 * @since 2022-07-14
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO registerVO);

    UserInfoVO login(LoginVO loginVO, String ip);
    //根据查询条件进行查询
    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    void lock(Long id,Integer status);

    boolean checkMobile(String mobile);
}
