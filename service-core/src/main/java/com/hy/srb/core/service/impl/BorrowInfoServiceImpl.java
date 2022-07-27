package com.hy.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hy.common.exception.Assert;
import com.hy.common.result.ResponseEnum;
import com.hy.srb.core.mapper.IntegralGradeMapper;
import com.hy.srb.core.mapper.UserInfoMapper;
import com.hy.srb.core.pojo.entity.BorrowInfo;
import com.hy.srb.core.mapper.BorrowInfoMapper;
import com.hy.srb.core.pojo.entity.IntegralGrade;
import com.hy.srb.core.pojo.entity.UserInfo;
import com.hy.srb.core.service.BorrowInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author jeremy
 * @since 2022-07-14
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;
    @Override
    public BigDecimal getBorrowAmount(Long userId) {

        //获取用户积分
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        Integer integral = userInfo.getIntegral();

        //根据积分获取额度
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper
                .le("integral_start",integral)
                .ge("integral_end",integral);

        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);
        if(integralGrade == null){
            return new BigDecimal("0");
        }
        return integralGrade.getBorrowAmount();

    }
}
