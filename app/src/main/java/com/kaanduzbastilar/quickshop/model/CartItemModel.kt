package com.kaanduzbastilar.quickshop.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kaanduzbastilar.quickshop.util.UpdateListenerTypeConverter
import io.reactivex.rxjava3.annotations.NonNull

@Entity
@TypeConverters(UpdateListenerTypeConverter::class)
data class CartItemModel(
    @PrimaryKey
    @ColumnInfo("productId")
    val productId: String,
    @ColumnInfo("productName")
    val productName: String?,
    @ColumnInfo("productSlug")
    val productSlug: String?,
    @ColumnInfo("productPrice")
    val productPrice: String?,
    @ColumnInfo("quantity")
    var quantity: Int = 1) : Parcelable {

    interface UpdateListener {
        fun onQuantityUpdated()
    }

    var updateListener: UpdateListener? = null

    fun incrementQuantity() {
        quantity++
        //updateQuantityInDatabase()
        updateListener?.onQuantityUpdated()
    }

    fun decrementQuantity() {
        if (quantity > 0) {
            quantity--
            //updateQuantityInDatabase()
            updateListener?.onQuantityUpdated()
        }
    }

    private fun updateQuantityInDatabase() {
        // Burada veritabanını güncelleyecek işlemleri yapabilirsiniz
        // Örneğin, bir Room veritabanı işlemi yapabilirsiniz
        // Örneğin, bir Retrofit servisi kullanarak veriyi sunucuya gönderebilirsiniz
    }

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel){
            parcel.writeString(productId)
            parcel.writeString(productName)
            parcel.writeString(productSlug)
            parcel.writeString(productPrice)
            parcel.writeInt(quantity)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItemModel> {
        override fun createFromParcel(parcel: Parcel): CartItemModel {
            return CartItemModel(parcel)
        }

        override fun newArray(size: Int): Array<CartItemModel?> {
            return arrayOfNulls(size)
        }
    }
}
