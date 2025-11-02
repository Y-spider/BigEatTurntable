import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

// 获取猫咪冷知识

export function getCatFactAPI(){
	return httpOFGet("fact/cat")
}

// 查询猫猫种类信息

export function getCatBreedInfoAPI(id){
	return httpOFGet(`breed/query/cat/${id}`)
}