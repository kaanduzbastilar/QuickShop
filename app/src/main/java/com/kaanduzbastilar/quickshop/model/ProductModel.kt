package com.kaanduzbastilar.quickshop.model

import android.os.Parcel
import android.os.Parcelable

data class ProductCategory(val _id: String, val name: String, val slug: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(name)
        parcel.writeString(slug)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductCategory> {
        override fun createFromParcel(parcel: Parcel): ProductCategory {
            return ProductCategory(parcel)
        }

        override fun newArray(size: Int): Array<ProductCategory?> {
            return arrayOfNulls(size)
        }
    }

    fun matchesCategory(selectedCategory: String): Boolean {
        return selectedCategory == "All" || this.name == selectedCategory
    }
}

data class ProductModel(
    val _id: String,
    val title: String,
    val price: Int,
    val category: ProductCategory,
    val description: String,
    val createdBy: CreatedBy,
    val createdAt: String,
    val updatedAt: String,
    val slug: String,
    val image: String
    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readParcelable(ProductCategory::class.java.classLoader) ?: ProductCategory("","",""),
        parcel.readString() ?: "",
        parcel.readParcelable(CreatedBy::class.java.classLoader) ?: CreatedBy("","",""),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(title)
        parcel.writeInt(price)
        parcel.writeParcelable(category, flags)
        parcel.writeString(description)
        parcel.writeParcelable(createdBy, flags)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(slug)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductModel> {
        override fun createFromParcel(parcel: Parcel): ProductModel {
            return ProductModel(parcel)
        }

        override fun newArray(size: Int): Array<ProductModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class CreatedBy(
    val role: String,
    val _id: String,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(role)
        parcel.writeString(_id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CreatedBy> {
        override fun createFromParcel(parcel: Parcel): CreatedBy {
            return CreatedBy(parcel)
        }

        override fun newArray(size: Int): Array<CreatedBy?> {
            return arrayOfNulls(size)
        }
    }
}