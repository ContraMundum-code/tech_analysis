import request from './request'

export const analysisApi = {
  getTechMap(dataType = 'patent', categoryCode?: string) {
    return request.get('/analysis/techmap', { params: { dataType, categoryCode } })
  },
  getTrend(country?: string, startYear?: number, endYear?: number, categoryCode?: string) {
    return request.get('/analysis/trend', { params: { country, startYear, endYear, categoryCode } })
  },
  getCompetition(categoryCode?: string) {
    return request.get('/analysis/competition', { params: { categoryCode } })
  },
  getComparison(countryA: string, countryB: string) {
    return request.get('/analysis/comparison', { params: { countryA, countryB } })
  },
  getNetwork(country?: string, categoryCode?: string) {
    return request.get('/analysis/network', { params: { country, categoryCode } })
  },
  getHotTech(categoryCode?: string) {
    return request.get('/analysis/hottech', { params: { categoryCode } })
  },
  getMaturity(categoryCode?: string) {
    return request.get('/analysis/maturity', { params: { categoryCode } })
  },
  getRegion(categoryCode?: string) {
    return request.get('/analysis/region', { params: { categoryCode } })
  }
}
