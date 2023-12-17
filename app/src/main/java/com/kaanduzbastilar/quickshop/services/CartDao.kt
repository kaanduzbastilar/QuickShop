package com.kaanduzbastilar.quickshop.services

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kaanduzbastilar.quickshop.model.CartItemModel

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg cartItems: CartItemModel)

    @Query("SELECT * FROM cartItemModel")
    suspend fun getAllCartItems() : List<CartItemModel>

    @Query("DELETE FROM cartItemModel")
    suspend fun deleteAllCartItems()

    @Update
    suspend fun updateCartItem(cartItem: CartItemModel)

    @Query("SELECT * FROM CartItemModel WHERE productId = :itemId")
    suspend fun getCartItemById(itemId: String): CartItemModel?

    @Delete
    suspend fun deleteCartItem(cartItem: CartItemModel)
}