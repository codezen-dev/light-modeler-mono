<!-- src/components/DslExportDialog.vue -->
<template>
  <el-dialog v-model="visible" title="DSL 导出结果" width="60%">
    <el-input
        type="textarea"
        v-model="dslContent"
        :rows="20"
        readonly
    />
    <template #footer>
      <el-button @click="copyToClipboard">复制</el-button>
      <el-button @click="downloadDsl">下载为文件</el-button>
      <el-button type="primary" @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {ElMessage} from "element-plus";

const visible = ref(false)
const dslContent = ref('')

function open(content: string) {
  dslContent.value = content
  visible.value = true
}

function copyToClipboard() {
  navigator.clipboard.writeText(dslContent.value).then(() => {
    ElMessage.success('已复制到剪贴板')
  })
}

function downloadDsl() {
  const blob = new Blob([dslContent.value], { type: 'text/plain' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = 'model.dsl'
  link.click()
}

defineExpose({ open })
</script>
