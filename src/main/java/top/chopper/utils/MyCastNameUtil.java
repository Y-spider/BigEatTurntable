package top.chopper.utils;

import top.chopper.pojo.Test;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
   @Author:ROBOT
   @DateTime:2024/3/31 15:22
   @Version:1.0.0
   @Description:  这个工具类是用于转化pojo中的属性名称，快速获取(属性名1，属性名2,....,属性名n)
   */
@SuppressWarnings("all")
public class MyCastNameUtil {
    public static void main(String[] args) {
        Class clazz = Test.class;
        System.out.print("<sql id=\"allTableField\">");
        System.out.print(getNameStrOfTable(clazz));
        System.out.println("</sql>");
        System.out.print("<sql id=\"allPojoField\">");
        System.out.print(getNameStrOfPojo(clazz));
        System.out.println("</sql>");
        getUpdateDynamicStr(clazz);
    }
    public static String getNameStrOfTable(Class clazz){
        // 只支持单个以及两个单词的属性名
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        // 进行遍历类中属性
        Field[] declaredFields = clazz.getDeclaredFields();
        Arrays.stream(declaredFields).forEach(field -> {
            String name = field.getName();
            if(!name.equals("serialVersionID")){
                String regStr = "([a-z]*)(([A-Z]{1}[a-z]*)*)";
                Pattern compile = Pattern.compile(regStr);
                Matcher matcher = compile.matcher(name);
                while (matcher.find()){
                    String t = matcher.group(1);
                    if(t!=""){
                        if(!matcher.group(2).equals("")){
                            t+="_"+matcher.group(2).toLowerCase();
                        }
                        t+=",";
                        stringBuilder.append(t);
                    }
                }
            }
        });
        stringBuilder.append(")");
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        return stringBuilder.toString();
    }

    public static String getNameStrOfPojo(Class clazz){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        Field[] declaredFields = clazz.getDeclaredFields();
        Arrays.stream(declaredFields).forEach(field -> {
            String name = field.getName();
            if(!name.equals("serialVersionID")){
                String t = "#{" + name + "}";
                stringBuilder.append(t + ",");
            }
        });
        stringBuilder.append(")");
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        return stringBuilder.toString();
    }


    public static String getUpdateDynamicStr(Class clazz){
        String nameStrOfPojo = getNameStrOfPojo(clazz);
        String pojo = nameStrOfPojo.substring(1, nameStrOfPojo.length() - 1);
        String nameStrOfTable = getNameStrOfTable(clazz);
        String table = nameStrOfTable.substring(1, nameStrOfTable.length() - 1);
        String[] splitTable = table.split(",");
        String[] splitPojo = pojo.split(",");
        for (int i = 0; i < splitTable.length; i++) {
            String tableF = splitTable[i];
            String substring = splitPojo[i].substring(2,splitPojo[i].length() - 1);
            System.out.println("<if test=\""+substring+"!=null\">\n" +
                    "                "+tableF+"=#{"+substring+"},\n" +
                    "</if>");
        }
        return null;
    }
}
