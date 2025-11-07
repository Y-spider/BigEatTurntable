import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

// 获取节日信息

export function getFestivalAPI(type){
	return httpOFGet(`festival/list/${type}`)
}

export function getFestivalDesAPI(id){
	return httpOFGet(`festival/query/${id}`)
}

export function saveCustomFestivalAPI(data){
	return httpOFPost(`festival/custom`,data,false,"POST")	
}

export function deleteByIdAPI(id){
	return httpOFPost(`festival/delete/${id}`,null,false,"DELETE")
}
