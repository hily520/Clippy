<template>
  <div class="chat-container">
    <!-- 顶部栏 -->
    <div class="chat-header">
      <div class="logo">
        <img src="@/assets/logo.svg" alt="Logo" class="logo-icon" />
        <img src="@/assets/logo-text.svg" alt="Life++" class="logo-text" />
      </div>
      <div class="header-actions">
        <v-btn icon variant="text" color="primary">
          <v-icon>mdi-share</v-icon>
        </v-btn>
        <v-btn icon variant="text" color="primary">
          <v-icon>mdi-download</v-icon>
        </v-btn>
        <v-btn icon variant="text" color="error" @click="$emit('close')">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </div>
    </div>

    <!-- 聊天内容区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(message, index) in props.messages" :key="index" 
           :class="['message', message.isUser ? 'user-message' : 'bot-message']">
        <div class="message-content">
          <div class="message-text">{{ message.text }}</div>
          <div v-if="!message.isUser" class="message-actions">
            <v-btn icon variant="text" size="small" color="primary">
              <v-icon>mdi-content-copy</v-icon>
            </v-btn>
            <v-btn icon variant="text" size="small" color="success">
              <v-icon>mdi-thumb-up</v-icon>
            </v-btn>
            <v-btn icon variant="text" size="small" color="error">
              <v-icon>mdi-thumb-down</v-icon>
            </v-btn>
            <v-btn icon variant="text" size="small" color="warning">
              <v-icon>mdi-refresh</v-icon>
            </v-btn>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="chat-input">
      <v-text-field
        v-model="inputMessage"
        placeholder="输入消息..."
        variant="outlined"
        hide-details
        class="message-field"
        bg-color="white"
        @keyup.enter="sendMessage"
      >
        <template v-slot:prepend>
          <v-btn icon variant="text" color="primary">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </template>
        <template v-slot:append>
          <v-btn icon variant="text" color="primary">
            <v-icon>mdi-microphone</v-icon>
          </v-btn>
          <v-btn icon variant="text" color="primary">
            <v-icon>mdi-paperclip</v-icon>
          </v-btn>
        </template>
      </v-text-field>
      <v-btn
        icon
        color="primary"
        size="large"
        class="send-button"
        @click="sendMessage"
      >
        <v-icon>mdi-send</v-icon>
      </v-btn>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'

const emit = defineEmits(['close', 'sendMessage'])
const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  }
})

const inputMessage = ref('')
const messagesContainer = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const sendMessage = () => {
  if (!inputMessage.value.trim()) return
  
  emit('sendMessage', inputMessage.value)
  inputMessage.value = ''
  scrollToBottom()
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background-color: white;
  border-bottom: 1px solid #e0e0e0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  height: 32px;
}

.logo-text {
  height: 24px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.message {
  max-width: 80%;
  display: flex;
  flex-direction: column;
}

.user-message {
  align-self: flex-end;
}

.bot-message {
  align-self: flex-start;
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  background-color: white;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.user-message .message-content {
  background-color: #e3f2fd;
  color: rgba(0, 0, 0, 0.87);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-text {
  white-space: pre-wrap;
  line-height: 1.5;
}

.message-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #e0e0e0;
}

.chat-input {
  padding: 16px;
  background-color: white;
  border-top: 1px solid #e0e0e0;
  display: flex;
  gap: 16px;
  align-items: center;
}

.message-field {
  flex: 1;
  border-radius: 24px;
}

.message-field :deep(.v-field__outline__start) {
  border-radius: 24px 0 0 24px !important;
}

.message-field :deep(.v-field__outline__end) {
  border-radius: 0 24px 24px 0 !important;
}

.send-button {
  width: 48px !important;
  height: 48px !important;
  border-radius: 50% !important;
  background-color: var(--v-theme-primary) !important;
  color: white !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.send-button .v-icon {
  color: white !important;
  font-size: 24px !important;
  margin: 0 !important;
}

.send-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.send-button:active {
  transform: translateY(1px);
}
</style>
