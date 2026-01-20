import request from './request'

export interface SystemConfig {
  apiUrl: string
  apiKey: string
  model: string
}

export function getSystemConfig() {
  return request.get<SystemConfig>('/admin/config')
}

export function saveSystemConfig(config: SystemConfig) {
  return request.post('/admin/config', config)
}
