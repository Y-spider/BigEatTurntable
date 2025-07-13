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