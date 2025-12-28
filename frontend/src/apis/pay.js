/**
 * Coded by 余卓炜 202330452181
 */
import request from "@/utils/http"

export  const getOrderAPI=(id)=>{
    return request({
        url:`/member/order/${id}`

    })


}