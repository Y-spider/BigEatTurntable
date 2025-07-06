package top.chopper.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.io.ApplicationResourceLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import top.chopper.mapper.UtilMapper;
import top.chopper.pojo.UtilComment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2024/4/29 16:35
   @Version:1.0.0
   @Description: 由于再系统中使用了Kief4j接口生成框架，需要再实体类上实体@Schema来对字段进行备注，但是发现如果再数据库中给字段写了备注，
   那么再这里还得再写一遍，之前写的少还没什么感觉，现在写的多了，实在是无味并且繁琐，所以这个工具类就用来给pojo类自动生成@Schema注解并且将数据库
   对应字段的注解添加再通过@Schema添加给实体类对应字段。
   当属性名为serialVersionID 时不会生生 @Schema注解。

   */
@Component
@SuppressWarnings("all")
public class MyGetCommentUtil {
    @Autowired
    private UtilMapper utilMapper;

    public void generate(String tableName,Class clazz){
        Field[] declaredFields = clazz.getDeclaredFields();
        String[] strings = handleTableField(tableName);
        List<Field> fields = Arrays.stream(declaredFields).toList();
        List<UtilComment> comment = utilMapper.getComment(tableName);
        BufferedWriter bufferedWriter = null;
        try {
            ClassPathResource resource = new ClassPathResource("fileds.txt");
            String absolutePath = resource.getFile().getAbsolutePath();
            bufferedWriter = new BufferedWriter(new FileWriter(absolutePath));
            for(int i = 0;i < fields.size();i++){
                if("serialVersionID".equals(fields.get(i).getName())) continue;
                boolean isFind = false;
                for(int j = 0;j < strings.length;j++){
                    if(strings[j].equals(fields.get(i).getName())){
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("@Schema(name=\"");
                        stringBuilder.append(strings[j]+"\",");
                        stringBuilder.append("description=\"");
                        stringBuilder.append(comment.get(j).getColumnComment()+"\"");
                        stringBuilder.append(")");
                        System.out.println(stringBuilder.toString());
                        // 找到pojo中对应字段
                        // private BigInteger id;
                        Field field = fields.get(i);
                        field.setAccessible(true);
                        String pojoFieldString = getPojoFieldString(field);
                        // 写入 @Schema注解
                        bufferedWriter.write(stringBuilder.toString());
                        bufferedWriter.newLine();
                        bufferedWriter.write(pojoFieldString);
                        bufferedWriter.newLine();
                        isFind = true;
                        break;
                    }
                }
                if(!isFind){
                    // 表示当前pojo字段在table表字段中没有匹配到
                    Field field = fields.get(i);
                    String allName = field.getName();
                    String name = allName.substring(allName.lastIndexOf(".") + 1, allName.length());
                    String pojoFieldString = getPojoFieldString(field);
                    bufferedWriter.write("@Schema(name=\""+name+"\",description=\"\")");
                    bufferedWriter.newLine();
                    bufferedWriter.write(pojoFieldString);
                    bufferedWriter.newLine();
                }
            }
            System.out.println("生成完毕");
        }catch ( Exception e ){
            e.printStackTrace();
        }finally {
            if(bufferedWriter != null){
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch ( IOException e ) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * @param tableName 表名称
     * @param isAppend 是否追加写入
     * @param clazz 生成sql语句的实体类class   列如User.clazz
     * 这个是generate的升级版，generate需要传入一个实体类，并且在这个实体类中已经写好了所有的字段
     * 而generatePerfect可以根据表中的字段自动生成pojo属性。注意生成的属性格式为 private 类型  属性名(驼峰)
     * 数据库与pojo字段类转换规则为
     *                  数据库                pojo字段
     *                  int                   Integer
     *                  Bigint                Long
     *                  varchar               String
     *                  datetime              LocalDateTime
     *                  一般只用到这几个，后面有需要再进行补充
     *  tips: 最后生成的结果都会写入到 D:\typora\Nantai Micro Campus\Server\NantaiServer\src\main\java\comment.txt (当然也是可以更改的)
     */
    public void generatePerfect(String tableName,Boolean isAppend,Class clazz){
        String[] strings = handleTableField(tableName);
        List<UtilComment> utilComments = utilMapper.getComment(tableName);
        BufferedWriter bufferedWriter = null;
        try {
            ResourceLoader resourceLoader = new ApplicationResourceLoader();
            File file = resourceLoader.getResource("src/main/resources/fileds.txt").getFile();
//            System.out.println("absolutePath====>" + file.getAbsolutePath());
            bufferedWriter = new BufferedWriter(new FileWriter(file,isAppend));
            bufferedWriter.write("======" + tableName +"表字段生成==========");
            if(clazz!=null){
                // 生成sql语句
                writeTableSql(bufferedWriter,clazz);
            }
            for(int i = 0;i < utilComments.size();i++){
                // 1. 开始拼凑pojo字段
                StringBuilder pojoFiledBuilder = new StringBuilder();
                pojoFiledBuilder.append("private ");
                String pojoFieldType = transferDateType(utilComments.get(i).getDataType());
                pojoFiledBuilder.append(pojoFieldType+" "+strings[i]+";");
                // 2. 开始拼凑@Schema注解
                StringBuilder schemaBuilder = new StringBuilder();
                schemaBuilder.append("@Schema(name=\"");
                schemaBuilder.append(strings[i]+"\",");
                schemaBuilder.append("description=\"");
                schemaBuilder.append(utilComments.get(i).getColumnComment()+"\")");
                // 3. 开始写入文件中
                bufferedWriter.newLine();
                bufferedWriter.write(schemaBuilder.toString());
                bufferedWriter.newLine();
                bufferedWriter.write(pojoFiledBuilder.toString());
                System.out.println("生成字段"+strings[i]+"成功");
            }


        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }finally {
            if(bufferedWriter!=null){
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    System.out.println("关闭文件输入流成功~~~");
                } catch ( IOException e ) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    /**
     * @param tableName 表名
     * @param isAppend 是否追加写入
     */
    public void generatePerfect(String tableName,Boolean isAppend){
        String[] strings = handleTableField(tableName);
        List<UtilComment> utilComments = utilMapper.getComment(tableName);
        BufferedWriter bufferedWriter = null;
        try {
            ResourceLoader resourceLoader = new ApplicationResourceLoader();
            File file = resourceLoader.getResource("src/main/resources/fileds.txt").getFile();
//            System.out.println("absolutePath====>" + file.getAbsolutePath());
            bufferedWriter = new BufferedWriter(new FileWriter(file,isAppend));
            bufferedWriter.write("\n======" + tableName +"表字段生成==========\n");
            for(int i = 0;i < utilComments.size();i++){
                // 1. 开始拼凑pojo字段
                StringBuilder pojoFiledBuilder = new StringBuilder();
                pojoFiledBuilder.append("private ");
                String pojoFieldType = transferDateType(utilComments.get(i).getDataType());
                pojoFiledBuilder.append(pojoFieldType+" "+strings[i]+";");
                // 2. 开始拼凑@Schema注解
                StringBuilder schemaBuilder = new StringBuilder();
                schemaBuilder.append("@Schema(name=\"");
                schemaBuilder.append(strings[i]+"\",");
                schemaBuilder.append("description=\"");
                schemaBuilder.append(utilComments.get(i).getColumnComment()+"\")");
                // 3. 开始写入文件中
                bufferedWriter.newLine();
                bufferedWriter.write(schemaBuilder.toString());
                bufferedWriter.newLine();
                bufferedWriter.write(pojoFiledBuilder.toString());
                System.out.println("生成字段"+strings[i]+"成功");
            }


        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }finally {
            if(bufferedWriter!=null){
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    System.out.println("关闭文件输入流成功~~~");
                } catch ( IOException e ) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void writeTableSql(BufferedWriter bufferedWriter,Class clazz){
        try {
            bufferedWriter.write("<sql id=\"allTableField\">");
            bufferedWriter.write(MyCastNameUtil.getNameStrOfTable(clazz));
            bufferedWriter.write("</sql>");
            bufferedWriter.newLine();
            bufferedWriter.write("<sql id=\"allPojoField\">");
            bufferedWriter.write(MyCastNameUtil.getNameStrOfPojo(clazz));
            bufferedWriter.write("</sql>");
            MyCastNameUtil.getUpdateDynamicStr(clazz,bufferedWriter);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }
    private String[] handleTableField(String tableName) {
        List<UtilComment> comment = utilMapper.getComment(tableName);
        String[] hadledTableField = new String[comment.size()];
        for (int j = 0; j < comment.size(); j++) {
            // 对数据库字段进行处理
            String tableFiled = comment.get(j).getColumnName();
            if (tableFiled.contains("_")) {
                int t = tableFiled.lastIndexOf('_');
                String substring = tableFiled.substring(0, t);
                String substring1 = tableFiled.substring(t + 2, tableFiled.length());
                tableFiled = substring + tableFiled.toUpperCase().charAt(t + 1) + substring1;
            }
            hadledTableField[j] = tableFiled;
        }
        return hadledTableField;
    }

    private String getPojoFieldString(Field field){
        // 构建pojo类字段 标识符  类型  属性名
        String fieldString = field.toString();
        String[] split = fieldString.split(" ");
        StringBuilder handledPojoFieldString = new StringBuilder();
        for(int t = 0;t < split.length;t++){
            if(split[t].lastIndexOf(".")==-1){
                handledPojoFieldString.append(split[t]);
                handledPojoFieldString.append(" ");
            }else{
                String substring = split[t].substring(split[t].lastIndexOf(".") + 1, split[t].length());
                handledPojoFieldString.append(substring);
                handledPojoFieldString.append(" ");
            }
        }
        handledPojoFieldString.replace(handledPojoFieldString.length()-1,handledPojoFieldString.length(),"");
        handledPojoFieldString.append(";");
        return handledPojoFieldString.toString();
    }


    private String transferDateType(String tableDateType){
        // 进行数据库与pojo之间的数据类型转换
        switch (tableDateType){
            case "int":return "Integer";
            case "bigint":return "Long";
            case "varchar":return "String";
            case "datetime":return "LocalDateTime";
            case "tinyint":return "Boolean";
            default:throw new RuntimeException("数据库字段"+tableDateType+"不在转换规则中");
        }
    }
}
