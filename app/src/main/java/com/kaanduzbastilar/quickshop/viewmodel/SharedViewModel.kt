package com.kaanduzbastilar.quickshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaanduzbastilar.quickshop.model.CartItemModel
import com.kaanduzbastilar.quickshop.model.ProductModel

class SharedViewModel : ViewModel() {
/*
    private var _text = MutableLiveData("Text")
    val text: LiveData<String> = _text
    fun saveText(newText : String){
        _text.value = newText
    }

 */

/*
    private val cartItems = MutableLiveData<ArrayList<CartItem>>()
    val cartItemsLiveData: LiveData<ArrayList<CartItem>> get() = cartItems

    fun setCartItemsData(items: ArrayList<CartItem>){
        cartItems.value = items
    }

    fun addCartItem(item: CartItem){
        val currentList = cartItems.value ?:ArrayList()
        currentList.add(item)
        cartItems.value = currentList
    }

 */

    //val selectedProduct = MutableLiveData<List<CartItemModel>>() //private yapman lazım bir alttaki için

    private val _selectedProduct = MutableLiveData<List<CartItemModel>>()
    val selectedProduct: MutableLiveData<List<CartItemModel>> get() = _selectedProduct

    fun setSelectedProduct(cartItemModel: List<CartItemModel>) {
        _selectedProduct.value = cartItemModel
    }

/*
    val selectedProduct: LiveData<CartItem>
        get() = _selectedProduct

    fun setSharedData(product: CartItem){
        _selectedProduct.value = product
    }

 */



    /*
    val selectedProduct : LiveData<CartItem> = _selectedProduct
    fun saveData(newData : CartItem){
        _selectedProduct.value = newData
    }

     */
}