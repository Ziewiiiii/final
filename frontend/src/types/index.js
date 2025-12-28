/**
 * 类型定义文件
 * 定义应用中使用的数据结构
 */

// 用户信息类型
export const UserInfo = {
  id: Number,
  username: String,
  email: String,
  avatar: String,
  phone: String
}

// 商品信息类型
export const GoodsInfo = {
  id: Number,
  name: String,
  price: Number,
  image: String,
  description: String,
  stock: Number,
  status: Number
}

// 购物车商品类型
export const CartItem = {
  id: Number,
  goodsId: Number,
  goodsName: String,
  goodsImage: String,
  price: Number,
  count: Number,
  selected: Boolean
}

// API响应类型
export const ApiResponse = {
  code: Number,
  message: String,
  data: Object
}
