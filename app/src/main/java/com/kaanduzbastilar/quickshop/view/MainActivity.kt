package com.kaanduzbastilar.quickshop.view

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.kaanduzbastilar.quickshop.R
import com.kaanduzbastilar.quickshop.model.ProductModel
import com.kaanduzbastilar.quickshop.view.ProductDetailActivity.Companion.ARG_PRODUCT_MODEL
import com.kaanduzbastilar.quickshop.viewmodel.SharedViewModel
import com.kaanduzbastilar.quickshop.databinding.ActivityMainBinding
import com.kaanduzbastilar.quickshop.model.CartItemModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productModel = intent.getParcelableExtra<ProductModel>(ARG_PRODUCT_MODEL)
        productModel?.let {
            val cartItemModel = CartItemModel(
                productId = productModel._id,
                productName = productModel.title,
                productPrice = productModel.price.toString(),
                productSlug = productModel.slug
            )
            sharedViewModel.selectedProduct.value = listOf(cartItemModel)
        }

        /*
        if(productModel != null){
            val cartFragment = productModel?.let { CartFragment.newInstance(it) }
            supportFragmentManager.beginTransaction()
                .replace(R.id.cartRecyclerView, cartFragment as Fragment)
                .commit()
        }

 */



/*
        val fragment = CartFragment()
        val bundle = Bundle().apply {
            productModel
            if(productModel != null){
                val cartFragment = productModel?.let { CartFragment.newInstance(it) }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.cartRecyclerView, cartFragment as Fragment)
                    .commit()
            }
        }
        fragment.arguments = bundle

 */

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_cart, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        }
}