// src/store/model.ts
import { defineStore } from 'pinia'
import type { Element } from '@/types/element'
import {fetchElements, createElement, deleteElement, updateElementApi, getDslById, getFullDsl} from '@/api/elements'

export const useModelStore = defineStore('model', {
    state: () => ({
        rootElements: [] as Element[],
        selected: null as Element | null
    }),

    actions: {
        async loadElements() {
            const res = await fetchElements()
            this.rootElements = res.data
        },

        async addRootElement(name: string, type = 'StructureDefinition') {
            const res = await createElement({ name, type })
            this.rootElements.push(res.data)
        },

        async addChildElement(parent: Element, name: string, type = 'PartUsage') {
            const res = await createElement({
                name,
                type,
                owner: parent.id
            })
            if (!parent.children) parent.children = []
            parent.children.push(res.data)
        },

        async updateElement(updated: Element) {
            await updateElementApi(updated.id, updated)

            const applyUpdate = (elements: Element[]) => {
                for (const el of elements) {
                    if (el.id === updated.id) {
                        Object.assign(el, updated)
                        return
                    }
                    if (el.children) applyUpdate(el.children)
                }
            }
            applyUpdate(this.rootElements)
        }
        ,

        async deleteElementById(id: string) {
            await deleteElement(id)
            const removeRecursively = (elements: Element[]): Element[] =>
                elements.filter(el => {
                    if (el.id === id) return false
                    if (el.children) el.children = removeRecursively(el.children)
                    return true
                })
            this.rootElements = removeRecursively(this.rootElements)
        },

        selectElement(el: Element) {
            this.selected = el
        },

        clear() {
            this.rootElements = []
            this.selected = null
        },

    // ✅ 添加方法，封装后端 DSL 导出逻辑
    async exportDslBySelection(): Promise<string> {
        if (this.selected?.id) {
            return await getDslById(this.selected.id)
        } else {
            return await getFullDsl()
        }
    }
    }

})
