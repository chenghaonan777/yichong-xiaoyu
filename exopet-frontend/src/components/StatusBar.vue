<template>
  <div class="status-bar">
    <div class="status-left">
      <span class="time">05:28</span>
      <span class="signal-icon">
        <svg viewBox="0 0 14 11" width="14" height="11">
          <rect x="0" y="7" width="3" height="4" fill="#333"/>
          <rect x="4" y="4" width="3" height="7" fill="#333"/>
          <rect x="8" y="2" width="3" height="9" fill="#333"/>
          <rect x="12" y="0" width="2" height="11" fill="#333"/>
        </svg>
      </span>
    </div>
    <div class="status-right">
      <svg viewBox="0 0 25 10" width="25" height="10">
        <rect x="0" y="1" width="22" height="8" rx="2" fill="none" stroke="#333" stroke-width="0.8"/>
        <rect x="3" y="3" width="16" height="4" rx="1" fill="#4CAF50"/>
        <rect x="23" y="3" width="2" height="4" rx="1" fill="#333"/>
      </svg>
      <span class="battery-text">80%</span>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 20px;
  padding: 0 14px;
  background: transparent;
  font-size: 12px;
  color: #333;
  position: relative;
  z-index: 10;
}
.status-left {
  display: flex;
  align-items: center;
  gap: 4px;
}
.status-right {
  display: flex;
  align-items: center;
  gap: 2px;
}
.time {
  font-size: 12px;
  font-weight: 400;
}
.battery-text {
  font-size: 12px;
  color: #333;
}
.signal-icon {
  display: flex;
  align-items: center;
}
</style>
