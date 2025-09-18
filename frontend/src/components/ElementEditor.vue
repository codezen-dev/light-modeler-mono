<template>
  <el-card shadow="never" v-if="selected">
    <template #header>
      <div class="flex justify-between items-center">
        <span>属性编辑器</span>
        <el-button size="small" type="primary" @click="save">保存</el-button>
      </div>
    </template>

    <el-form label-width="100px" :model="local">
      <el-form-item label="名称">
        <el-input v-model="local.name" />
      </el-form-item>

      <el-form-item label="类型">
        <el-input v-model="local.metadata.type" placeholder="如：Integer 或 Signal" />
      </el-form-item>

      <el-form-item label="多重性">
        <el-input v-model="local.metadata.multiplicity" placeholder="如：1..1, 0..*" />
      </el-form-item>

      <el-form-item label="默认值">
        <el-input v-model="local.metadata.defaultValue" placeholder="如：100, true" />
      </el-form-item>

      <el-form-item label="方向">
        <el-select v-model="local.metadata.direction" placeholder="请选择">
          <el-option label="in" value="in" />
          <el-option label="out" value="out" />
          <el-option label="inout" value="inout" />
        </el-select>
      </el-form-item>

      <el-form-item label="修饰符">
        <el-checkbox-group v-model="local.metadata.modifiers">
          <el-checkbox label="ordered" />
          <el-checkbox label="public" />
          <el-checkbox label="private" />
          <el-checkbox label="protected" />
          <el-checkbox label="readonly" />
        </el-checkbox-group>
      </el-form-item>

      <el-form-item label="说明">
        <el-input v-model="local.documentation" type="textarea" rows="2" />
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { watch, reactive } from 'vue'
import { useModelStore } from '@/store/model'
import type { Element } from '@/types/element'

const model = useModelStore()
const selected = model.selected
const local = reactive<Element>({
  id: '',
  name: '',
  type: '',
  documentation: '',
  modifiers: [],
  metadata: {},
  owner: undefined,
  children: []
})

watch(
    () => model.selected,
    (newVal) => {
      if (newVal) {
        Object.assign(local, JSON.parse(JSON.stringify(newVal))) // 深拷贝
        if (!local.metadata) local.metadata = {}
        if (!local.metadata.modifiers) local.metadata.modifiers = []
      }
    },
    { immediate: true }
)

function save() {
  model.updateElement(local)
}
</script>
