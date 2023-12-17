package com.kaanduzbastilar.quickshop.model

import android.os.Parcel
import android.os.Parcelable

data class User(val role: String,
                val _id: String,
                val name: String,
                val email: String,
                val number: String,
                val password: String,
                val createdAt: String,
                val updatedAt: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(role)
        parcel.writeString(_id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(number)
        parcel.writeString(password)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}