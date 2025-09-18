export interface Element {
  id: string
  name: string
  type: string
  owner?: string
  documentation?: string
  modifiers?: string[]
  metadata: Record<string, any>
  children?: Element[]
}
