// 上传avatar
export function uploadAvatar(tempFilePath){
	return new Promise((resolve,reject)=>{
		const token = uni.getStorageSync("token");
		uni.uploadFile({
		  url: "http://127.0.0.1:16378/file/avatar",
		  filePath: tempFilePath, // ✅ 正确字段
		  name: 'file',
		  formData: {},
		  header:{
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
		  fail: reject
		})

	})
}

// 上传 文件
export function uploadFile(tempFilePath){
	const token = uni.getStorageSync("token");
	return new Promise((resolve,reject)=>{
		uni.uploadFile({
			url:"http://127.0.0.1:16378/file/upload",
			filePath: tempFilePath,
			name: 'file',
			formData:{
				
			},
			header:{
				token
			},
			success: (uploadFileRes) => {
				if(uploadFileRes.data.code == 200){
					resolve(uploadFileRes.data);
				}else{
					reject(uploadFileRes.data.errMsg)
				}
			},
			fail:(err)=>{
				reject(err);
			}
		})
	})
}