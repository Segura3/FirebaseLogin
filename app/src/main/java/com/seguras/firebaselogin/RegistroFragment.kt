package com.seguras.firebaselogin

import android.accessibilityservice.GestureDescription
import android.content.ContentValues
import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.seguras.firebaselogin.databinding.FragmentRegistroBinding
import java.util.regex.Pattern

class RegistroFragment : Fragment() {

    private lateinit var binding: FragmentRegistroBinding
    lateinit var autenticacion: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autenticacion = FirebaseAuth.getInstance()
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistroBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRGuardar.setOnClickListener {
            if(validaCampos()){
                registro(view)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.tietRName.setText("")
        binding.tietRName.error = null
        binding.tietRApellido.setText("")
        binding.tietRApellido.error = null
        binding.tietREmail.setText("")
        binding.tietREmail.error = null
        binding.tietRContrasena.setText("")
        binding.tietRContrasena.error = null
    }

    fun registro(view: View){
        val name: String = binding.tietRName.text.toString()
        val apellido: String = binding.tietRApellido.text.toString()
        val correo: String = binding.tietREmail.text.toString()
        val psw: String = binding.tietRContrasena.text.toString()

        autenticacion.createUserWithEmailAndPassword(correo,psw).addOnCompleteListener {
            if(it.isSuccessful){

                val id: String = autenticacion.currentUser?.uid.toString()

                val userData = hashMapOf(
                    "Nombre" to name,
                    "Apellido" to apellido,
                    "id" to id
                )

                db.collection("usuarios")
                    .add(userData)
                    .addOnSuccessListener { documentReference ->
                        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error al añadir los datos", e)
                    }

                autenticacion.signOut()
                if(autenticacion.currentUser==null){
                    Toast.makeText(activity,"Registro exitoso", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(view).navigate(R.id.action_registroFragment_to_loginFragment)
                }
            }else{
                Toast.makeText(activity,getString(R.string.error_registro), Toast.LENGTH_SHORT).show()
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
            if(tietRName.text.toString().isEmpty()){
                tietRName.error = getString(R.string.valreq)
                res = false
            }else tietRName.error = null
            if(tietRApellido.text.toString().isEmpty()){
                tietRApellido.error = getString(R.string.valreq)
                res = false
            }else tietRApellido.error = null
            if(tietREmail.text.toString().isEmpty()){
                tietREmail.error = getString(R.string.valreq)
                res = false
            }else if(!Patterns.EMAIL_ADDRESS.matcher(tietREmail.text.toString()).matches()){
                tietREmail.error = getString(R.string.correo_valido)
                res = false
            }else tietREmail.error = null
            if(tietRContrasena.text.toString().isEmpty()){
                tietRContrasena.error = getString(R.string.valreq)
                res = false
            }else if(!pswRegex.matcher(tietRContrasena.text).matches()){
                tietRContrasena.error = getString(R.string.psw_guideline)
                res = false
            }else tietRContrasena.error = null
        }
        return res
    }
}