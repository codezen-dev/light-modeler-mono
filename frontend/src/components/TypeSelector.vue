<template>
  <div>
    <h3>Type Selector</h3>
    <el-select v-model="selected" placeholder="Select type">
      <el-option
        v-for="item in types"
        :key="item.name"
        :label="item.name"
        :value="item.name" />
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useTypeLibrary } from '@/store/typeLibrary'

const selected = ref('')
const { types } = useTypeLibrary()
</script>
