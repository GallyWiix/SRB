package com.hy.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hy.srb.core.mapper.DictMapper;
import com.hy.srb.core.pojo.dto.ExcelDictDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

    private DictMapper dictMapper;
    //创建数据列表
    List<ExcelDictDTO> list = new ArrayList<>();
    //每次存储数据的大小
    private static final int BATCH_COUNT = 5;

    public ExcelDictDTOListener(DictMapper dictMapper){
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {
        log.info("读取到一条记录：{}", excelDictDTO);
        //数据存入数据列表
        list.add(excelDictDTO);
        if(list.size() >= BATCH_COUNT){
            saveData();
            list.clear();
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //当剩余数据<batch——count时，最终一次性存储
        saveData();
        log.info("所有数据读取完成");
    }

    private void saveData(){
        //调用mapper层的批量save方法
        dictMapper.insertBatch(list);
        log.info("{}条数据被存储到数据库",list.size());
    }
}
