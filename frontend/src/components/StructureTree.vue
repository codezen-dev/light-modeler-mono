<template>
  <el-card class="full-height-card">
    <template #header>
      <div class="tree-header" @contextmenu.prevent="onRightClickTitle">系统结构</div>

    </template>

    <el-tree
        ref="treeRef"
        node-key="id"
        :data="treeData"
        :props="defaultProps"
        lazy
        highlight-current
        :load="loadNode"
        @node-click="onSelect"
        @node-contextmenu="onRightClick"
    />

    <el-divider />
    <ElementEditor v-if="model.selected" class="mt-4" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useModelStore } from '@/store/model'
import ElementEditor from './ElementEditor.vue'
import axios from 'axios'
import { ElMessageBox } from 'element-plus'

const model = useModelStore()
const treeRef = ref()
const treeData = ref<any[]>([])

const defaultProps = {
  children: 'children',
  label: 'name',
  isLeaf: 'isLeaf'
}
const onRightClickTitle = async (event: MouseEvent) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入结构名称', '添加一级结构', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    const res = await axios.post('/api/elements', {
      name: value,
      type: 'StructureDefinition',
      owner: null
    })
    treeData.value.push({ ...res.data, isLeaf: false })
  } catch {
    // 用户取消
  }
}


const loadNode = async (node: any, resolve: (children: any[]) => void) => {
  if (node.level === 0) {
    const res = await axios.get('/api/elements/root')
    resolve(res.data.map((item: any) => ({ ...item, isLeaf: false })))
  } else {
    const parentId = node.data.id
    const res = await axios.get(`/api/elements/children/${parentId}`)
    resolve(res.data.map((item: any) => ({ ...item, isLeaf: false })))
  }
}

const onSelect = async (node: Element) => {
  const res = await axios.get(`/api/elements/${node.id}`)
  model.selectElement(res.data)
}


const onRightClick = async (event: MouseEvent, data: any, node: any) => {
  event.preventDefault()
  const ownerId = node?.id ?? null
  try {
    const { value } = await ElMessageBox.prompt(
        ownerId ? '请输入子结构名称' : '请输入结构名称',
        '添加结构',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }
    )
    const res = await axios.post('/api/elements', {
      name: value,
      type: ownerId ? 'PartUsage' : 'StructureDefinition',
      owner: ownerId
    })

    if (ownerId) {
      node.children = node.children || []
      node.children.push({ ...res.data, isLeaf: false })
    } else {
      treeData.value.push({ ...res.data, isLeaf: false })
    }
  } catch {
    // 用户取消输入
  }
}

onMounted(async () => {
  const res = await axios.get('/api/elements/root')
  treeData.value = res.data.map((item: any) => ({ ...item, isLeaf: false }))
})
</script>

<style scoped>
.tree-header {
  font-weight: bold;
  font-size: 16px;
  padding: 6px 0;
}
</style>
