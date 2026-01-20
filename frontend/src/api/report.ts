import request from './request'

export const reportApi = {
  list(page = 1, size = 10) {
    return request.get('/report/list', { params: { page, size } })
  },
  getById(id: number) {
    return request.get(`/report/${id}`)
  },
  generate(topic: string, requirements: string) {
    return request.post('/report/generate', { topic, requirements })
  },
  save(report: { title: string; content: string }) {
    return request.post('/report/save', report)
  }
}
