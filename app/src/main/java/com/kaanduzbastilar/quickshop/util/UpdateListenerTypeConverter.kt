package com.kaanduzbastilar.quickshop.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaanduzbastilar.quickshop.model.CartItemModel

class UpdateListenerTypeConverter {

    @TypeConverter
    fun fromString(value: String?): CartItemModel.UpdateListener? {
        return if (value != null) {
            val type = object : TypeToken<CartItemModel.UpdateListener>() {}.type
            Gson().fromJson(value, type)
        } else {
            // Eğer value null ise, uygun bir değer döndürmek istiyorsanız burada uygun işlemi yapabilirsiniz.
            null
        }
    }



    @TypeConverter
    fun toString(updateListener: CartItemModel.UpdateListener?): String {
        return updateListener?.let {
            Gson().toJson(updateListener)
        } ?: ""
    }
}
