/* eslint-disable */
import request from '@/utils/request'


// 分页获取菜品申请列表

export function listDishApplyList(queryData){
    return request.post("/apply/dish/list/page",queryData)
}

// 审核菜品申请
export function reviewApplyDish(data){
    return request.post("/apply/dish/review",data)
}

// 删除菜品申请
export function deleteApplyDish(id){
    return request.delete("/apply/dish/delete/" + id)
}