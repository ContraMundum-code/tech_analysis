import request from './request'

export const adminApi = {
  // 直接导入（大文件推荐）
  importPatent(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/import/patent', formData, {
      timeout: 600000  // 10分钟超时，支持大文件
    })
  },
  importProject(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/import/project', formData, {
      timeout: 600000
    })
  },
  importPaper(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/import/paper', formData, {
      timeout: 600000
    })
  },

  // 预览+确认模式（小文件）
  previewPatent(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/import/patent/preview', formData)
  },
  confirmPatent(patents: any[]) {
    return request.post('/admin/import/patent/confirm', patents)
  },
  previewProject(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/import/project/preview', formData)
  },
  confirmProject(projects: any[]) {
    return request.post('/admin/import/project/confirm', projects)
  },
  previewPaper(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/import/paper/preview', formData)
  },
  confirmPaper(papers: any[]) {
    return request.post('/admin/import/paper/confirm', papers)
  }
}
