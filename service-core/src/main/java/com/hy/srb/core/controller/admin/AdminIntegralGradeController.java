package com.hy.srb.core.controller.admin;


import com.hy.common.exception.BusinessException;
import com.hy.common.result.R;
import com.hy.common.result.ResponseEnum;
import com.hy.srb.core.pojo.entity.IntegralGrade;
import com.hy.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author jeremy
 * @since 2022-07-14
 */
@Api(tags = "积分等级管理")
@CrossOrigin
@RestController
@RequestMapping("/admin/core/integralGrade")
@Slf4j
public class AdminIntegralGradeController {
    @Resource
    private IntegralGradeService integralGradeService;


    @ApiOperation("积分等级列表")
    @GetMapping("/list")
    public R listAll(){
        log.info("this is log info");
        log.warn("this is log warn");
        log.error("this is log error");
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list).message("获取列表成功");

    }

    @ApiOperation(value = "根据id删除数据")
    @DeleteMapping ("/remove/{id}")
    public R removeById(
            @ApiParam(value = "数据id",example = "100",required = true)
            @PathVariable Long id){
        boolean result = integralGradeService.removeById(id);
        if(result){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }

    @ApiOperation(value = "新增积分等级")
    @PostMapping("/save")
    public R save(
            @ApiParam(value = "积分等级对象" ,required = true)
            @RequestBody IntegralGrade integralGrade){

        if(integralGrade.getBorrowAmount() == null){
            throw new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        }
        boolean result = integralGradeService.save(integralGrade);
        if(result){
            return R.ok().message("新增成功");
        }else {
            return R.error().message("新增失败");
        }
    }
    @ApiOperation(value = "根据id获得积分等级")
    @GetMapping("/get/{id}")
    public R getById(
            @ApiParam(value = "数据id")
            @PathVariable Long id){
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if (integralGrade != null){
            return R.ok().data("record",integralGrade);

        }else {
            return R.error().message("根据id获取积分等级失败");
        }

    }

    @ApiOperation(value = "更新积分等级")
    @GetMapping("/update")
    public R updateById(
            @ApiParam(value = "积分等级对象")
            @RequestBody IntegralGrade integralGrade){

        boolean save = integralGradeService.updateById(integralGrade);
        if (save){
            return R.ok().message("保存成功");

        }else {
            return R.error().message("保存失败");
        }

    }

}

