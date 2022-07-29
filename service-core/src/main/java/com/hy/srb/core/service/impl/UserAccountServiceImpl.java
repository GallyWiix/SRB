package com.hy.srb.core.service.impl;

import com.hy.srb.core.pojo.entity.UserAccount;
import com.hy.srb.core.mapper.UserAccountMapper;
import com.hy.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author jeremy
 * @since 2022-07-14
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        return null;
    }
}
