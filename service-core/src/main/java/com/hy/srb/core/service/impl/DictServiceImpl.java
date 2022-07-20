package com.hy.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.hy.srb.core.listener.ExcelDictDTOListener;
import com.hy.srb.core.pojo.dto.ExcelDictDTO;
import com.hy.srb.core.pojo.entity.Dict;
import com.hy.srb.core.mapper.DictMapper;
import com.hy.srb.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author jeremy
 * @since 2022-07-14
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    private RedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class,new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("excel导入成功");
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);

        //创建exceldictdto列表，将dict转换
        ArrayList<ExcelDictDTO> excelDictDTOList = new ArrayList<>(dictList.size());
        dictList.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict,excelDictDTO);
            excelDictDTOList.add(excelDictDTO);
        });
        return excelDictDTOList;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {

        //首先查询redis中是否存在数据列表
        try {
            List<Dict> dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
            if(dictList != null){
                //如果存在则从redis中获取
                log.info("从redis中获取数据");
                return dictList;
            }
        } catch (Exception e) {
            log.error("redis服务器异常" + ExceptionUtils.getStackTrace(e));
        }


        //否则从数据库中查询
        log.info("从数据库中获取");
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",parentId);
        List<Dict> dictList = baseMapper.selectList(dictQueryWrapper);
        //填充haschilren字段
        dictList.forEach(dict -> {
            //判断是否有子节点
            boolean hasChildren = this.hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });
        try {
            //将数据存入redis
            log.info("将数据存入redis");
            redisTemplate.opsForValue().set("srb:core:dictList:" + parentId, dictList,5, TimeUnit.MINUTES );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dictList;
    }

    private boolean hasChildren(Long id){
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(dictQueryWrapper);
        if(count.intValue() > 0){
            return true;
        }
        return false;
    }
}
