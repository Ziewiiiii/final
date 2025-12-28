import ImageView from './ImageView/indexpic.vue'
import Sku from './XtxSku/index.vue'
export const componentPlugin = {
  install (app) {
    // app.component('组件名字'，组件配置对象)
    app.component('xsImageView', ImageView)
    app.component('xsSku', Sku)
  }
}