package com.kaanduzbastilar.quickshop.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kaanduzbastilar.quickshop.model.CartItemModel
import com.kaanduzbastilar.quickshop.viewmodel.SharedViewModel
import com.kaanduzbastilar.quickshop.databinding.CartCardLayoutBinding
import com.kaanduzbastilar.quickshop.services.CartDatabase
import com.kaanduzbastilar.quickshop.util.CartFragmentListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartItemAdapter(private val cartItemModels: MutableList<List<CartItemModel>>, private val sharedViewModel: SharedViewModel, context: Context) :
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    private val dao = CartDatabase(context).cartDao()

    //en son listener ekledim ki cartfragmentteki cart totali güncelleyebilmek için
    //listenerda checkitout çağırıyoruz ki - ile sepetten sildiğimizde görünümün değişmesi için en son bunu ekledim
    private var listener: CartFragmentListener? = null

    fun setCartFragmentListener(listener: CartFragmentListener) {
        this.listener = listener
    }


    fun addItem(cartItemModel: List<CartItemModel>) {
        //cartItemModels.clear()
        cartItemModels.addAll(listOf(cartItemModel))
        //cartItemModels.add(0,cartItemModel)
        Log.d("CartItemAdapter", "Item added. New item count: ${cartItemModels.size}")
        Log.d("CartItemAdapter", "Item details: $cartItemModel")
        Log.d("CartItemAdapter", "Total after add: ${calculateTotal()}")
        notifyDataSetChanged()
    }

    fun clear() {
        cartItemModels.clear()
        notifyDataSetChanged()
    }

    fun calculateTotal(): Double {
        var total = 0.0
        for (cartItemModelList in cartItemModels) {
            for (cartItemModel in cartItemModelList) {
                val price = cartItemModel.productPrice?.toDoubleOrNull()
                val quantity = cartItemModel.quantity
                if (price != null) {
                    total += price * quantity
                }
            }
        }
        Log.d("CartItemAdapter", "Calculated total: $total")
        return total
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = CartCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = cartItemModels[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int {
        return cartItemModels.size
    }

    inner class CartItemViewHolder(private val binding: CartCardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = cartItemModels[position]
                    sharedViewModel.selectedProduct.value = clickedItem
                    //sharedViewModel.setSharedData(clickedItem)
                }
            }

            binding.btnPlus.setOnClickListener {

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = cartItemModels[position][0]
                    clickedItem.incrementQuantity()

                    CoroutineScope(Dispatchers.Main).launch {
                        val existingItem = withContext(Dispatchers.IO) {
                            dao.getCartItemById(clickedItem.productId)
                        }

                        existingItem?.let {
                            it.quantity = clickedItem.quantity
                            updateCartItemAsync(it)
                        }
                        notifyDataSetChanged()
                    }
                }
                calculateTotal()
            }


            binding.btnMinus.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = cartItemModels[position][0] // Tek bir öğeyi işlemek için [0] ekledik.
                    clickedItem.decrementQuantity()

                    CoroutineScope(Dispatchers.Main).launch {
                        val existingItem = withContext(Dispatchers.IO) {
                            dao.getCartItemById(clickedItem.productId)
                        }

                        existingItem?.let {
                            it.quantity = clickedItem.quantity
                            updateCartItemAsync(it)
                        }

                        notifyDataSetChanged()
                    }
                }
            }

        }

        private fun removeItem(productId: String) {
            val removedItem = cartItemModels.find { cartItemList ->
                cartItemList.any { it.productId == productId && it.quantity == 0 }
            }

            removedItem?.let {
                val itemToRemove = it[0]
                CoroutineScope(Dispatchers.IO).launch {
                    dao.deleteCartItem(itemToRemove)
                }
                cartItemModels.remove(it)
                notifyDataSetChanged()
                Log.d("CartItemAdapter", "Item removed. New item count: ${cartItemModels.size}")
                Log.d("CartItemAdapter", "Total after remove: ${calculateTotal()}")
            }
        }

///bunu yapınca recycler viewdan siliyor fakat databasedende silmemiz lazım
        //geri gidip geldiğimde 0 adet göüzükücek tekrardan

        private suspend fun updateCartItemAsync(item: CartItemModel) {
            withContext(Dispatchers.IO) {
                dao.updateCartItem(item)
            }

            if (item.quantity == 0) {
                removeItem(item.productId)
            }

            listener?.onCartItemUpdated()

        }

        fun bind(cartItemModel: List<CartItemModel>) {
            binding.productText.text = cartItemModel[0].productName
            binding.productPrice.text = "Price: ${cartItemModel[0].productPrice}"
            binding.productTotal.text = "In Basket : ${cartItemModel[0].quantity}"
            binding.productTotal2.text = cartItemModel[0].quantity.toString()
        }
    }
}

