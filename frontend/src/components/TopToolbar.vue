<!-- src/components/TopToolbar.vue -->
<template>
  <div class="toolbar-container">
    <el-row type="flex" justify="space-between" class="toolbar">
      <div></div>
      <div>
        <el-button @click="clearModel">清空建模</el-button>
        <el-button @click="saveModel">保存（待实现）</el-button>
        <el-button @click="exportDsl">导出 DSL</el-button>
      </div>
    </el-row>

    <!-- DSL 弹窗组件 -->
    <DslExportDialog ref="dslDialogRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useModelStore } from '@/store/model'
import { getDslById, getFullDsl } from '@/api/elements'
import DslExportDialog from './DslExportDialog.vue'

const modelStore = useModelStore()
const dslDialogRef = ref()

function clearModel() {
  modelStore.clear()
}

function saveModel() {
  console.log('保存功能待实现')
}

async function exportDsl() {
  const selected = modelStore.selected
  const dsl = selected ? await getDslById(selected.id) : await getFullDsl()
  dslDialogRef.value?.open(dsl)
}
</script>

<style scoped>
.toolbar-container {
  padding: 10px 15px;
  border-bottom: 1px solid #dcdfe6;
  background-color: #f9f9f9;
}
</style>
