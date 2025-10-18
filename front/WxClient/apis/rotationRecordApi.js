import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

// 分页获取转动记录
export function listWithPageAPI(pageIndex,pageSize){
	return httpOFGet(`record/list/page/${pageIndex}/${pageSize}`)
}

// 获取用户转动总数
export function selectRecordCountAPI(){
	return httpOFGet("record/list/count")
}

// 添加转动记录
export function saveRecordAPI(data){
	return httpOFPost("record/add",data,false,"POST")
}

// 获取单独转盘的转动记录
export function listSingleTurntableRecordAPI(id){
	return httpOFGet(`record/list/share/${id}`)
}