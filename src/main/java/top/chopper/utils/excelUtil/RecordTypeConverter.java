package top.chopper.utils.excelUtil;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import top.chopper.enums.RecordTypeEnum;

/**
 * BillRecord 中 type导出导出字映射转换去
 */
public class RecordTypeConverter implements Converter<String> {

    // Java 类型
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    // Excel 单元格类型（字符串）
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    // Excel -> Java（导入时用）
    @Override
    public String convertToJavaData(ReadConverterContext<?> context) {
        ReadCellData<?> cellData = context.getReadCellData();
        String cellValue = cellData.getStringValue();
        return RecordTypeEnum.codeOfText(cellValue);
    }

    // Java -> Excel（导出时用）
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        String value = context.getValue();
        String text = RecordTypeEnum.textOfCode(value);
        return new WriteCellData<>(text);
    }
}
