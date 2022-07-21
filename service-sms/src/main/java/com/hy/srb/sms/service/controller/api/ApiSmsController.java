package com.hy.srb.sms.service.controller.api;

import com.hy.common.exception.Assert;
import com.hy.common.result.R;
import com.hy.common.result.ResponseEnum;
import com.hy.common.util.RandomUtils;
import com.hy.common.util.RegexValidateUtils;
import com.hy.srb.sms.service.SmsService;
import com.hy.srb.sms.util.SmsProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
@CrossOrigin
@Slf4j
public class ApiSmsController {
    @Resource
    private SmsService smsService;
    @Resource
    private RedisTemplate redisTemplate;

    @ApiOperation("获取验证码")
    @GetMapping("/send/{mobile}")
    public R send(
            @ApiParam(value = "手机号",required = true)
            @PathVariable String mobile){
        //校验手机号码不为空
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        //校验是否合法
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResponseEnum.MOBILE_ERROR);
        String code = RandomUtils.getFourBitRandom();
        HashMap<String,Object> map = new HashMap<>();
        map.put("code", code);
        smsService.send(mobile, SmsProperties.TEMPLATE_CODE, map);

        //将验证码存入redis
        redisTemplate.opsForValue().set("srb:sms:code:" + mobile,code,5, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");

    }
}
