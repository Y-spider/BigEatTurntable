// 菜品收藏相关API
import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

export function collectionAPI(id){
	return httpOFGet(`collection/add?dishId=${id}`);
}


export function cancelAPI(id){
	return httpOFGet(`collection/cancel?dishId=${id}`);
}

export function queryAPI(id){
	return httpOFGet(`collection/query?dishId=${id}`);
}

export function listAPI(queryParams){
		return httpOFPost(`collection/list`,queryParams,false,'POST')
}