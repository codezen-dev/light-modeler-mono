import { defineStore } from 'pinia'

export interface TypeEntry {
  name: string
  description?: string
}

export const useTypeLibrary = defineStore('typeLibrary', {
  state: () => ({
    types: [] as TypeEntry[]
  }),
  actions: {
    loadTypes() {
      // 这里可以替换为从后端加载或静态导入
      this.types = [
        { name: 'PartUsage', description: 'Part element' },
        { name: 'AttributeUsage', description: 'Attribute element' },
        { name: 'StructureDefinition', description: 'Structure element' }
      ]
    }
  }
})
