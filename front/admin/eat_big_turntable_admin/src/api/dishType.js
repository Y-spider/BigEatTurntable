import request from '@/utils/request'

// 获取全部菜品类型
export function listAllDishType() {
  return request.get('/dishType/list')
}

// 模糊搜索菜品类型
export function searchDishTypeByName(name) {
  return request.get(`/dishType/like/${encodeURIComponent(name)}`)
}

// 根据id获取菜品类型
export function getDishTypeById(id) {
  return request.get(`/dishType/query/${id}`)
}

// 新增菜品类型
export function addDishType(data) {
  return request.post('/dishType/add', data)
}

// 更新菜品类型
export function updateDishType(data) {
  return request.put('/dishType/update', data)
}

// 删除菜品类型
export function deleteDishType(id) {
  return request.delete(`/dishType/delete/${id}`)
} 