package com.uaeh.aame.cobmaestros.ui

import android.app.ProgressDialog
import android.app.RemoteInput
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import app.AppConfig
import app.AppController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.uaeh.aame.cobmaestros.R
import helper.SessionManager
import org.json.JSONException
import org.json.JSONObject

class UpdateUserFrg: Fragment() {

    // TAG
    private val TAG = UpdateUserFrg::class.simpleName

    // Variable Session Manager y pDialog
    private lateinit var session: SessionManager
    private val appConfig = AppConfig()

    // Botones y Edit Text
    private lateinit var inputNombre: EditText
    private lateinit var inputApellido: EditText
    private lateinit var inputMateria: EditText
    private lateinit var inputTelefono: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputContrasena: EditText
    private lateinit var inputConfirm: EditText
    private lateinit var btnActualizar: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_update_data, container, false)
        // Progress Dialog y Session Manager
        session = SessionManager(requireActivity().applicationContext)

        // Find View By Id Edit Text y Button
        inputNombre = root.findViewById(R.id.etNombre)
        inputApellido = root.findViewById(R.id.etApellido)
        inputMateria = root.findViewById(R.id.etMateria)
        inputTelefono = root.findViewById(R.id.etTelefono)
        inputEmail = root.findViewById(R.id.etEmail)
        inputContrasena = root.findViewById(R.id.etContraseña)
        inputConfirm = root.findViewById(R.id.etConfirmCont)
        btnActualizar = root.findViewById(R.id.btnUpdateUser)

        btnActualizar.setOnClickListener() {
            actualizar()
        }

        return root
    }

    /**
     * Funcion para actualizar los datos del usuario
     */
    private fun actualizar() {
        // tag used to cancel the request
        val tag_string_req = "req_update"

        // Arreglo con los datos a actualizar
        val maestro = obtenerDatos()

        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_UPDATE, Response.Listener { response ->
            Log.d(TAG, "Update Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error Response: $error")

                // Checar por error en nodo Json
                if (!error) {
                    // Usuario Actualizado
                    // Volver a guardar los valores del maestro
                    val id = jObj.getString("id_prof")
                    val user = jObj.getJSONObject("user")
                    val nombre = user.getString("nombres")
                    val apellido = user.getString("apellidos")
                    val materia = user.getString("materia")
                    val telefono = user.getString("telefono")
                    val email = user.getString("email")
                    val contrasena = user.getString("contraseña")

                    // Guardar los datos actualizados del maestro
                    session.saveDatosUsuario(id, nombre, apellido, materia, telefono, email, contrasena)
                    Toast.makeText(requireActivity().applicationContext, "Tus datos se actualizaron sin problema, verifica al regresar a tus datos.", Toast.LENGTH_SHORT).show()
                } else {
                    // Error al actualizar datos del maestro
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                // Json Error
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Login Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                // Agregando los parametros a enviar al update url
                val params = HashMap<String, String>()
                params["id"] = session.getId()
                params["nombres"] = maestro[0]
                params["apellidos"] = maestro[1]
                params["materia"] = maestro[2]
                params["telefono"] = maestro[3]
                params["email"] = maestro[4]
                params["contraseña"] = maestro[5]
                params["encriptar"] = maestro[6]
                return params
            }
        }

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    /**
     * Funcion para recolectar los datos de los edit text
     * Si hay campos vacios se mandan los datos guardados anteriormente
     */
    private fun obtenerDatos(): Array<String> {
        val nomb: String
        val apel: String
        val mate: String
        val tel: String
        val email: String
        if (inputNombre.text.toString().isNotEmpty())
            nomb = inputNombre.text.toString()
        else
            nomb = session.getNombre()
        if (inputApellido.text.toString().isNotEmpty())
            apel = inputApellido.text.toString()
        else
            apel = session.getApellidos()
        if (inputMateria.text.toString().isNotEmpty())
            mate = inputMateria.text.toString()
        else
            mate = session.getMateria()
        if (inputTelefono.text.toString().isNotEmpty())
            tel = inputTelefono.text.toString()
        else
            tel = session.getTel()
        if (inputEmail.text.toString().isNotEmpty())
            email = inputEmail.text.toString()
        else
            email = session.getMail()

        lateinit var contra: String
        lateinit var cambiar: String
        if (inputContrasena.text.toString().isNotEmpty() && inputConfirm.text.toString().isNotEmpty()){
            if (inputContrasena.text.toString() == inputConfirm.text.toString()) {
                contra = inputContrasena.text.toString()
                cambiar = "si"
            } else
                Toast.makeText(requireActivity().applicationContext, "Por favor verifica la contraseña", Toast.LENGTH_SHORT).show()
        } else {
            contra = session.getCont()
            cambiar = "no"
        }

        val datosMaestro = arrayOf(nomb, apel, mate, tel, email, contra, cambiar)
        return datosMaestro
    }

}