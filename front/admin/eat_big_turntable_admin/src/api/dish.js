/* eslint-disable */
import request from '@/utils/request'

// 分页获取菜品信息

export function listDishByPage(queryData){
    return request.post("/sysDish/list/page",queryData)
}

// 按照id获取菜品信息

export function getDishById(id ){
    return request.get(`/sysDish/select/${id}`)
}

// 删除菜品

export function deleteDishByIds(ids){
    return request.post("/sysDish/delete/batch",ids)
}

// 修改菜品信息
export function updateDish(data){
    return request.put("/sysDish/update",data)
}

// 获取菜品类型

export function getDishType(){
    return request.get("/dishType/list")
}
// 获取菜品制作教程
export function getDishMake(id){
    return  request.get(`/make/select/${id}`)
}
// 修改菜品制作教程
export function updateDishMake(data){
    return request.put("/make/update",data)
}
// 新增菜单制作教程
export function addDishMake(data){
    return request.post("/make/add",data)
}

// 新增菜品附加菜品制作教程url
export function addDishWithMakeUrl(data){
    return request.post("/sysDish/add/additional/makeUrl",data)
}

// 使用搜索添加菜品信息
export function addDishBySearchName(params){
    return request.post(`/make/add/search/${params.searchName}/${params.count}/${params.startPage}/${params.endPage}`)
}

// 使用类型进行搜索
export function addDishBySearchType(params){
    return request.post(`/make/add/type`,params)
}

// 根据菜品分类id和搜索菜品名称获取菜品信息
export function listWithTypeIdAndSearchName(typeId,searchName){
    console.log("searchName",searchName=="")
    let url = `/sysDish/list/typeId/${typeId}/`;
    if(searchName==""){
        url+="all"
    }
    else{
        url+=searchName
    }
    return request.get(url)
}