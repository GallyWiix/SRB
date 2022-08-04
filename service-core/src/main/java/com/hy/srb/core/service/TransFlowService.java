package com.hy.srb.core.service;

import com.hy.srb.core.pojo.bo.TransFlowBO;
import com.hy.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author jeremy
 * @since 2022-07-14
 */
public interface TransFlowService extends IService<TransFlow> {
     void saveTransFlow(TransFlowBO transFlowBO);

     boolean isSaveTransFlow(String agentBillNo);

    List<TransFlow> selectByUserId(Long userId);
}
