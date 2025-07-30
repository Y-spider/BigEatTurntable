import request from '@/utils/request'


// 上传文件
export function uploadFile(data){
    return request.post("/file/upload",data)
}