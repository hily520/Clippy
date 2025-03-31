<template>
  <div 
    class="robot-container"
    :style="{ top: position.y + 'px', left: position.x + 'px' }"
    @mousedown.stop="handleMouseDown"
    @mousemove.prevent="handleMouseMove"
    @mouseup.prevent="handleMouseUp"
    @click.stop="toggleMenu"
    @dragenter.prevent="isDraggingOver = true"
    @dragleave.prevent="isDraggingOver = false"
    @dragover.prevent
    @drop.prevent="handleDrop"
  >
    <div class="robot-content">
      <canvas ref="canvas" width="150" height="150"></canvas>
      
      <transition name="fade">
        <div v-show="showMenu" class="menu-ring">
          <v-btn
            icon
            variant="text"
            class="menu-item"
            color="primary"
            size="small"
            @click.stop="connectWallet"
          >
            <v-icon size="small">mdi-wallet</v-icon>
            <span class="button-text">连接钱包</span>
          </v-btn>
          <v-btn
            icon
            variant="text"
            class="menu-item"
            color="error"
            size="small"
            @click.stop="showMenu = false"
          >
            <v-icon size="small">mdi-close</v-icon>
            <span class="button-text">关闭</span>
          </v-btn>
          <v-btn
            icon
            variant="text"
            class="menu-item"
            color="info"
            size="small"
            @click.stop="showInput = true"
          >
            <v-icon size="small">mdi-message-text</v-icon>
            <span class="button-text">与我交流</span>
          </v-btn>
        </div>
      </transition>
    </div>

    <transition name="slide">
      <div v-show="showInput" class="message-input">
        <v-text-field
          v-model="message"
          @keyup.enter="handleSendMessage"
          placeholder="输入消息..."
          hide-details
          density="compact"
          variant="outlined"
          class="message-field"
          bg-color="white"
        >
          <template v-slot:append>
            <v-btn
              icon
              color="primary"
              size="small"
              @click="handleSendMessage"
              class="send-button"
            >
              <v-icon>mdi-send</v-icon>
            </v-btn>
          </template>
        </v-text-field>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watchEffect } from 'vue'
import { ethers } from 'ethers'
import axios from 'axios'

const canvas = ref(null)
const position = reactive({ x: window.innerWidth - 200, y: window.innerHeight - 200 })
const showMenu = ref(false)
const showInput = ref(false)
const message = ref('')
const isDragging = ref(false)
const isDraggingOver = ref(false)
const isUploading = ref(false)
let ctx = null
let animationFrame = null
let eyeOffset = 0
let glowColor = '#00ff00'

// 眼睛跟踪参数
const eyePos = reactive({ x: 75, y: 75 })
const targetEyePos = reactive({ x: 75, y: 75 })

// 拖拽相关
let dragStartPos = { x: 0, y: 0 }
let isMouseDown = false
let dragTimeout = null

const emit = defineEmits(['openChat'])

onMounted(() => {
  ctx = canvas.value.getContext('2d')
  startAnimation()
  
  window.addEventListener('mousemove', updateEyePosition)
  window.addEventListener('mouseup', () => {
    isMouseDown = false
    clearTimeout(dragTimeout)
  })
  // 在onMounted中添加：
  const savedPos = localStorage.getItem('robotPosition')
    if (savedPos) {
  Object.assign(position, JSON.parse(savedPos))
  } else {
    position.x = window.innerWidth - 200
    position.y = window.innerHeight - 200
  }

})

// 动画循环
function startAnimation() {
  drawRobot()
  animationFrame = requestAnimationFrame(startAnimation)
}

function drawRobot() {
  ctx.clearRect(0, 0, 150, 150)
  
  // 绘制发光外圈
  ctx.beginPath()
  ctx.arc(75, 75, 60, 0, Math.PI * 2)
  ctx.shadowColor = isDraggingOver.value ? glowColor : 'transparent'
  ctx.shadowBlur = 20
  ctx.strokeStyle = '#ffffff'
  ctx.lineWidth = 4
  ctx.stroke()
  
  // 绘制身体
  ctx.beginPath()
  ctx.arc(75, 75, 50, 0, Math.PI * 2)
  ctx.fillStyle = '#3f51b5'
  ctx.fill()
  
  // 绘制眼睛
  drawEye(60, 60)
  drawEye(90, 60)
  
  // 绘制嘴巴
  ctx.beginPath()
  ctx.moveTo(55, 90)
  ctx.quadraticCurveTo(75, 100, 95, 90)
  ctx.strokeStyle = '#ffffff'
  ctx.lineWidth = 3
  ctx.stroke()
}

function drawEye(x, y) {
  ctx.save()
  ctx.translate(x, y)

  // 眼白
  ctx.beginPath()
  ctx.arc(0, 0, 10, 0, Math.PI * 2)
  ctx.fillStyle = '#ffffff'
  ctx.fill()

  // 瞳孔
  ctx.beginPath()
  const pupilX = (targetEyePos.x - x) / 8
  const pupilY = (targetEyePos.y - y) / 8
  const maxOffset = 5
  const distance = Math.sqrt(pupilX * pupilX + pupilY * pupilY)
  
  if (distance > maxOffset) {
    const scale = maxOffset / distance
    ctx.arc(
      pupilX * scale,
      pupilY * scale,
      5, 0, Math.PI * 2
    )
  } else {
    ctx.arc(pupilX, pupilY, 5, 0, Math.PI * 2)
  }
  
  ctx.fillStyle = '#000000'
  ctx.fill()
  
  ctx.restore()
}

function updateEyePosition(e) {
  targetEyePos.x = e.clientX - position.x
  targetEyePos.y = e.clientY - position.y
  
  // 平滑移动
  eyePos.x += (targetEyePos.x - eyePos.x) * 0.1
  eyePos.y += (targetEyePos.y - eyePos.y) * 0.1
}

function handleMouseDown(e) {
  // 只响应左键点击
  if (e.button !== 0) return
  
  isDragging.value = true
  dragStartPos = {
    x: e.clientX - position.x,
    y: e.clientY - position.y
  }
}

function handleMouseMove(e) {
  if (!isDragging.value) {
    updateEyePosition(e)
    return
  }
  
  position.x = e.clientX - dragStartPos.x
  position.y = e.clientY - dragStartPos.y
  
  // 添加边界限制
  const maxX = window.innerWidth - 100
  const maxY = window.innerHeight - 100
  position.x = Math.min(maxX, Math.max(0, position.x))
  position.y = Math.min(maxY, Math.max(0, position.y))
}

function handleMouseUp() {
  if (isDragging.value) {
    isDragging.value = false
    // 保存位置到localStorage
    localStorage.setItem('robotPosition', JSON.stringify(position))
  }
}

async function handleDrop(e) {
  isDraggingOver.value = false
  isUploading.value = true
  
  // 模拟上传
  const files = e.dataTransfer.files
  await new Promise(resolve => setTimeout(resolve, 2000))
  isUploading.value = false
}

watchEffect(() => {
  if (isUploading.value) {
    const interval = setInterval(() => {
      glowColor = glowColor === '#00ff00' ? '#ff0000' : '#00ff00'
    }, 500)
    return () => clearInterval(interval)
  }
})

function toggleMenu() {
  if (!isDragging.value) {
    showMenu.value = !showMenu.value
    // 如果打开菜单，添加点击其他区域关闭菜单的事件
    if (showMenu.value) {
      setTimeout(() => {
        window.addEventListener('click', closeMenu)
      }, 0)
    }
  }
}

function closeMenu() {
  showMenu.value = false
  window.removeEventListener('click', closeMenu)
}

async function connectWallet() {
  try {
    if (!window.ethereum) {
      console.error('请安装 MetaMask!')
      return
    }

    // 请求连接钱包
    const accounts = await window.ethereum.request({ 
      method: 'eth_requestAccounts' 
    })
    
    if (!accounts || accounts.length === 0) {
      console.error('未能获取钱包账户')
      return
    }

    // 创建 provider 和 signer
    const provider = new ethers.BrowserProvider(window.ethereum)
    const signer = await provider.getSigner()
    
    // 签名消息
    const message = 'Login'
    const signature = await signer.signMessage(message)
    
    console.log('钱包地址:', accounts[0])
    console.log('签名结果:', signature)
    
    // 使用 axios 发送请求到后端
    try {
      const response = await axios.post('http://192.168.82.23:8080/api/submit_question', {
        address: accounts[0],
        question: "",
        sign: signature
      })
      
      console.log('后端返回数据:', response.data)
      
      return {
        address: accounts[0],
        signature,
        serverResponse: response.data
      }
    } catch (axiosError) {
      console.error('后端请求失败:', axiosError.response?.data || axiosError.message)
      throw axiosError
    }
  } catch (error) {
    console.error('连接钱包失败:', error)
    throw error
  }
}

function handleSendMessage() {
  if (!message.value.trim()) return
  
  console.log('发送消息:', message.value)
  // 触发事件，传递消息内容
  emit('openChat', message.value)
  console.log('事件已触发')
  showInput.value = false
  message.value = ''
}
</script>

<style scoped>
.robot-container {
  position: fixed;
  cursor: pointer;
  touch-action: none;
  user-select: none;
  transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  will-change: transform;
  z-index: 1000;
  display: flex;
  align-items: center;
  flex-direction: row-reverse;
  gap: 16px;
}

.robot-container:active {
  filter: brightness(1.1);
  transform: scale(1.05);
}

.robot-content {
  position: relative;
  width: 150px;
  height: 150px;
}

.menu-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  pointer-events: none;
  width: 150px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-item {
  position: absolute;
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: white !important;
  pointer-events: auto;
  width: 40px !important;
  height: 40px !important;
  min-width: 0 !important;
  padding: 0 !important;
  transform-origin: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-radius: 20px !important;
  overflow: visible;
  white-space: nowrap;
  display: inline-flex !important;
  align-items: center;
  justify-content: center !important;
  will-change: transform, width, box-shadow;
}

.button-text {
  position: absolute;
  opacity: 0;
  padding: 0;
  font-size: 14px;
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  pointer-events: none;
  color: inherit;
  white-space: nowrap;
  display: inline-block;
  transform: translateX(-10px);
  visibility: hidden;
  left: 40px;
  will-change: transform, opacity;
}

.menu-item:hover {
  width: auto !important;
  min-width: 120px !important;
  padding: 0 16px !important;
  box-shadow: 0 4px 15px var(--glow-color) !important;
}

.menu-item:hover .button-text {
  opacity: 1;
  transform: translateX(0);
  visibility: visible;
}

.menu-item .v-icon {
  font-size: 18px !important;
  color: inherit;
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  will-change: transform, left;
}

.menu-item:hover .v-icon {
  left: 20px;
  transform: translateX(0);
}

.menu-item:nth-child(1) {
  transform: rotate(0deg) translate(75px) rotate(0deg);
  --glow-color: rgba(33, 150, 243, 0.6);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.menu-item:nth-child(2) {
  transform: rotate(120deg) translate(75px) rotate(-120deg);
  --glow-color: rgba(244, 67, 54, 0.6);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.menu-item:nth-child(3) {
  transform: rotate(240deg) translate(75px) rotate(-240deg);
  --glow-color: rgba(3, 169, 244, 0.6);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 添加淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.message-input {
  width: 300px;
  background: transparent;
  margin-right: 8px;
}

.message-field {
  border-radius: 20px;
  overflow: visible;
}

.message-field :deep(.v-field__outline__start) {
  border-radius: 20px 0 0 20px !important;
  border-width: 1px !important;
}

.message-field :deep(.v-field__outline__end) {
  border-radius: 0 20px 20px 0 !important;
  border-width: 1px !important;
}

.message-field :deep(.v-field__append-inner) {
  padding-inline-start: 8px;
  margin-inline-end: -12px;
}

.send-button {
  margin: -8px -12px -8px 0;
  border-radius: 50% !important;
  width: 36px !important;
  height: 36px !important;
  min-width: 36px !important;
  min-height: 36px !important;
  padding: 0 !important;
  background-color: var(--v-theme-primary) !important;
  box-shadow: 0 3px 5px -1px rgba(0,0,0,.2),0 6px 10px 0 rgba(0,0,0,.14),0 1px 18px 0 rgba(0,0,0,.12);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.send-button:hover {
  background-color: var(--v-theme-primary-darken-1) !important;
  transform: translateY(-1px);
  box-shadow: 0 5px 8px -3px rgba(0,0,0,.2),0 8px 12px 1px rgba(0,0,0,.14),0 3px 16px 2px rgba(0,0,0,.12);
}

.send-button:active {
  transform: translateY(1px);
  box-shadow: 0 2px 4px -1px rgba(0,0,0,.2),0 4px 8px 0 rgba(0,0,0,.14),0 1px 14px 0 rgba(0,0,0,.12);
}

.send-button .v-icon {
  color: #8600fb !important;
  font-size: 18px !important;
  margin: 0 !important;
}

/* 滑动动画 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  max-width: 300px;
}

.slide-enter-from,
.slide-leave-to {
  max-width: 0;
  opacity: 0;
  margin-right: -16px;
}

.slide-enter-to,
.slide-leave-from {
  max-width: 300px;
  opacity: 1;
  margin-right: 0;
}
</style> 