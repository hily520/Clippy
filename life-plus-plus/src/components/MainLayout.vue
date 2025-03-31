<template>
  <div class="main-layout">
    <!-- ChatBox 组件，使用 v-show 控制显示/隐藏 -->
    <v-dialog
      v-model="showChat"
      fullscreen
      :retain-focus="false"
      :persistent="false"
      transition="dialog-bottom-transition"
      class="chat-dialog"
    >
      <ChatBox 
        :messages="messages"
        @close="showChat = false"
        @sendMessage="handleChatMessage"
      />
    </v-dialog>

    <!-- RobotAssistant 组件 -->
    <RobotAssistant @openChat="handleOpenChat" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ChatBox from './ChatBox.vue'
import RobotAssistant from './RobotAssistant.vue'

const showChat = ref(false)
const messages = ref([
  { text: '你好！我是AI助手，有什么我可以帮你的吗？', isUser: false },
])

onMounted(() => {
  console.log('MainLayout 组件已挂载')
})

function handleOpenChat(message) {
  console.log('收到openChat事件:', message)
  showChat.value = true
  console.log('showChat值已设置为:', showChat.value)
  
  if (message && message.trim()) {
    messages.value.push({ text: message, isUser: true })
    // 模拟机器人回复
    setTimeout(() => {
      messages.value.push({
        text: '这是一个模拟的回复消息。',
        isUser: false
      })
      console.log('机器人回复已添加')
    }, 1000)
  }
}

function handleChatMessage(message) {
  if (message && message.trim()) {
    messages.value.push({ text: message, isUser: true })
    // 模拟机器人回复
    setTimeout(() => {
      messages.value.push({
        text: '这是一个模拟的回复消息。',
        isUser: false
      })
    }, 1000)
  }
}
</script>

<style scoped>
.main-layout {
  width: 100%;
  height: 100%;
}

:deep(.chat-dialog) {
  background: white;
}

:deep(.dialog-bottom-transition-enter-active),
:deep(.dialog-bottom-transition-leave-active) {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.dialog-bottom-transition-enter-from),
:deep(.dialog-bottom-transition-leave-to) {
  transform: translateY(100%);
  opacity: 0;
}

:deep(.dialog-bottom-transition-enter-to),
:deep(.dialog-bottom-transition-leave-from) {
  transform: translateY(0);
  opacity: 1;
}

:deep(.v-overlay__content) {
  width: 100% !important;
  height: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
}
</style> 