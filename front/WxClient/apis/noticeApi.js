import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

/** 获取当前激活公告*/
export function getActiveNoticeAPI(){
	return httpOFGet(`notice/active`)
}