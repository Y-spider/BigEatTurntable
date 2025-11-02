package top.chopper.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.mapper.FestivalMapper;
import top.chopper.pojo.Festival;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class FestivalService {
    @Autowired
    private FestivalMapper festivalMapper;

    @Transactional
    public void init(){
        // 先删除
        LambdaQueryWrapper<Festival> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Festival::getType,"custom");
        festivalMapper.delete(queryWrapper);
        String url = "https://wannianli.tianqi.com/jieri/#:~:text=%E4%B8%87%E5%B9%B4%E5%8E%86%EF%BC%88wann,025%E5%B9%B4%E8%8A%82%E6%97%A5%E8%A1%A8%E3%80%82";
        HttpRequest get = HttpUtil.createGet(url);
        String body = get.execute().body();
//        System.out.println(body);
        String regex = "<li><a class=\"(\\w+)\" href=\"/([^/]+)/\"[^>]*>([^<]+)</a>\\[([^]]+)]</li>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            Festival festival = new Festival();
            if(matcher.group(1).equals("cGreen")){
                festival.setType("international");
            }else if(matcher.group(1).equals("cBlue")){
                festival.setType("solar_term");
            }else if(matcher.group(1).equals("cRed")){
                festival.setType("tradition");
            }else{
                festival.setType("解析失败");
            }
            String des = getFestivalContent(matcher.group(2));
            festival.setDes(des);
//            System.out.println(matcher.group(3));
            festival.setName(matcher.group(3));
            String date = matcher.group(4);
            String calDate = ""; // 农历时间
            date = date.replace("(","（");
            date = date.replace(")","）");
            if (date.contains("（")) {
                calDate = date.substring(date.indexOf("（") + 1, date.indexOf("）"));
                date = date.substring(0, date.indexOf("（"));
                festival.setCalender(calDate);
            }
            LocalDate time = handleDateRobust(date);
            festival.setTime(time);
            calcDistance(time);
            festival.setRemaining(calcDistance(time));
            festivalMapper.insert(festival);

        }

    }

    /**
     * @param dateStr  日期时间 mm:dd xx表示月 yy表示日
     * @return 返回
     */

    // 更健壮的版本
    private static LocalDate handleDateRobust(String dateStr) {
        try {
            String[] parts = dateStr.split("月");
            // 提取月份和日期数字
//            String[] parts = dateStr.split("月");
            if (parts.length == 2) {
                int month = Integer.parseInt(parts[0].trim());
                int day = Integer.parseInt(parts[1].replace("日", "").replace("号","").trim());
                // 直接创建LocalDate对象，避免字符串解析问题
                return LocalDate.of(LocalDate.now().getYear(), month, day);
            }
        } catch (Exception e) {
            System.err.println("日期解析失败: " + dateStr + " - " + e.getMessage());
        }
        return null;
    }


    private static long calcDistance(LocalDate aimTime) {
        long between = ChronoUnit.DAYS.between(LocalDate.now(), aimTime);
        // 如果日期已过，则计算到下一年的同一天
        if (between < 0) {
            LocalDate nextYear = aimTime.plusYears(1);
            between = ChronoUnit.DAYS.between(LocalDate.now(), nextYear);
        }
        return between;
    }


    private static String getFestivalContent(String partUrl){
        String url = "https://wannianli.tianqi.com/" + partUrl;
        List<HashMap<String, Object>> mapList = new ArrayList<>();
        HttpRequest request = HttpUtil.createGet(url);
        String body = request.execute().body();
        String regex = "<p class=\"t2\">\\s*([^<]*)\\s*</p>\\s*<p>([\\s\\S]*?)</p>";        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("label", deepClean(matcher.group(1)).replaceAll("</br>",""));
            map.put("value", deepClean(matcher.group(2)).replaceAll("</br>",""));
            mapList.add(map);
        }
        return JSONUtil.toJsonStr(mapList);
    }


    /**
     * 深度清理 - 处理各种空白字符
     */
    public static String deepClean(String str) {
        if (str == null) return null;

        // 1. 去除前后空白字符（包括全角空格、制表符、换行等）
        String cleaned = str.replaceAll("^[\\s\\u3000\\t\\n\\r]+|[\\s\\u3000\\t\\n\\r]+$", "");

        // 2. 替换字符串中间的全角空格为普通空格（可选）
        cleaned = cleaned.replaceAll("\\u3000", " ");

        return cleaned;
    }


    public List<Festival> listByType(String type){
        LambdaQueryWrapper<Festival> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Festival::getType,type);
        queryWrapper.orderByAsc(Festival::getTime);
        queryWrapper.orderByDesc(Festival::getCreateTime);
        List<Festival> festivals = festivalMapper.selectList(queryWrapper);
        festivals.forEach(festival -> {
            long between = ChronoUnit.DAYS.between(LocalDate.now(), festival.getTime());
            // 如果日期已过，则计算到下一年的同一天
//            if (between < 0) {
//                LocalDate nextYear = festival.getTime().plusYears(1);
//                between = ChronoUnit.DAYS.between(LocalDate.now(), nextYear);
//                festival.setTime(nextYear);
//            }
            festival.setRemaining(between);

        });
        return festivals;
    }

    public Festival queryById(Integer id){
        return festivalMapper.selectById(id);
    }


    @Transactional
    public void addCustomFestival(Festival festival){
        festival.setType("custom");
        festival.setOpenid(SecurityUtil.getUserName());
        festival.setStatus(1);
        festival.setCreateTime(LocalDateTime.now());
        festivalMapper.insert(festival);
    }

    @Transactional

    public void deleteCustomById(Integer id){
        LambdaQueryWrapper<Festival> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Festival::getType,"custom")
                .eq(Festival::getOpenid,SecurityUtil.getUserName())
                .eq(Festival::getId,id);
        festivalMapper.delete(queryWrapper);
    }

}
