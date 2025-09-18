import axios from 'axios'
import type { Element } from '@/types/element'

export const fetchElements = () => axios.get<Element[]>('/api/elements')

export const createElement = (element: Partial<Element>) =>
    axios.post<Element>('/api/elements', element)

export const updateElementApi = (id: string, element: Partial<Element>) =>
    axios.put(`/api/elements/${id}`, element)

export const deleteElement = (id: string) =>
    axios.delete(`/api/elements/${id}`)

export function getDslById(id: string): Promise<string> {
    return axios.get(`/api/dsl/export/${id}`).then(res => res.data)
}

export function getFullDsl(): Promise<string> {
    return axios.get(`/api/dsl/export`).then(res => res.data)
}

