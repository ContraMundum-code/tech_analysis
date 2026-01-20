import request from './request'

export const authApi = {
  login(username: string, password: string) {
    return request.post('/auth/login', { username, password })
  },
  register(username: string, password: string, email: string) {
    return request.post('/auth/register', { username, password, email })
  },
  getProfile() {
    return request.get('/auth/profile')
  }
}
