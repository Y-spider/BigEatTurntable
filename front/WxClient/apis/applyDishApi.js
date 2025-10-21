import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

export function addApplyDishAPI(data){
	return httpOFPost("apply/dish/add",data,false,"POST")
}