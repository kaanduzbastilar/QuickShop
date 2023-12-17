package com.kaanduzbastilar.quickshop.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.kaanduzbastilar.quickshop.model.ProductModel
import com.kaanduzbastilar.quickshop.viewmodel.SharedViewModel
import com.kaanduzbastilar.quickshop.databinding.FragmentProductDetailBinding
import com.kaanduzbastilar.quickshop.model.CartItemModel
import com.kaanduzbastilar.quickshop.services.CartDatabase
import com.kaanduzbastilar.quickshop.services.RetrofitService
import com.kaanduzbastilar.quickshop.view.ProductDetailActivity.Companion.ARG_PRODUCT_MODEL
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailFragment : Fragment() {

    private lateinit var sharedViewModel : SharedViewModel
    //  by lazy { ViewModelProvider(this)[SharedViewModel::class.java] }

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val mySharedViewModel = vita.with(VitaOwner.Multiple(this.viewLifecycleOwner)).getViewModel<SharedViewModel>()

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)//requireactivity


        val productModel = arguments?.getParcelable<ProductModel>(ARG_PRODUCT_MODEL)
        if (productModel != null){
            binding.productTitle.text = productModel.title
            binding.productPrice.text = "Price : ${productModel.price}"
            binding.productCategory.text = productModel.category.name
        }


        binding.addToCartButton.setOnClickListener {
            productModel?.let {
                val cartItem = CartItemModel(
                    productId = productModel._id,
                    productName = productModel.title,
                    productPrice = productModel.price.toString(),
                    productSlug = productModel.slug
                    // Diğer cart item özellikleri buraya eklenebilir
                )

                // CartItemModel'i SharedViewModel'a aktar
                //sharedViewModel.setSelectedProduct(listOf(cartItem))



                saveCartItemToSharedPreferences(cartItem)

                observeLiveData()

                //val intent = Intent(requireContext(), MainActivity::class.java)
                //intent.putExtra(ARG_PRODUCT_MODEL, productModel)
                //startActivity(intent)
            }
            /*

                        val productModel = arguments?.getParcelable<ProductModel>(ARG_PRODUCT_MODEL)
                        productModel?.let {
                            val cartItem = CartItem(
                                productId = productModel._id,
                                productName = productModel.title,
                                productPrice = productModel.price.toString()
                            )
                            println("productmodel boş değilse çalıştı")
                            sharedViewModel.selectedProduct.value = listOf(cartItem)





                            //sharedViewModel.addCartItem(cartItem)
                            //sharedViewModel.selectedProduct.value = cartItem
                            //sharedViewModel.setSharedData(cartItem)
                            //println("selected product değeri atandı")

                        }

             */

            /*
            sharedViewModel.selectedProduct.observe(viewLifecycleOwner) { cartItem ->
                sharedViewModel.selectedProduct.value = cartItem
            }

             */

            /*
            val action = ProductDetailFragmentDirections.actionProductDetailFragmentToNavigationCart()
            findNavController().navigate(action)
             */
        }



        /*
        binding.addToCartButton.setOnClickListener {
            val productModel = arguments?.getParcelable<ProductModel>(ARG_PRODUCT_MODEL)
            productModel?.let {
                sharedViewModel.selectedProduct.value = productModel
            }

            // CartFragment'ı oluşturun veya alın
            val cartFragment = CartFragment()

            // FragmentTransaction oluşturun
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            // Eğer geri gitme yığınına eklemek istiyorsanız:
            transaction.addToBackStack(null)

            // CartFragment'ı fragment_container'a ekleme işlemi
            transaction.replace(R.id.container, cartFragment)

            // Değişiklikleri onaylayın
            transaction.commit()
        }

         */


        return root
    }

    fun observeLiveData() {
        // SharedPreferences'den veriyi çek
        val sharedPreferences = requireContext().getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)
        val productSlug = sharedPreferences.getString("productSlug", "")

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

                            //_productSlug = productSlug

                            Toast.makeText(requireContext(), "Added to Basket!", Toast.LENGTH_SHORT).show()


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

    private fun storeInSQLite(list: List<CartItemModel>): List<CartItemModel> {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = CartDatabase(requireContext()).cartDao()

            val existingItems = dao.getAllCartItems()

            //dao.deleteAllCartItems()

            val combinedList = mutableListOf<CartItemModel>()

            for (newItem in list) {
                val existingItem = existingItems.find { it.productId == newItem.productId }

                if (existingItem != null) {
                    // Eğer aynı ürün varsa quantity'yi artır
                    existingItem.quantity += 1
                    //combinedList.add(existingItem)
                    dao.updateCartItem(existingItem)
                } else {
                    // Yeni bir ürünse direkt ekleyebilirsin
                    //combinedList.add(newItem)
                    dao.insertAll(newItem)
                }
            }

            dao.insertAll(*combinedList.toTypedArray())
        }
        return list
    }

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

    fun productModelToCartItem(productModel: ProductModel): CartItemModel {
        return CartItemModel(
            productId = productModel._id,
            productName = productModel.title,
            productPrice = productModel.price.toString(),
            productSlug = productModel.slug
        )
    }

    fun saveCartItemToSharedPreferences(cartItem: CartItemModel){
        val sharedPreferences = requireActivity().getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("productSlug", cartItem.productSlug)
        editor.apply()
    }

    companion object {
        private const val ARG_PRODUCT_MODEL = "product_model"

        fun newInstance(productModel: ProductModel) : ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_PRODUCT_MODEL, productModel)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        println("destroyed")
    }

}