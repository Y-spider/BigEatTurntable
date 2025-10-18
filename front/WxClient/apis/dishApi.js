import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

/** 分页获取菜品信息*/
export function listDishWithPageAPI(data){
	return httpOFPost("sysDish/list/page",data,false,"POST")
}

/** 随机获取菜品*/
export function listDishRandomAPI(count,dishType){
	return httpOFGet(`sysDish/list/random/${count}/${dishType}`)
}

// 根据菜品id获取制作教程

export function getDishMakeByIdAPI(id){
	return httpOFGet(`make/select/${id}`)
}

// 获取全部菜品类型
export function getAllDishTypeAPI(){
	return httpOFGet("dishType/list")
}