import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

// 获取猫咪冷知识

export function getDogFactAPI(){
	return httpOFGet("fact/dog")
}

// 查询猫猫种类信息

export function getDogBreedInfoAPI(id){
	return httpOFGet(`breed/query/dog/${id}`)
}