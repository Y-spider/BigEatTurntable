import request from '@/utils/request'

// 分页条件获取转盘信息

export function listPageWithPageQuery(data){
  return request.post("/turntable/list/page",data)
}

// 根据id获取轮盘信息
export function selectById(id){
  return request.get(`/turntable/querySingle/${id}`)
}
// 更新轮盘新
export function updateTurntable(data){
  return request.put("/turntable/update/admin",data)
}
// 删除轮盘
export function deleteTurntableById(id){
  return request.delete(`/turntable/delete/${id}`)
}
// 新增轮盘
export function addTurntable(data){
  return request.post("/turntable/admin/add",data)
}

