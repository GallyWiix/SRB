package com.hy.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hy.srb.core.enums.TransTypeEnum;
import com.hy.srb.core.hfb.FormHelper;
import com.hy.srb.core.hfb.HfbConst;
import com.hy.srb.core.hfb.RequestHelper;
import com.hy.srb.core.mapper.UserAccountMapper;
import com.hy.srb.core.mapper.UserInfoMapper;
import com.hy.srb.core.pojo.bo.TransFlowBO;
import com.hy.srb.core.pojo.entity.UserAccount;
import com.hy.srb.core.pojo.entity.UserInfo;
import com.hy.srb.core.service.TransFlowService;
import com.hy.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hy.srb.core.util.LendNoUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private TransFlowService transFlowService;
    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        //获取充值人绑定协议号
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getChargeNo());
        paramMap.put("bingCode",bindCode);
        paramMap.put("chargeAmt",chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl",HfbConst.RECHARGE_NOTIFY_URL);
        paramMap.put("returnUrl",HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign",RequestHelper.getSign(paramMap));

        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return formStr;
    }

    @Override
    public String notify(Map<String, Object> paramMap) {
        String agentBillNo = (String)paramMap.get("agentBillNo");
        //幂等性判断..判断交易流水是否存在
        boolean isSave = transFlowService.isSaveTransFlow(agentBillNo);
        if(isSave){
            log.warn("幂等性返回");
            return "success";
        }

        //账户处理
        String bindCode = (String) paramMap.get("bindCode");
        String chargeAmt = (String) paramMap.get("chargeAmt");
        baseMapper.updateAccount(bindCode,new BigDecimal(chargeAmt),new BigDecimal(0));

        //记录账户流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值啦");

        transFlowService.saveTransFlow(transFlowBO);
        return "success";
    }

    @Override
    public BigDecimal getAccount(Long userId) {

        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id",userId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);
        return userAccount.getAmount();

    }
}
