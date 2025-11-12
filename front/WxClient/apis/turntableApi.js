import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

export function updateTurntableAPI (data) {
	return httpOFPost("turntable/update",data,true,"PUT")
	
}

// h获取当前用户的自定义的转盘信息
export function getUserTurntableInfoAPI(){
	return httpOFGet("turntable/list/user")
}

// 根据id获取转盘详细信息
export function getTurntableDetailAPI(id){
	return httpOFGet(`turntable/querySingle/${id}`)
}

// 获取热门轮盘

export function getHotTurtableAPI(){
	return httpOFGet("turntable/list/hot")
}

export function addTurntableAPI(data){
	return httpOFPost("turntable/client/add",data,false,"POST")
}

export function deleteTurntableByIdAPI(id){
	return httpOFPost(`turntable/delete/${id}`,null,false,"DELETE")
}

// 获取所有系统转盘
export function getAllSystemTurntableAPI(){
	return httpOFGet(`turntable/list/system`)
}

// 修改转盘抽奖限制次数
export function updateTurantableLimitCountAPI(data){
	return httpOFPost(`turntable/client/limit`,data,false,"POST")
}

// 获取转盘二维码
export function getErCodeUrlAPI(id){
	return httpOFGet(`turntable/erCode/${id}`)
}