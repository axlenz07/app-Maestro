package com.uaeh.aame.cobmaestros.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class HomeFragment : Fragment() {

    private lateinit var session: SessionManager
    private val appConfig = AppConfig()
    private val TAG = HomeFragment::class.simpleName
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }
    /*
    private fun addMateria(){
        val tag_string_req = "req_add_materia"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_MATERIA, Response.Listener { response ->
            Log.d(TAG, "Materia Add Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    Toast.makeText(requireActivity(), "Materia enviado correctamente!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Materia Agregar Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["materia"] = session.getMateria()
                params["maestro"] = session.getId()
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }
     */
}