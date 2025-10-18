import request from '@/utils/request'

// 分页获取通知
export function listNoticeByPage(data) {
  return request.post('/notice/list/page', data)
}

// 新增通知
export function addNotice(data) {
  return request.post('/notice/add', data)
}

// 修改通知
export function updateNotice(data) {
  return request.put('/notice/update', data)
}

// 获取当前激活通知
export function getActiveNotice() {
  return request.get('/notice/active')
}

// 删除通知
export function deleteNotive(id){
  return request.delete(`/notice/delete/${id}`)
}

// 激活/取消激活通知（直接复用updateNotice，active字段控制）
