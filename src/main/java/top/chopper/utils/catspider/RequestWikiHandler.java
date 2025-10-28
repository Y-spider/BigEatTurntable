package top.chopper.utils.catspider;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.mapper.AnimalBreedMapper;
import top.chopper.pojo.AnimalBreed;
import top.chopper.utils.BaiduTranslateUtil;

import java.util.List;
import java.util.Map;

/*
   @Author:ROBOT
   @DateTime:2025/10/28 15:42
   @Version:1.0.0
   @Description: 维基百科信息访问
   */
@Component
public class RequestWikiHandler {

    @Autowired
    private AnimalBreedMapper animalBreedMapper;
    @Autowired
    private BaiduTranslateUtil baiduTranslateUtil;
//    public static void main(String[] args) {
//        String url = "https://en.wikipedia.org/wiki/Aegean_cat";
//        String body = HttpRequest.get(url).execute().body();
//        System.out.println(body);
//    }
@Transactional
public  void initCatBreed() {
    String url = "https://api.thecatapi.com/v1/breeds";
    String body = HttpRequest.get(url).execute().body();
    List<Map> breedsList = JSONUtil.toList(body, Map.class);
    for (Map<String, Object> breedData : breedsList) {
        Object imperial = JSONUtil.parseObj(breedData.get("weight")).get("imperial");
        Object metric = JSONUtil.parseObj(breedData.get("weight")).get("metric");
        AnimalBreed breed = new AnimalBreed();
        breed.setType("cat");
        breed.setWeightImperial(imperial.toString());
        breed.setWeightMetric(metric.toString());

        // 设置其它字段
        if (breedData.containsKey("id") && breedData.get("id") != null) {
            breed.setId((String) breedData.get("id"));
        }
        if (breedData.containsKey("name") && breedData.get("name") != null) {
            breed.setName((String) breedData.get("name"));
            breed.setZhName(baiduTranslateUtil.aiTextTranslate(breed.getName()));
        }
        if (breedData.containsKey("cfa_url") && breedData.get("cfa_url") != null) {
            breed.setCfaUrl((String) breedData.get("cfa_url"));
        }
        if (breedData.containsKey("vetstreet_url") && breedData.get("vetstreet_url") != null) {
            breed.setVetstreetUrl((String) breedData.get("vetstreet_url"));
        }
        if (breedData.containsKey("vcahospitals_url") && breedData.get("vcahospitals_url") != null) {
            breed.setVcahospitalsUrl((String) breedData.get("vcahospitals_url"));
        }
        if (breedData.containsKey("temperament") && breedData.get("temperament") != null) {
            breed.setTemperament((String) breedData.get("temperament"));
        }
        if (breedData.containsKey("origin") && breedData.get("origin") != null) {
            breed.setOrigin((String) breedData.get("origin"));
        }
        if (breedData.containsKey("country_codes") && breedData.get("country_codes") != null) {
            breed.setCountryCodes((String) breedData.get("country_codes"));
        }
        if (breedData.containsKey("country_code") && breedData.get("country_code") != null) {
            breed.setCountryCode((String) breedData.get("country_code"));
        }
        if (breedData.containsKey("description") && breedData.get("description") != null) {
            breed.setDescription((String) breedData.get("description"));
            // 中文翻译
            breed.setZhDescription(baiduTranslateUtil.aiTextTranslate((String) breedData.get("description")));
        }
        if (breedData.containsKey("life_span") && breedData.get("life_span") != null) {
            breed.setLifeSpan((String) breedData.get("life_span"));
        }
        if (breedData.containsKey("indoor") && breedData.get("indoor") != null) {
            breed.setIndoor((Integer) breedData.get("indoor") == 1);
        }
        if (breedData.containsKey("lap") && breedData.get("lap") != null) {
            breed.setLap((Integer) breedData.get("lap") == 1);
        }
        if (breedData.containsKey("alt_names") && breedData.get("alt_names") != null) {
            breed.setAltNames((String) breedData.get("alt_names"));
        }
        if (breedData.containsKey("adaptability") && breedData.get("adaptability") != null) {
            breed.setAdaptability((Integer) breedData.get("adaptability"));
        }
        if (breedData.containsKey("affection_level") && breedData.get("affection_level") != null) {
            breed.setAffectionLevel((Integer) breedData.get("affection_level"));
        }
        if (breedData.containsKey("child_friendly") && breedData.get("child_friendly") != null) {
            breed.setChildFriendly((Integer) breedData.get("child_friendly"));
        }
        if (breedData.containsKey("dog_friendly") && breedData.get("dog_friendly") != null) {
            breed.setDogFriendly((Integer) breedData.get("dog_friendly"));
        }
        if (breedData.containsKey("energy_level") && breedData.get("energy_level") != null) {
            breed.setEnergyLevel((Integer) breedData.get("energy_level"));
        }
        if (breedData.containsKey("grooming") && breedData.get("grooming") != null) {
            breed.setGrooming((Integer) breedData.get("grooming"));
        }
        if (breedData.containsKey("health_issues") && breedData.get("health_issues") != null) {
            breed.setHealthIssues((Integer) breedData.get("health_issues"));
        }
        if (breedData.containsKey("intelligence") && breedData.get("intelligence") != null) {
            breed.setIntelligence((Integer) breedData.get("intelligence"));
        }
        if (breedData.containsKey("shedding_level") && breedData.get("shedding_level") != null) {
            breed.setSheddingLevel((Integer) breedData.get("shedding_level"));
        }
        if (breedData.containsKey("social_needs") && breedData.get("social_needs") != null) {
            breed.setSocialNeeds((Integer) breedData.get("social_needs"));
        }
        if (breedData.containsKey("stranger_friendly") && breedData.get("stranger_friendly") != null) {
            breed.setStrangerFriendly((Integer) breedData.get("stranger_friendly"));
        }
        if (breedData.containsKey("vocalisation") && breedData.get("vocalisation") != null) {
            breed.setVocalisation((Integer) breedData.get("vocalisation"));
        }
        if (breedData.containsKey("experimental") && breedData.get("experimental") != null) {
            breed.setExperimental((Integer) breedData.get("experimental") == 1);
        }
        if (breedData.containsKey("hairless") && breedData.get("hairless") != null) {
            breed.setHairless((Integer) breedData.get("hairless") == 1);
        }
        if (breedData.containsKey("natural") && breedData.get("natural") != null) {
            breed.setNatural((Integer) breedData.get("natural") == 1);
        }
        if (breedData.containsKey("rare") && breedData.get("rare") != null) {
            breed.setRare((Integer) breedData.get("rare") == 1);
        }
        if (breedData.containsKey("rex") && breedData.get("rex") != null) {
            breed.setRex((Integer) breedData.get("rex") == 1);
        }
        if (breedData.containsKey("suppressed_tail") && breedData.get("suppressed_tail") != null) {
            breed.setSuppressedTail((Integer) breedData.get("suppressed_tail") == 1);
        }
        if (breedData.containsKey("short_legs") && breedData.get("short_legs") != null) {
            breed.setShortLegs((Integer) breedData.get("short_legs") == 1);
        }
        if (breedData.containsKey("wikipedia_url") && breedData.get("wikipedia_url") != null) {
            breed.setWikipediaUrl((String) breedData.get("wikipedia_url"));
        }
        if (breedData.containsKey("hypoallergenic") && breedData.get("hypoallergenic") != null) {
            breed.setHypoallergenic((Integer) breedData.get("hypoallergenic") == 1);
        }
        if (breedData.containsKey("reference_image_id") && breedData.get("reference_image_id") != null) {
            breed.setReferenceImageId((String) breedData.get("reference_image_id"));
        }

// 插入数据
        animalBreedMapper.insert(breed);

    }
    System.out.println("初始化成功");
}


//    public static void main(String[] args) {
//        String url = "https://en.wikipedia.org/wiki/American_Bobtail";
//        String body = HttpRequest.get(url).execute().body();
//        // 正则匹配 <h2 id="History"> 和 <p> 内容
//        System.out.println(body);
//        Pattern p = Pattern.compile("<h2 id=\\\"([^\\\"]+)\\\">([^<]+)<\\\\/h2>.*?<p>(.*?)<\\\\/p>", Pattern.DOTALL);
//        Matcher m = p.matcher(body);
//        while (m.find()) {
//            // 提取 History 和 p 内容
//            String history = m.group(1);  // "History"
//            String paragraph = m.group(2);  // 段落文本
//
//            // 去除所有 HTML 标签
//            paragraph = paragraph.replaceAll("<[^>]+>", "");  // 去掉所有HTML标签
//
//            System.out.println("label: " + history);
//            System.out.println("content: " + paragraph);
//        }
//    }
}
