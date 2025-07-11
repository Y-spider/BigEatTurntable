import {httpOFGet,httpOFPost} from "@/request/globalRequest.js"

export  function logInAPI(data){
	return httpOFPost("user/client/login",data)
}