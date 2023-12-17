package com.kaanduzbastilar.quickshop

import android.content.Context
import android.content.Intent
import com.kaanduzbastilar.quickshop.model.ProductModel
import com.kaanduzbastilar.quickshop.view.MainActivity

class NavigationManager {
    companion object {
        fun openCartActivity(context: Context, productModel: ProductModel) {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("productModel", productModel)
            }
            context.startActivity(intent)
        }
    }
}