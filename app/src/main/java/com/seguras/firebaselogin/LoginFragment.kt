package com.seguras.firebaselogin

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.seguras.firebaselogin.databinding.FragmentLoginBinding
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    lateinit var autenticacion: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autenticacion = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            if(validaCampos()){
                login(view)
            }
        }
        binding.btnRegistro.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registroFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = autenticacion.currentUser
        if(user != null){
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_mainFragment)
        }

        binding.tietContrasena.setText("")
        binding.tietContrasena.error = null
        binding.tietName.setText("")
        binding.tietName.error = null
    }

    fun login(view: View){
        val correo: String = binding.tietName.text.toString()
        val password: String = binding.tietContrasena.text.toString()

        autenticacion.signInWithEmailAndPassword(correo,password).addOnCompleteListener {
            if(it.isSuccessful){
                //Avansar a la pantalla principal
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainFragment)
            }else{
                //Mostrar un resutado de error
                Toast.makeText(activity, getString(R.string.usuario_no_registrado), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun validaCampos(): Boolean{
        val pswRegex = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=[^A-Za-z]*[A-Za-z])" + //at least 1 letter
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 6 characters
                "$")
        var res = true

        with(binding){
            if(tietName.text.toString().isEmpty()){
                tietName.error = getString(R.string.valreq)
                res = false
            }else if(!Patterns.EMAIL_ADDRESS.matcher(tietName.text.toString()).matches()){
                tietName.error = getString(R.string.ingresa_valor)
                res = false
            }else tietName.error = null
            if(tietContrasena.text.toString().isEmpty()){
                tietContrasena.error = getString(R.string.valreq)
                res = false
            }else tietContrasena.error = null
        }
        return res
    }
}