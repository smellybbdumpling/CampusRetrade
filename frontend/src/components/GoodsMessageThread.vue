<template>
  <article class="message-item" :class="{ 'is-reply': depth > 0 }">
    <div class="message-meta">
      <strong>{{ message.senderName }}</strong>
      <span class="message-time">{{ formatMessageTime(message.createTime).date }}<br />{{ formatMessageTime(message.createTime).time }}</span>
    </div>
    <p>{{ message.content }}</p>
    <button v-if="canReply(message)" class="reply-trigger" @click="toggleReplyBox(message.id)">回复</button>
    <div class="reply-box" v-if="canReply(message) && activeReplyId === message.id">
      <textarea v-model="replyDrafts[message.id]" class="textarea" rows="2" placeholder="在此回复此条留言"></textarea>
      <button class="btn" @click="submitReply(message.id)">发送回复</button>
    </div>
    <div class="reply-list" v-if="message.replies?.length">
      <GoodsMessageThread
        v-for="reply in message.replies"
        :key="reply.id"
        :message="reply"
        :depth="depth + 1"
        :active-reply-id="activeReplyId"
        :reply-drafts="replyDrafts"
        :can-reply="canReply"
        :toggle-reply-box="toggleReplyBox"
        :submit-reply="submitReply"
        :format-message-time="formatMessageTime"
      />
    </div>
  </article>
</template>

<script setup>
import GoodsMessageThread from './GoodsMessageThread.vue'

defineProps({
  message: {
    type: Object,
    required: true
  },
  depth: {
    type: Number,
    default: 0
  },
  activeReplyId: {
    type: [Number, String, null],
    default: null
  },
  replyDrafts: {
    type: Object,
    required: true
  },
  canReply: {
    type: Function,
    required: true
  },
  toggleReplyBox: {
    type: Function,
    required: true
  },
  submitReply: {
    type: Function,
    required: true
  },
  formatMessageTime: {
    type: Function,
    required: true
  }
})
</script>

<style scoped>
.message-item {
  padding: 14px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid var(--line);
}

.message-item.is-reply {
  margin-left: 18px;
}

.reply-list {
  display: grid;
  gap: 10px;
  margin-top: 12px;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: var(--muted);
  margin-bottom: 8px;
}

.reply-trigger {
  margin-top: 4px;
  border: none;
  background: transparent;
  color: var(--accent-deep);
  font-weight: 700;
  padding: 0;
}

.reply-box {
  display: grid;
  gap: 10px;
  margin-top: 12px;
}

.message-time {
  text-align: right;
  line-height: 1.4;
}
</style>
