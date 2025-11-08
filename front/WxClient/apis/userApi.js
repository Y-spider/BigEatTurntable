import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

export  function logInAPI(data){
	return httpOFPost("user/client/login",data)
}

export function getOpenidAPI(){
	return httpOFGet('user/get/userId',false)
}

export function getUserInfoAPI(){
	return httpOFGet('user',false)
}

export function updateUserInfoAPI(data){
	return httpOFPost("user/client/update",data,false,"POST")
}

