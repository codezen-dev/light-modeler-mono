<template>
  <div class="app-container">
    <TopToolbar />

    <el-row type="flex" style="margin-top: 10px">
      <el-col :span="4">
        <StructureTree />
      </el-col>
      <el-col :span="20">
        <CanvasArea />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useModelStore } from '@/store/model'
import TopToolbar from './TopToolbar.vue'
import StructureTree from './StructureTree.vue'
import CanvasArea from './CanvasArea.vue'

const model = useModelStore()

onMounted(() => {
  model.loadElements()
})
</script>

<style scoped>
.app-container {
  padding: 15px;
}
</style>
