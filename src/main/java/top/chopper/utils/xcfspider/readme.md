# 这个工具类主要用于对获取下厨房获取菜品制作进行解析爬取 
https://www.xiachufang.com/



菜品制作信息存储格式json格式如下：

{
"id": 123,                // 编辑时有，新增时可不传
"name": "家常沙拉",        // 菜品名称
"desc": "简单美味的家常沙拉", // 菜品描述
"coverUrl":"https://xxxxxx", // 菜品封面url
"ingredients": [ // 用料表
    {
    "category": "蔬菜类",
    "name": "苦菊",
    "amount": "1把"
    },
    {
    "category": "蔬菜类",
    "name": "紫甘蓝",
    "amount": "2片"
    },
    {
    "category": "水果类",
    "name": "莲雾",
    "amount": "1个"
    }
    // ... 其他用料
    ],
    "_steps_": [ // 制作步骤
    {
    "desc": "按自己的喜好准备食材。",
    "imgUrl": "https://xxx.com/step1.jpg"
    },
    {
    "desc": "蔬菜清洗干净。",
    "imgUrl": "https://xxx.com/step2.jpg"
    }
]