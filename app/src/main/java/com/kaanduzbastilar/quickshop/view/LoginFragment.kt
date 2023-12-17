package com.kaanduzbastilar.quickshop.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.kaanduzbastilar.quickshop.services.LoginAPI
import com.kaanduzbastilar.quickshop.model.LoginRequest
import com.kaanduzbastilar.quickshop.services.RetrofitService
import com.kaanduzbastilar.quickshop.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var userEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLoggedIn()) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.signUp.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.signIn.setOnClickListener {
            val email = binding.loginTextEmail.text.toString()
            val password = binding.loginTextPassword.text.toString()

            val request = LoginRequest(email, password)
            val apiService = RetrofitService.create(LoginAPI::class.java)

            CoroutineScope(Dispatchers.Main).launch {
                if (email.isNullOrEmpty() || password.isNullOrEmpty()){
                    Toast.makeText(requireContext(), "Enter Email and Password!", Toast.LENGTH_LONG).show()
                }
                else if (!isValidEmail(email)){
                    Toast.makeText(requireContext(), "Enter Valid Email!", Toast.LENGTH_LONG).show()
                }else{
                    try {
                        val response = apiService.login(request)

                        if (response.status == 200) {
                            // Giriş başarılıysa kullanıcı bilgilerini elde et
                            val accessToken = response.data.access_token
                            val refreshToken = response.data.refresh_token

                            setLoggedIn(true)

                            // Kullanıcı bilgileriyle yapılacak işlemleri gerçekleştirin
                            // Örneğin, MainActivity'ye yönlendirme
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish() // LoginActivity'yi kapat
                        } else {
                            // Giriş başarısızsa kullanıcıya hata mesajını gösterin veya ek işlemleri gerçekleştirin
                            wrong() //catch içindeki çalışıyor ne alakaysa
                        }
                    } catch (e: Exception) {
                        // API çağrısı sırasında hata oluştuğunda işlemleri burada ele alabilirsiniz
                        e.printStackTrace()
                        wrong()
                        Toast.makeText(requireContext(),"error",Toast.LENGTH_LONG).show()
                    }
                }
            }

        }

        /*
        activity?.let {
            val intent = Intent(it, MainActivity::class.java)
            startActivity(intent)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        */
    }

    private fun isLoggedIn(): Boolean{
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    private fun setLoggedIn(isLoggedIn: Boolean){
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()
        //val editor = sharedPreferences.edit()
        //editor.putBoolean(IS_LOGGED_IN, isLoggedIn)
        //editor.apply()
    }

    private fun wrong(){
        Toast.makeText(requireContext(), "Wrong Email or Password!", Toast.LENGTH_LONG).show()
    }

    private fun isValidEmail(email: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        //SharedPreferences veya ViewModel LiveData kullanılabilir
        //SharedPref ile
        private const val PREFS_NAME = "MyPrefs"
        private const val IS_LOGGED_IN = "isLoggedIn"
        private const val USER_EMAIL = "userEmail"
    }

}