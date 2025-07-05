package com.example.doctorappoint.data.api

import com.example.doctorappoint.common.Helper.Helpers
import com.example.doctorappoint.data.Constant.AppInfo
import okhttp3.FormBody
import org.json.JSONObject
import java.util.Date

class CreateOrder {
    private inner class CreateOrderData(amount: String) {
        val AppId: String
        val AppUser: String
        val AppTime: String
        val Amount: String
        val AppTransId: String
        val EmbedData: String
        val Items: String
        val BankCode: String
        val Description: String
        val Mac: String

        init {
            val appTime = Date().time
            AppId = AppInfo.APP_ID.toString()
            AppUser = "Doctor_Appointment"
            AppTime = appTime.toString()
            Amount = amount
            AppTransId = Helpers.getAppTransId()
            EmbedData = "{}"
            Items = "[]"
            BankCode = "zalopayapp"
            Description = "Thanh toán hóa đơn đặt lịch khám bệnh ${Helpers.getAppTransId()}"

            val inputHMac = "$AppId|$AppTransId|$AppUser|$Amount|$AppTime|$EmbedData|$Items"
            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac)
        }
    }

    @Throws(Exception::class)
    fun createOrder(amount: String): JSONObject {
        val input = CreateOrderData(amount)

        val formBody = FormBody.Builder()
            .add("app_id", input.AppId)
            .add("app_user", input.AppUser)
            .add("app_time", input.AppTime)
            .add("amount", input.Amount)
            .add("app_trans_id", input.AppTransId)
            .add("embed_data", input.EmbedData)
            .add("item", input.Items)
            .add("bank_code", input.BankCode)
            .add("description", input.Description)
            .add("mac", input.Mac)
            .build()

        return ZaloPayHttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody)
    }
}