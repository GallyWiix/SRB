package com.hy.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.hy.common.result.R;
import com.hy.srb.base.util.JwtUtils;
import com.hy.srb.core.hfb.RequestHelper;
import com.hy.srb.core.pojo.vo.UserBindVO;
import com.hy.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author jeremy
 * @since 2022-07-14
 */
@Api(tags = "会员账号绑定")
@RestController
@RequestMapping("/api/core/userBind")
@Slf4j
public class UserBindController {
    @Resource
    private UserBindService userBindService;

    @ApiOperation("账户绑定提交数据")
    @PostMapping("/auth/bind")
    public R bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request){

        //从header中获取token，提取UserId
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        //根据userId,生成一个动态表单
        String formStr = userBindService.commitBindUser(userBindVO,userId);
        return R.ok().data("formStr",formStr);
    }

    @PostMapping("/notify")
    @ApiOperation("账户绑定异步回调")
    public String notify(HttpServletRequest httpServletRequest){
        //汇付宝想尚融宝发起回调请求时携带的参数
        Map<String, Object> paramMap = RequestHelper.switchMap(httpServletRequest.getParameterMap());
        log.info("用户异步回调接受的参数如下："+ JSON.toJSONString(paramMap));

        if(!RequestHelper.isSignEquals(paramMap)){
            log.error("用户账号绑定异步回调签名验证错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }
        log.info("验签成功！开始账户绑定");
        userBindService.notify(paramMap);
        return "success";


    }

}

