/**
 * Coded by 余卓炜 202330452181
 * 开发环境配置
 */
// 如果在服务器上运行，需要设置为服务器IP；本地开发可以改为 localhost
const development = {
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 30000, // 增加到30秒，避免邮件发送导致超时
  debug: true
}

// 生产环境配置
// 支持通过环境变量 VITE_API_BASE_URL 覆盖，如果没有则使用默认值
const production = {
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://106.52.56.62:8080/api',
  timeout: 10000,
  debug: false
}

// 根据环境变量选择配置
const config = import.meta.env.MODE === 'development' ? development : production

export default config
