package top.chopper.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import top.chopper.utils.excelUtil.RecordTypeConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/12/3 21:27
   @Version:1.0.0
   @Description:
   */
@Data
public class BillRecordExportExcelDto {

    @ExcelProperty(value = "账本")
    private String BookName;


    @ExcelProperty("用户")
    private String name;

    @ExcelProperty(value = "账单类型",converter = RecordTypeConverter.class)
    private String type;

    @ExcelProperty("消费日期")
    private String recordTime;

    @ExcelProperty("支付方式")
    private String methodLabel;

    @ExcelProperty("消费类型")
    private String labelName;

    @ExcelProperty("金额")
    private BigDecimal amount;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    @ExcelProperty("修改时间")
    private LocalDateTime updateTime;

    @ExcelProperty("备注信息")
    private String remark;

    @ExcelProperty("总收入")
    private BigDecimal inTotal;

    @ExcelProperty("总支出")
    private BigDecimal outTotal;

    @ExcelProperty("结余")
    private BigDecimal surplus;
}
