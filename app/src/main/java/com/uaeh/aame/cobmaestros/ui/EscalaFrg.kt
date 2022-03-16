package com.uaeh.aame.cobmaestros.ui

import adapter.AvisosAdapter
import adapter.Escala1
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.AppConfig
import app.AppController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.uaeh.aame.cobmaestros.R
import helper.SessionManager
import kotlinx.coroutines.joinAll
import org.json.JSONException
import org.json.JSONObject

class EscalaFrg : Fragment() {

    // Botones y Edit Text
    private lateinit var etAspecto: EditText
    private lateinit var etPorcentaje: EditText
    private lateinit var etParcial: EditText
    private lateinit var btnAgregar: Button
    private lateinit var btnEditar: Button
    private lateinit var btnRemover: Button

    // Session Manager appConfig TAG
    private lateinit var session: SessionManager
    private val appConfig = AppConfig()
    private val TAG = EscalaFrg::class.simpleName

    // Adaptador de ListView 1 2 3
    private lateinit var listView1: ListView
    private lateinit var listView2: ListView
    private lateinit var listView3: ListView
    private lateinit var adapter1: Escala1
    private lateinit var adapter2: Escala1
    private lateinit var adapter3: Escala1

    // arreglo 1
    private lateinit var Id1: ArrayList<String>
    private lateinit var Aspecto1: ArrayList<String>
    private lateinit var Porcentaje1: ArrayList<String>

    // arreglo 2
    private lateinit var Id2: ArrayList<String>
    private lateinit var Aspecto2: ArrayList<String>
    private lateinit var Porcentaje2: ArrayList<String>

    // arreglo 3
    private lateinit var Id3: ArrayList<String>
    private lateinit var Aspecto3: ArrayList<String>
    private lateinit var Porcentaje3: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_escala, container, false)
        session = SessionManager(requireActivity().applicationContext)

        // Array List 1
        Id1 = ArrayList()
        Aspecto1 = ArrayList()
        Porcentaje1 = ArrayList()

        // Array List 2
        Id2 = ArrayList()
        Aspecto2 = ArrayList()
        Porcentaje2 = ArrayList()

        // Array 3
        Id3 = ArrayList()
        Aspecto3 = ArrayList()
        Porcentaje3 = ArrayList()

        // Botones y Edit Text View FindView
        btnAgregar = root.findViewById(R.id.anadir_aspecto)
        btnEditar = root.findViewById(R.id.editar_aspecto)
        btnRemover = root.findViewById(R.id.remover_aspecto)
        etAspecto = root.findViewById(R.id.aspecto)
        etParcial = root.findViewById(R.id.parcial)
        etPorcentaje = root.findViewById(R.id.porcentaje)

        // List view 1 parcial
        listView1 = root.findViewById(R.id.lista_parcial_1)
        listView2 = root.findViewById(R.id.lista_parcial_2)
        listView3 = root.findViewById(R.id.lista_parcial_3)

        adapter1 = Escala1(activity as Activity, Id1, Aspecto1, Porcentaje1)
        adapter2 = Escala1(activity as Activity, Id2, Aspecto2, Porcentaje2)
        adapter3 = Escala1(activity as Activity, Id3, Aspecto3, Porcentaje3)
        listView1.adapter = adapter1
        listView2.adapter = adapter2
        listView3.adapter = adapter3

        btnEditar.visibility = View.INVISIBLE
        btnRemover.visibility = View.INVISIBLE
        btnAgregar.visibility = View.VISIBLE

        var Pos = 0
        var Parcial = 0
        // Primer List View
        listView1.setOnItemClickListener{
            adapterView, view, position, id ->
            etAspecto.hint = Aspecto1[position]
            etPorcentaje.hint = Porcentaje1[position]
            etParcial.visibility = View.INVISIBLE
            Pos = position
            Parcial = 1
            btnAgregar.visibility = View.INVISIBLE
            btnEditar.visibility = View.VISIBLE
            btnRemover.visibility = View.VISIBLE
        }

        // Segundo List View
        listView2.setOnItemClickListener{
                adapterView, view, position, id ->
            etAspecto.hint = Aspecto2[position]
            etPorcentaje.hint = Porcentaje2[position]
            etParcial.visibility = View.INVISIBLE
            Pos = position
            Parcial = 2
            btnAgregar.visibility = View.INVISIBLE
            btnEditar.visibility = View.VISIBLE
            btnRemover.visibility = View.VISIBLE
        }

        // Tercer List View
        listView3.setOnItemClickListener{
                adapterView, view, position, id ->
            etAspecto.hint = Aspecto3[position]
            etPorcentaje.hint = Porcentaje3[position]
            etParcial.visibility = View.INVISIBLE
            Pos = position
            Parcial = 3
            btnAgregar.visibility = View.INVISIBLE
            btnEditar.visibility = View.VISIBLE
            btnRemover.visibility = View.VISIBLE
        }

        btnAgregar.setOnClickListener{
            if (etAspecto.text.isEmpty() && etPorcentaje.text.isEmpty() && etParcial.text.isEmpty()){
                Toast.makeText(requireActivity().applicationContext, "Un dato se encuentra vacio!", Toast.LENGTH_SHORT).show()
            } else {
                val asp = etAspecto.text.toString()
                val porc = etPorcentaje.text.toString()
                val parc = etParcial.text.toString()
                val maest = session.getId()
                etAspecto.setText("")
                etPorcentaje.setText("")
                etParcial.setText("")
                etAspecto.setHint(R.string.hint_aspecto)
                etPorcentaje.setHint(R.string.hint_porcentaje)
                etParcial.setHint(R.string.hint_parcial)

                addAspecto(asp, porc, parc, maest)
            }
        }

        btnEditar.setOnClickListener{
            if (etAspecto.text.isEmpty() && etPorcentaje.text.isEmpty()) {
                Toast.makeText(requireActivity().applicationContext, "Un dato se encuentra vacio!", Toast.LENGTH_SHORT).show()
            } else{
                val asp = etAspecto.text.toString()
                val porc = etPorcentaje.text.toString()
                var apId: String = ""
                etAspecto.setText("")
                etAspecto.setHint(R.string.hint_aspecto)
                etPorcentaje.setText("")
                etPorcentaje.setHint(R.string.hint_porcentaje)
                etParcial.visibility = View.VISIBLE
                if (Parcial == 1){
                    apId = Id1[Pos]
                } else if (Parcial == 2){
                    apId = Id2[Pos]
                } else if (Parcial == 3){
                    apId = Id3[Pos]
                }
                editAspecto(apId, asp, porc, Parcial.toString(), Pos)
                Log.d(TAG, "Id del aviso es: $apId")
                btnEditar.visibility = View.INVISIBLE
                btnRemover.visibility = View.INVISIBLE
                btnAgregar.visibility = View.VISIBLE
                //Toast.makeText(requireActivity().applicationContext, "la posicion a editar fue ${Pos + 1}, del parcial ${Parcial} ", Toast.LENGTH_SHORT).show()
            }
        }

        btnRemover.setOnClickListener{
            var apId = ""
            if (Parcial == 1){
                apId = Id1[Pos]
            } else if (Parcial == 2){
                apId = Id2[Pos]
            } else if (Parcial == 3){
                apId = Id3[Pos]
            }
            etAspecto.setText("")
            etAspecto.setHint(R.string.hint_aspecto)
            etPorcentaje.setText("")
            etPorcentaje.setHint(R.string.hint_porcentaje)
            etParcial.visibility = View.VISIBLE

            deleteAspecto(apId, Pos, Parcial)

            Log.d(TAG, "Id del aviso es: $apId")
            btnEditar.visibility = View.INVISIBLE
            btnRemover.visibility = View.INVISIBLE
            btnAgregar.visibility = View.VISIBLE
            //Toast.makeText(requireActivity().applicationContext, "la posicion a eliminar fue ${Pos + 1}, del parcial ${Parcial} ", Toast.LENGTH_SHORT).show()
        }

        getEscala()

        return root
    }

    private fun addAspecto(aspecto: String, porcentaje: String, parcial: String, maestro: String){
        val tag_string_req = "req_add_aspecto"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_AGREGAR_ASPECTO, Response.Listener { response ->
            Log.d(TAG, "Escala Add Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    val id_aspecto = jObj.getString("id_aspecto")
                    val aspecto = jObj.getJSONObject("aspecto")
                    val asp = aspecto.getString("aspec")
                    val porc = aspecto.getString("porcentaje")
                    val par = aspecto.getString("parcial")

                    addItemLista(id_aspecto, asp, porc, par)
                    Toast.makeText(requireActivity().applicationContext, "Aspecto añadido a la escala correctamente", Toast.LENGTH_SHORT)
                        .show()
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
            Log.d(TAG, "Escala anadir aspecto error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["aspecto"] = aspecto
                params["porcentaje"] = porcentaje
                params["parcial"] = parcial
                params["maestro"] = maestro
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun getEscala(){
        val tag_string_req = "req_get_aspectos"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_OBTENER_ESCALA, Response.Listener { response ->
            Log.d(TAG, "Escala Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    val tam = jObj.length()-1
                    for (indice in 0 until tam){
                        val aspecto = jObj.getJSONObject(indice.toString())
                        val id = aspecto.getString("id_aspecto")
                        val asp = aspecto.getString("aspecto")
                        val por = aspecto.getString("porcentaje")
                        val parc = aspecto.getString("parcial")
                        addItemLista(id, asp, por, parc)
                    }
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
            Log.d(TAG, "Escala anadir aspecto error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["maestro"] = session.getId()
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun editAspecto(id_aspecto: String, aspecto: String, porcentaje: String, parcial: String, posicion: Int) {
        val tag_string_req = "req_edit_aspecto"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_EDITAR_ASPECTO, Response.Listener { response ->
            Log.d(TAG, "Escala Edit Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    val id = jObj.getString("id_aspecto")
                    val Aspecto = jObj.getJSONObject("aspecto")
                    val asp = Aspecto.getString("aspec")
                    val porc = Aspecto.getString("porcentaje")

                    updateItemLista(posicion, parcial, asp, porc)
                    Toast.makeText(requireActivity().applicationContext, "Aspecto actualizado correctamente", Toast.LENGTH_SHORT)
                        .show()
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
            Log.d(TAG, "Escala editar aspecto error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_aspecto"] = id_aspecto
                params["aspecto"] = aspecto
                params["porcentaje"] = porcentaje
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun deleteAspecto(id_aspecto: String, posicion: Int, parcial: Int) {
        val tag_string_req = "req_delete_aspecto"
        val strReq: StringRequest = object : StringRequest(Method.POST, appConfig.URL_DELETE_ASPECTO, Response.Listener { response ->
            Log.d(TAG, "Aviso update Response: $response")
            try {
                val jObj = JSONObject(response.substring(response.indexOf("{"), response.length))
                val error = jObj.getBoolean("error")
                Log.d(TAG, "Error response: $error")

                // Checar si es de a debis
                if (!error) {
                    removerItemLista(posicion, parcial)
                    Toast.makeText(requireActivity().applicationContext, "Aspecto Removido Correctamente!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val errorMsg = jObj.getString("msg")
                    Toast.makeText(requireActivity().applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException){
                e.printStackTrace()
                Toast.makeText(requireActivity().applicationContext, "Json error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "Aspecto Eliminar Error: ${error.message}")
            Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_aspecto"] = id_aspecto
                return params
            }
        }
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req)
    }

    private fun addItemLista(id: String, aspecto: String, porcentaje: String, parcial: String){
        if (parcial == "1") {
            Id1.add(id)
            Aspecto1.add(aspecto)
            Porcentaje1.add(porcentaje)
            listView1.adapter = adapter1
        } else if (parcial == "2") {
            Id2.add(id)
            Aspecto2.add(aspecto)
            Porcentaje2.add(porcentaje)
            listView2.adapter = adapter2
        } else if (parcial == "3") {
            Id3.add(id)
            Aspecto3.add(aspecto)
            Porcentaje3.add(porcentaje)
            listView3.adapter = adapter3
        }
    }

    private fun updateItemLista(posicion: Int, parcial: String, aspecto: String, porcentaje: String){
        if (parcial == "1") {
            Aspecto1[posicion] = aspecto
            Porcentaje1[posicion] = porcentaje
            listView1.adapter = adapter1
        } else if (parcial == "2") {
            Aspecto2[posicion] = aspecto
            Porcentaje2[posicion] = porcentaje
            listView2.adapter = adapter2
        } else if (parcial == "3") {
            Aspecto3[posicion] = aspecto
            Porcentaje3[posicion] = porcentaje
            listView3.adapter = adapter3
        }
    }

    private fun removerItemLista(posicion: Int, parcial: Int) {
        if (parcial == 1) {
            Id1.removeAt(posicion)
            Aspecto1.removeAt(posicion)
            Porcentaje1.removeAt(posicion)
            listView1.adapter = adapter1
        } else if (parcial == 2) {
            Id2.removeAt(posicion)
            Aspecto2.removeAt(posicion)
            Porcentaje2.removeAt(posicion)
            listView2.adapter = adapter2
        } else if (parcial == 3) {
            Id3.removeAt(posicion)
            Aspecto3.removeAt(posicion)
            Porcentaje3.removeAt(posicion)
            listView3.adapter = adapter3
        }
    }
}