package com.kaanduzbastilar.quickshop.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.kaanduzbastilar.quickshop.model.ProductModel
import com.kaanduzbastilar.quickshop.adapters.RecyclerViewAdapter
import com.kaanduzbastilar.quickshop.view.ProductDetailActivity.Companion.ARG_PRODUCT_MODEL
import com.kaanduzbastilar.quickshop.services.RetrofitService
import com.kaanduzbastilar.quickshop.databinding.FragmentHomeBinding
import com.kaanduzbastilar.quickshop.viewmodel.HomeViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    private lateinit var categoryFilterSpinner: Spinner
    private lateinit var allCategories: MutableList<String>
    private lateinit var originalList: List<ProductModel>
    private lateinit var tempList: ArrayList<ProductModel>
    private lateinit var showList: ArrayList<ProductModel>


    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var productModelArrayList : ArrayList<ProductModel>
    private lateinit var recycleViewAdapter : RecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        productModelArrayList = ArrayList()
        recycleViewAdapter = RecyclerViewAdapter(productModelArrayList, requireContext())
        recycleViewAdapter.setOnItemClickListener(this)

        val layoutManager = GridLayoutManager(requireContext(),2)

        binding.productsRecyclerView.layoutManager = layoutManager
        binding.productsRecyclerView.adapter = recycleViewAdapter

        categoryFilterSpinner = binding.categoryFilterSpinner

        fetchData()

        setupCategoryFilterSpinner()



        //productModelArrayList.add(ProductModel(0,"test", "20", R.drawable.custom_email_icon))
        //productModelArrayList.add(ProductModel(1,"test2", "15", R.drawable.custom_email_icon))

        //val adapter = RecyclerViewAdapter(productModelArrayList, requireContext(),)

        return root
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitService.apiService.getAllProducts()
                withContext(Dispatchers.Main) {
                    if (response.status == 200) {
                        originalList = ArrayList(response.data ?: emptyList())
                        //productModelArrayList.addAll(response.data ?: emptyList())
                        tempList = ArrayList(originalList)
                        //showList = ArrayList(tempList)
                        //recycleViewAdapter.notifyDataSetChanged()
                        updateRecyclerView(tempList)
                    } else {
                        // Handle error, show a message or log
                        println("failed to fetch data")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception, show a message or log
                println("error")
            }
        }
    }

    override fun onItemClick(productModel: ProductModel) {

        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra(ARG_PRODUCT_MODEL, productModel)
        startActivity(intent)



        /*
        val detailFragment = ProductDetailFragment.newInstance(productModel)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.productDetailFragment, detailFragment)
            .addToBackStack(null)
            .commit()

         */



        /*
        val bundle = bundleOf("productModel" to productModel)
        val action = HomeFragmentDirections.actionNavigationHomeToProductDetailFragment()

        findNavController().navigate(action)

         */


    }

    suspend fun fetchAllCategories(): List<String> {
        return withContext(Dispatchers.IO){ try {
            val response = RetrofitService.apiService.getAllCategories()
            if (response.status == 200) {
                response.data?.map { it.name } ?: emptyList()
            } else {
                // Handle error, show a message or log
                println("failed to fetch categories")
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exception, show a message or log
            println("error")
            emptyList()
        }
        }
    }

    fun setupCategoryFilterSpinner(){
        CoroutineScope(Dispatchers.Main).launch() {
            allCategories = mutableListOf("All")
            allCategories.addAll(fetchAllCategories())

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, allCategories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            categoryFilterSpinner.adapter = adapter

            categoryFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCategory = allCategories[position]
                    applyFilter(selectedCategory)
                    println("setup category apply filter ")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }
    }

    private fun applyFilter(selectedCategory: String) {
        //originalList = ArrayList(productModelArrayList)
        println("before filter Original list size: ${originalList.size}")
        val filteredList = if (selectedCategory == "All") {
            ArrayList(tempList)//tümünü göster
        }else {
            tempList.filter { it.category.matchesCategory(selectedCategory) }
        }
        println("after list size: ${filteredList.size}")

        updateRecyclerView(filteredList)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(filteredProducts : List<ProductModel>){

        println("Original list size: ${filteredProducts.size}")

        productModelArrayList.clear() //listeyi temizle

        productModelArrayList.addAll(filteredProducts) //filtrelenmiş ürünleri ekle

        recycleViewAdapter.notifyDataSetChanged()// adaptera değişiklikleri bildir

        println("update recycler")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}