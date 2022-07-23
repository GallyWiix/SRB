package com.hy.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hy.common.exception.Assert;
import com.hy.common.result.R;
import com.hy.common.result.ResponseEnum;
import com.hy.common.util.RegexValidateUtils;
import com.hy.srb.base.util.JwtUtils;
import com.hy.srb.core.pojo.entity.UserInfo;
import com.hy.srb.core.pojo.query.UserInfoQuery;
import com.hy.srb.core.pojo.vo.LoginVO;
import com.hy.srb.core.pojo.vo.RegisterVO;
import com.hy.srb.core.pojo.vo.UserInfoVO;
import com.hy.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author jeremy
 * @since 2022-07-14
 */
@RestController
@Api(tags = "会员管理")
@Slf4j
@CrossOrigin
@RequestMapping("/admin/core/userInfo")
public class AdminUserInfoController {


    @Resource
    private UserInfoService userInfoService;

    @ApiOperation("获取会员分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页码",required = true)
            @PathVariable Long page,
            @ApiParam(value = "每页记录数",required = true)
            @PathVariable Long limit,
            @ApiParam(value = "查询对象",required = true)
            UserInfoQuery userInfoQuery

    ){
        Page<UserInfo> pageParam = new Page<>(page,limit);
        IPage<UserInfo> pageModel = userInfoService.listPage(pageParam,userInfoQuery);

        return R.ok().data("pageModel",pageModel);


    }


}

