package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/*
   @Author:ROBOT
   @DateTime:2025/10/5 18:54
   @Version:1.0.0
   @Description: cat品种信息
   */
@Data
@TableName("animal_breeds")  // 表名
public class AnimalBreed {
    @TableId
    private String id;  // 品种唯一标识符

    @TableField("`name`")
    private String name;  // 品种名称

    @TableField("`weight_imperial`")
    private String weightImperial;  // 体重（英制）

    @TableField("`weight_metric`")
    private String weightMetric;  // 体重（公制）

    @TableField("`cfa_url`")
    private String cfaUrl;  // CFA 网站链接

    @TableField("`vetstreet_url`")
    private String vetstreetUrl;  // VetStreet 网站链接

    @TableField("`vcahospitals_url`")
    private String vcahospitalsUrl;  // VCA 网站链接

    @TableField("`temperament`")
    private String temperament;  // 性格特征

    @TableField("`origin`")
    private String origin;  // 品种起源地

    @TableField("`country_codes`")
    private String countryCodes;  // 国家代码

    @TableField("`country_code`")
    private String countryCode;  // 国家代码

    @TableField("`description`")
    private String description;  // 品种描述

    @TableField("`zh_description`")
    private String zhDescription;  // 品种描述(中文)

    @TableField("`life_span`")
    private String lifeSpan;  // 寿命范围

    @TableField("`indoor`")
    private boolean indoor;  // 是否适合室内养殖

    @TableField("`lap`")
    private boolean lap;  // 是否喜欢坐在膝盖上

    @TableField("`alt_names`")
    private String altNames;  // 别名

    @TableField("`adaptability`")
    private int adaptability;  // 适应性评分

    @TableField("`affection_level`")
    private int affectionLevel;  // 亲和力评分

    @TableField("`child_friendly`")
    private int childFriendly;  // 与儿童友好度评分

    @TableField("`dog_friendly`")
    private int dogFriendly;  // 与狗友好度评分

    @TableField("`energy_level`")
    private int energyLevel;  // 活力水平评分

    @TableField("`grooming`")
    private int grooming;  // 打理需求评分

    @TableField("`health_issues`")
    private int healthIssues;  // 健康问题评分

    @TableField("`intelligence`")
    private int intelligence;  // 智力评分

    @TableField("`shedding_level`")
    private int sheddingLevel;  // 掉毛程度评分

    @TableField("`social_needs`")
    private int socialNeeds;  // 社交需求评分

    @TableField("`stranger_friendly`")
    private int strangerFriendly;  // 与陌生人友好度评分

    @TableField("`vocalisation`")
    private int vocalisation;  // 发声频率评分

    @TableField("`experimental`")
    private boolean experimental;  // 是否为实验性品种

    @TableField("`hairless`")
    private boolean hairless;  // 是否无毛

    @TableField("`natural`")  // 加上反引号避免SQL关键字冲突
    private boolean natural;  // 是否为天然品种

    @TableField("`rare`")  // 加上反引号避免SQL关键字冲突
    private boolean rare;  // 是否稀有

    @TableField("`rex`")  // 加上反引号避免SQL关键字冲突
    private boolean rex;  // 是否雷克斯品种

    @TableField("`suppressed_tail`")
    private boolean suppressedTail;  // 是否抑制尾巴

    @TableField("`short_legs`")
    private boolean shortLegs;  // 是否为短腿品种

    @TableField("`wikipedia_url`")
    private String wikipediaUrl;  // 维基百科链接

    @TableField("`hypoallergenic`")
    private boolean hypoallergenic;  // 是否低过敏性品种

    @TableField("`reference_image_id`")
    private String referenceImageId;  // 参考图片ID

    @TableField("`type`")
    private String type;  // 种类
    @TableField("`zh_name`")
    private String zhName;  // 名称中文

    @TableField("`breed_group`")
    private String breedGroup;  // 分组


    @TableField("`bred_for`")
    private String bredFor;  // 职业


    @TableField("`height_metric`")
    private String heightMetric;  // 高度米尺

    @TableField("`height_imperial`")
    private String heightImperial;  // 盖度英尺

    @TableField("`id_dog`")
    private Integer idDog;  // dogId

}
