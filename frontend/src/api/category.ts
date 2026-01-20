import request from './request'

export interface TechSubCategory {
  code: string
  name: string
  enName: string
  description: string
  icon: string
  count: number
  keywords: string[]
}

export interface TechCategory {
  code: string
  name: string
  enName: string
  description: string
  icon: string
  patentCount: number
  paperCount: number
  projectCount: number
  totalCount: number
  subItems: TechSubCategory[]
}

export interface CategoryData {
  categories: TechCategory[]
}

export const categoryApi = {
  getCategories() {
    return request.get<CategoryData>('/category')
  }
}
