// 上传avatar
const baseUrl = "https://www.sunnygo.chat/turntable/api/"
// const baseUrl = "http://192.168.1.104:16378/"
export function uploadAvatar(tempFilePath) {
	return new Promise((resolve, reject) => {
		const token = uni.getStorageSync("token");
		uni.uploadFile({
			url: baseUrl + "file/avatar",
			filePath: tempFilePath, // ✅ 正确字段
			name: 'file',
			formData: {},
			header: {
				token
			},
			success: (uploadFileRes) => {
				console.log('uploadFileRes', uploadFileRes)
				const data = JSON.parse(uploadFileRes.data || '{}')
				if (data.code === 200) {
					resolve(data)
				} else {
					reject(data.errMsg || '上传失败')
				}
			},
			fail: (err) =>{
				console.log("上传头像失败", err)
				reject(err)
			}
		})

	})
}

// 上传 文件
export function uploadFile(tempFilePath) {
	const token = uni.getStorageSync("token");
	return new Promise((resolve, reject) => {
		uni.uploadFile({
			url: baseUrl + "file/upload",
			filePath: tempFilePath,
			name: 'file',
			formData: {

			},
			header: {
				token
			},
			success: (uploadFileRes) => {
				const data = JSON.parse(uploadFileRes.data || '{}')
				if (data.code == 200) {
					resolve(data);
				} else {
					reject(data.errMsg)
				}
			},
			fail: (err) => {
				console.log("上传文件失败", err)
				reject(err);
			}
		})
	})
}