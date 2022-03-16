package com.uaeh.aame.cobmaestros.ui

import adapter.AvisosAdapter
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.PointerIcon
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
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

class   AvisosFrg : Fragment() {

    /**
     * Arreglos para los detalles de aviso
     */
    private lateinit var Id: ArrayList<String>
    private lateinit var Titulo: ArrayList<String>
    private lateinit var Description: ArrayList<String>
    private lateinit var Fecha: ArrayList<String>

    /**
     * Botones y Edit text
     */
    private lateinit var btnAgregar: Button
    private lateinit var btnEditar: Button
    private lateinit var btnRemover: Button
    private lateinit var etTitulo: EditText
    private lateinit var etDescripcion: EditText

    // Session Manager y appConfig
    private lateinit var session: SessionManager
    private val appConfig = AppConfig()
    private val TAG = AvisosFrg::class.simpleName

    // Adaptador de ListView
    private lateinit var listView: ListView
    private lateinit var myListAdapter: AvisosAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_avisos, container, false)
        // Inicializacion de los arreglos
        Id = ArrayList()
        Titulo = ArrayList()
        Description = ArrayList()
        Fecha = ArrayList()

        // Botones y Edit Text View FindView
        btnAgregar = root.findViewById(R.id.anadir_aviso)
        btnEditar = root.findViewById(R.id.editar_aviso)
        btnRemover = root.findViewById(R.id.remover_aviso)
        etTitulo = root.findViewById(R.id.asunto)
        etDescripcion = root.findViewById(R.id.descripcion)

        // List view FindView
        listView = root.findViewById(R.id.lista_avisos)
        session = SessionManager(requireActivity().applicationContext)

        myListAdapter = AvisosAdapter(activity as Activity, Id, Titulo, Description, Fecha)
        listView.adapter = myListAdapter

        btnEditar.visibility = View.INVISIBLE
        btnRemover.visibility = View.INVISIBLE

        var Pos: Int = 0
        listView.setOnItemClickListener{
            adapterView, view, position, id ->
            etTitulo.hint = Titulo[position]
            etDescripcion.hint = Description[position]
            Pos = position
            btnEditar.visibility = View.VISIBLE
            btnRemover.visibility = View.VISIBLE
            btnAgregar.visibility = View.INVISIBLE
        }

        btnAgregar.setOnClickListener{
            if (etTitulo.text.isEmpty() && etDescripcion.text.isEmpty()) {
                Toast.makeText(requireActivity().applicationContext, "Un dato se encuentra vacio!", Toast.LENGTH_SHORT).show()
            } else{
                val tit = etTitulo.text.toString()
                val desc = etDescripcion.text.toString()
                val maes = session.getId()
                etTitulo.setText("")
                etTitulo.setHint(R.string.hint_add_titulo)
                etDescripcion.setText("")
                etDescripcion.setHint(R.string.hint_descripcion)
                addAviso(tit, desc, maes)
            }
        }

        btnEditar.setOnClickListener{
            if (etTitulo.text.isEmpty() && etDescripcion.text.isEmpty()) {
                Toast.makeText(requireActivity().applicationContext, "Un dato se encuentra vacio!", Toast.LENGTH_SHORT).show()
            } else{
                val tit = etTitulo.text.toString()
                val desc = etDescripcion.text.toString()
                val avisoID = Id[Pos]
                etTitulo.setText("")
                etTitulo.setHint(R.string.hint_add_titulo)
                etDescripcion.setText("")
                etDescripcion.setHint(R.string.hint_descripcion)

                editAviso(avisoID, tit, desc, Pos)
                Log.d(TAG, "Id del aviso es: $avisoID")
                btnEditar.visibility = View.INVISIBLE
                btnRemover.visibility = View.INVISIBLE
                btnAgregar.visibility = View.VISIBLE
                //Toast.makeText(requireActivity().applicationContext, "la posicion a editar fue ${Pos + 1}, $avisoID ", Toast.LENGTH_SHORT).show()
            }
        }

        btnRemover.setOnClickListener{
            val avisoID = Id[Pos]
            deleteAviso(avisoID, Pos)
            etTitulo.setText("")
            etTitulo.setHint(R.string.hint_add_titulo)
            etDescripcion.setText("")
            etDescripcion.setHint(R.string.hint_descripcion)
            btnEditar.visibility = View.INVISIBLE
            btnRemover.visibility = View.INVISIBLE
            btnAgregar.visibility = View.VISIBLE
            //Toast.makeText(requireActivity().applicationContext,"La posicion a eliminar fue ${Pos + 1}", Toast.LENGTH_SHORT).show()
        }

        getAvisos()
        return root
    }

    private fun getAvisos() {
        val tag_string_req = "req_get_avisos"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_OBTENER_AVISO, Response.Listener { response ->
            Log.d(TAG, "Avisos Respones: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")
                if (!error){
                    val tam = jObj.length()-1
                    for (indice in 0 until tam) {
                        val aviso = jObj.getJSONObject(indice.toString())

                        Id.add(aviso.getString("id_aviso"))
                        Titulo.add(aviso.getString("titulo"))
                        Description.add(aviso.getString("descripcion"))
                        Fecha.add(aviso.getString("fecha"))
                    }
                    listView.adapter = myListAdapter
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e:JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Aviso Agregar Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_maes"] = session.getId()
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun addAviso(titulo: String, descripcion: String, id_maestro: String){
        val tag_string_req = "req_add_aviso"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_AGREGAR_AVISO, Response.Listener { response ->
            Log.d(TAG, "Aviso Add Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    val id_aviso = jObj.getString("id_aviso")
                    val aviso = jObj.getJSONObject("aviso")
                    val titulo = aviso.getString("titulo")
                    val desc = aviso.getString("descripcion")
                    val fecha = aviso.getString("fecha")

                    addItemLista(id_aviso, titulo, desc, fecha)
                    Toast.makeText(requireActivity(), "Aviso enviado correctamente!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Aviso Agregar Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["titulo"] = titulo
                params["descripcion"] = descripcion
                params["maestro"] = id_maestro
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun editAviso(id_aviso: String, titulo: String, descripcion: String, posicion: Int){
        val tag_string_req = "req_update_aviso"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_UPDATE_AVISO, Response.Listener { response ->
            Log.d(TAG, "Aviso update Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    val id_aviso = jObj.getString("id_aviso")
                    val aviso = jObj.getJSONObject("aviso")
                    val titulo = aviso.getString("titulo")
                    val desc = aviso.getString("descripcion")

                    updateItemLiata(posicion, titulo, desc)
                    Toast.makeText(requireActivity().applicationContext, "Aviso Actualizado Correctamente!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Aviso Actualizar Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_aviso"] = id_aviso
                params["titulo"] = titulo
                params["descripcion"] = descripcion
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun deleteAviso(id_aviso: String, posicion: Int){
        val tag_string_req = "req_update_aviso"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_DELETE_AVISO, Response.Listener { response ->
            Log.d(TAG, "Aviso update Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    removerItemLista(posicion)
                    Toast.makeText(requireActivity().applicationContext, "Aviso Removido Correctamente!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val errorMsg = jObj.getString("error_msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Aviso Actualizar Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_aviso"] = id_aviso
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun addItemLista(id: String, titulo: String, descripcion: String, dia: String){
        Id.add(id)
        Titulo.add(titulo)
        Description.add(descripcion)
        Fecha.add(dia)
        listView.adapter = myListAdapter
    }

    private fun updateItemLiata(posicion: Int, titulo: String, descripcion: String){
        Titulo[posicion] = titulo
        Description[posicion] = descripcion
        listView.adapter = myListAdapter
    }

    private fun removerItemLista(posicion: Int){
        Id.removeAt(posicion)
        Titulo.removeAt(posicion)
        Description.removeAt(posicion)
        Fecha.removeAt(posicion)
        myListAdapter.notifyDataSetChanged()
    }
}