package com.kaanduzbastilar.quickshop.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.kaanduzbastilar.quickshop.R
import com.kaanduzbastilar.quickshop.model.ProductModel
import com.kaanduzbastilar.quickshop.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val productModel = intent.getParcelableExtra<ProductModel>(ARG_PRODUCT_MODEL)
        if (productModel != null){
            val detailFragment = productModel?.let { ProductDetailFragment.newInstance(it) }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_detail_container, detailFragment as Fragment)
                .commit()
        }

    }

    companion object {
        const val ARG_PRODUCT_MODEL = "product_model"
    }



}