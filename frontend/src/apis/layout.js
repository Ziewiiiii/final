/**
 * Coded by 余卓炜 202330452181
 */
import httpInstance from "@/utils/http"

export function getCategoryAPI () {
  return httpInstance({
    url: '/home/category/head'
  })
}