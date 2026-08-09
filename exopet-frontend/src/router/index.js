import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/splash'
  },
  {
    path: '/splash',
    name: 'Splash',
    meta: { requiresAuth: false },
    component: () => import('../views/Splash.vue')
  },
  {
    path: '/login1',
    name: 'Login1',
    meta: { requiresAuth: false },
    component: () => import('../views/Login1.vue')
  },
  {
    path: '/login2',
    name: 'Login2',
    meta: { requiresAuth: false },
    component: () => import('../views/Login2.vue')
  },
  {
    path: '/home',
    name: 'Home',
    meta: { requiresAuth: false },
    component: () => import('../views/Home.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    meta: { requiresAuth: true },
    component: () => import('../views/Profile.vue')
  },
  {
    path: '/membership',
    name: 'Membership',
    meta: { requiresAuth: true },
    component: () => import('../views/Membership.vue')
  },
  {
    path: '/coupons',
    name: 'Coupons',
    meta: { requiresAuth: true },
    component: () => import('../views/Coupons.vue')
  },
  {
    path: '/my-pets',
    name: 'MyPets',
    meta: { requiresAuth: true },
    component: () => import('../views/MyPets.vue')
  },
  {
    path: '/ai-consult',
    name: 'AIConsult',
    meta: { requiresAuth: true },
    component: () => import('../views/AIConsult.vue')
  },
  {
    path: '/specialist-consult',
    name: 'SpecialistConsult',
    meta: { requiresAuth: true },
    component: () => import('../views/SpecialistConsult.vue')
  },
  {
    path: '/emergency-consult',
    name: 'EmergencyConsult',
    meta: { requiresAuth: true },
    component: () => import('../views/EmergencyConsult.vue')
  },
  {
    path: '/hospital-finder',
    name: 'HospitalFinder',
    meta: { requiresAuth: true },
    component: () => import('../views/HospitalFinder.vue')
  },
  {
    path: '/pet-photo',
    name: 'PetPhotoRecognition',
    meta: { requiresAuth: true },
    component: () => import('../views/PetPhotoRecognition.vue')
  },
  {
    path: '/ai-encyclopedia',
    name: 'AIEncyclopedia',
    meta: { requiresAuth: false },
    component: () => import('../views/AIEncyclopedia.vue')
  },
  {
    path: '/mood-analysis',
    name: 'MoodAnalysis',
    meta: { requiresAuth: true },
    component: () => import('../views/MoodAnalysis.vue')
  },
  {
    path: '/guinea-pig',
    name: 'GuineaPig',
    meta: { requiresAuth: false },
    component: () => import('../views/GuineaPig.vue')
  },
  {
    path: '/dialog',
    name: 'Dialog',
    meta: { requiresAuth: true },
    component: () => import('../views/Dialog.vue')
  },
  {
    path: '/feedback',
    name: 'Feedback',
    meta: { requiresAuth: true },
    component: () => import('../views/Feedback.vue')
  },
  {
    path: '/submit-success',
    name: 'SubmitSuccess',
    meta: { requiresAuth: false },
    component: () => import('../views/SubmitSuccess.vue')
  },
  {
    path: '/my-orders',
    name: 'MyOrders',
    meta: { requiresAuth: true },
    component: () => import('../views/MyOrders.vue')
  },
  {
    path: '/disputes',
    name: 'Dispute',
    meta: { requiresAuth: true },
    component: () => import('../views/Dispute.vue')
  },
  {
    path: '/hospital-detail/:id',
    name: 'HospitalDetail',
    meta: { requiresAuth: true },
    component: () => import('../views/HospitalDetail.vue')
  },
  {
    path: '/my-appointments',
    name: 'MyAppointments',
    meta: { requiresAuth: true },
    component: () => import('../views/MyAppointments.vue')
  },
  {
    path: '/notifications',
    name: 'Notifications',
    meta: { requiresAuth: true },
    component: () => import('../views/NotificationCenter.vue')
  },
  // 404 兜底
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    meta: { requiresAuth: false },
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 🔐 路由鉴权守卫 — 基于 meta.requiresAuth
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login1')
  } else {
    next()
  }
})

export default router
