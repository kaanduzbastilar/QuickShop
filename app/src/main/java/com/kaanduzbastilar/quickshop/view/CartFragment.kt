package com.kaanduzbastilar.quickshop.view

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaanduzbastilar.quickshop.R
import com.kaanduzbastilar.quickshop.adapters.CartItemAdapter
import com.kaanduzbastilar.quickshop.adapters.RecyclerViewAdapter
import com.kaanduzbastilar.quickshop.model.ProductModel
import com.kaanduzbastilar.quickshop.viewmodel.SharedViewModel
import com.kaanduzbastilar.quickshop.databinding.FragmentCartBinding
import com.kaanduzbastilar.quickshop.model.CartItemModel
import com.kaanduzbastilar.quickshop.services.CartDatabase
import com.kaanduzbastilar.quickshop.services.RetrofitService
import com.kaanduzbastilar.quickshop.util.CartFragmentListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment(), CartFragmentListener{

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartItemAdapter: CartItemAdapter

    private lateinit var cartItemModelArrayList : ArrayList<CartItemModel>

    private lateinit var totalTextView: TextView

    private var _binding: FragmentCartBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setHasOptionsMenu(true)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.cartRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        cartItemAdapter = CartItemAdapter(mutableListOf(), sharedViewModel, requireContext())
        recyclerView.adapter = cartItemAdapter

        cartItemAdapter.setCartFragmentListener(this)

        lifecycleScope.launch {
            checkItOut()
        }

        totalTextView = binding.cartTotal

        //observeLiveData()
        getDataFromSQLite()

    }
/*
fun observeLiveData() {
    // SharedPreferences'den veriyi çek
    val sharedPreferences = requireContext().getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)
    val productSlug = sharedPreferences.getString("productSlug", "")
    var _productSlug: String ?= null

    if (productSlug != null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productDetails = fetchProductDetails(productSlug)
                    if (productDetails != null) {
                        // Assuming addItem function in cartItemAdapter accepts a ProductModel
                        val cartItem = productModelToCartItem(productDetails)

                            storeInSQLite(listOf(cartItem))

                withContext(Dispatchers.Main) {
                    // Update your UI or perform any actions based on the product details


                    //cartItemAdapter.addItem(storedData)
                    //val dao = CartDatabase(requireContext()).cartDao().getAllCartItems()
                    getDataFromSQLite()
                    //_productSlug = productSlug
                }}else {
                    // Handle the case where productDetails is null
                    //getDataFromSQLite()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception, show a message, or log
            }
        }
    }
}


 */
    /*
    suspend fun fetchProductDetails(productSlug: String): ProductModel? {
        // Use your API call logic to fetch product details based on productName
        // Return the fetched product details or null if there's an error
        return try {
            val response = RetrofitService.apiService.getProductBySlug(productSlug)
            if (response.status == 200) {
                response.data // Assuming data contains the ProductModel
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

     */


    /*
    private fun storeInSQLite(list: List<CartItemModel>): List<CartItemModel> {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = CartDatabase(requireContext()).cartDao()

            val existingItems = dao.getAllCartItems()

            val combinedList = (existingItems + list).distinctBy { it.productId }

            dao.deleteAllCartItems()

            dao.insertAll(*combinedList.toTypedArray())
        }
        return list
    }
    */

    override fun onResume() {
        super.onResume()
        calculateTotal()
    }

    private fun calculateTotal(){
        val total = cartItemAdapter.calculateTotal()
        println("Total: $total")
        totalTextView.text = "Cart Total: $total"
    }

    private fun getDataFromSQLite(){
        CoroutineScope(Dispatchers.IO).launch {
            val cartItems = CartDatabase(requireContext()).cartDao().getAllCartItems()
            withContext(Dispatchers.Main){

                cartItems.forEach{ item ->
                    cartItemAdapter.addItem(listOf(item))
                }

                //cartItemAdapter.addItem(cartItems)
            }
        }
    }

    /*
    private fun updateRecyclerView(cartItems : List<CartItemModel>){


        cartItemModelArrayList.clear() //listeyi temizle

        cartItemModelArrayList.addAll(cartItems) //filtrelenmiş ürünleri ekle

        cartItemAdapter.notifyDataSetChanged()// adaptera değişiklikleri bildir


    }

     */




    /*
    fun productModelToCartItem(productModel: ProductModel): CartItemModel {
        return CartItemModel(
            productId = productModel._id,
            productName = productModel.title,
            productPrice = productModel.price.toString(),
            productSlug = productModel.slug
        )
    }

     */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cart_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun clearCart() {
        CoroutineScope(Dispatchers.IO).launch{
            val dao = CartDatabase(requireContext()).cartDao()
            withContext(Dispatchers.Main){
                    dao.deleteAllCartItems()
                    cartItemAdapter.clear()
                    lifecycleScope.launch {
                        checkItOut()
                }
            }
        }
    }

    private fun deleteItems() {
        CoroutineScope(Dispatchers.IO).launch{
            val dao = CartDatabase(requireContext()).cartDao()
            val cartItems = dao.getAllCartItems()

            if (cartItems.isNotEmpty()){
                withContext(Dispatchers.Main){
                    showDeleteConfirmationDialog()
                }
            }else{
                withContext(Dispatchers.Main){
                    Toast.makeText(context,"Cart is already Empty!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Clear Cart")
        builder.setMessage("Are you sure you want to clear your cart?")
        builder.setPositiveButton("Yes") { dialog, which ->
            clearCart()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                deleteItems()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    suspend fun checkItOut(){
        Log.d("CheckItOut", "Function called")
        println("func called")
        CoroutineScope(Dispatchers.IO).launch {
            val countryItems = CartDatabase(requireContext()).cartDao().getAllCartItems()
            Log.d("CheckItOut", "Number of items: ${countryItems.size}")
            println("number of items")
            withContext(Dispatchers.Main){
                if (countryItems.isNullOrEmpty()) {
                    Log.d("CheckItOut", "Cart is empty")
                    binding.cartRecyclerView.visibility = View.GONE
                    binding.cartTotalLayout.visibility = View.GONE
                    binding.cartIsEmpty.visibility = View.VISIBLE
                } else {
                    Log.d("CheckItOut", "Cart is not empty")
                    binding.cartRecyclerView.visibility = View.VISIBLE
                    binding.cartTotalLayout.visibility = View.VISIBLE
                    binding.cartIsEmpty.visibility = View.GONE
                }
            }
        }
    }


    companion object {
        private const val ARG_PRODUCT_MODEL = "product_model"

        fun newInstance(productModel: ProductModel) : CartFragment {
            val fragment = CartFragment()
            val args = Bundle()
            args.putParcelable(ARG_PRODUCT_MODEL, productModel)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCartItemUpdated() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleScope.launch {
                    try {
                        checkItOut()
                        println("onCartItemUpdated")
                        calculateTotal()
                    } catch (e: Exception) {
                        Log.e("CheckItOut", "Error in onCartItemUpdated", e)
                    }
                }
            }
    }
    private fun calculateAndShowTotal() {
        val total = (recyclerView.adapter as? CartItemAdapter)?.calculateTotal() ?: 0.0
        totalTextView.text = "Cart Total: ${total}"
    }
}