import request from './request'

export interface SearchItem {
  id: number
  title: string
  type: 'patent' | 'paper' | 'project'
  country: string
  year: number
  keywords: string
}

export interface SearchResult {
  patents: SearchItem[]
  papers: SearchItem[]
  projects: SearchItem[]
  totalCount: number
}

export const searchApi = {
  search(keyword: string) {
    return request.get<SearchResult>('/search', { params: { keyword } })
  }
}
