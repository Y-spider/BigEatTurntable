// 记账模块请求API
import {httpOFGet,httpOFPost} from "@/request/globalRequest.js";

// 获取当前用户选择的账本信息
export function getChoosedBillBookAPI(){
	return httpOFGet('book/choose');
}

// 获取当前用户的所有账本
export function listAllBillBookAPI(){
	return httpOFGet('book/list');
}
// 添加账本
export function addBillBookAPI(data){
	return httpOFPost('book/add',data,null,'POST')
}

// 修改选择账单
export function changeCurrentChooseBillBookAPI(data){
	return httpOFPost('book/change',data,null,'PUT');
}

// 删除账单
export function deleteBillBookAPI(id){
	return httpOFPost(`book/delete/${id}`,{},null,'DELETE');
}

// 新增账单记录
export function addBillRecordAPI(data){
	return httpOFPost('book/record/add',data,null,'POST')
}
// 获取账单记录
export function getBillRecordAPI(id){
	return httpOFGet(`book/record/query/${id}`)
}

// 获取月份总结数据
export function getMonthSummaryAPI(time,userId){
	return httpOFGet(`book/record/summary/${time}/${userId}`)
}

//按月分获取到消费记录
export function getMonthBillRecordListAPI(time,userId){
	return httpOFGet(`book/record/list/month/${time}/${userId}`)
}

// 更新账单记录
export function updateBillRecordAPI(data){
	return httpOFPost(`book/record/update`,data,false,'PUT')
}

// 删除账单
export function deleteBillRecordAPI(id){
	return httpOFPost(`book/record/delete/${id}`,null,false,'DELETE')
}

// 获取当前账单的所有用户信息
export function getCurrentBookAllUserAPI(){
	return httpOFGet(`book/share/user`)
}

// 加入共享账本
export function joinShareBillBookAPI(id){
	return httpOFPost(`book/join/${id}`,null,false,'POST')
}

// 合并账本记录
export function mergeBillRecordAPI(formBillBookId,toBillBookId){
	return httpOFPost(`book/record/merge/${formBillBookId}/${toBillBookId}`,null,false,'POST')
}

// 获取搜索列表信息
export function getSearchListAPI(data){
	return httpOFPost(`book/record/search`,data,false,'POST');
}

// 导出账单
export function exportBillRecordAPI(data){
	return httpOFPost(`book/record/export/excel`,data,false,'POST');
}

//输入内容ai解析结果
export function makeBillRecordByDesWithAiAPI(data){
	return httpOFPost(`book/record/add/des`,data,false,'POST')
}

// 统计分析数据拉取接口
export function getStasticDataAPI(data){
	return httpOFPost(`book/record/static`,data,false,'POST')
}

// 获取月份统计信息
export function getSummaryMonthAPI(data){
	return httpOFPost(`book/record/duration/summary`,data,false,'POST');
}