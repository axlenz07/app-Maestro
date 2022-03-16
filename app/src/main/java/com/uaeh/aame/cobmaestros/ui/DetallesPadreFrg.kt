package com.uaeh.aame.cobmaestros.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.AppConfig
import app.AppController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.uaeh.aame.cobmaestros.R
import helper.SessionManager
import org.json.JSONException
import org.json.JSONObject

class DetallesPadreFrg : Fragment() {

    // TAG session
    private val TAG = DetallesPadreFrg::class.simpleName
    private lateinit var session: SessionManager
    private val appConfig = AppConfig()

    // EditText
    private lateinit var tvNombre: TextView
    private lateinit var tvApellido: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvTel: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = layoutInflater.inflate(R.layout.fragment_detalles_padre, container, false)

        // Session
        session = SessionManager(requireActivity().applicationContext)

        // Text View
        tvNombre = root.findViewById(R.id.tvNombresP)
        tvApellido = root.findViewById(R.id.tvApellidosP)
        tvEmail = root.findViewById(R.id.tvEmailP)
        tvTel = root.findViewById(R.id.tvTelefonoP)

        tvNombre.visibility = View.INVISIBLE
        tvApellido.visibility = View.INVISIBLE
        tvEmail.visibility = View.INVISIBLE
        tvTel.visibility = View.INVISIBLE
        getDetalles()

        return root
    }

    private fun getDetalles(){
        val tag_string_req = "req_get_detalles"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_OBTENER_PADRE, Response.Listener { response ->
            Log.d(TAG, "Detalle Padre Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    val id_padre = jObj.getString("id_parent")
                    val padre = jObj.getJSONObject("padre")
                    val nom = padre.getString("nombre")
                    val ape = padre.getString("apellido")
                    val tel = padre.getString("telefono")
                    val mail = padre.getString("email")
                    tvNombre.text = nom
                    tvApellido.text = ape
                    tvEmail.text = mail
                    tvTel.text = tel

                    tvNombre.visibility = View.VISIBLE
                    tvApellido.visibility = View.VISIBLE
                    tvEmail.visibility = View.VISIBLE
                    tvTel.visibility = View.VISIBLE
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Detalle Padre Get error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_papa"] = session.getIdPadre()
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }
}