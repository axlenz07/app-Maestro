package helper

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log

class SessionManager {

    // Logcat Tag
    private val TAG = SessionManager::class.simpleName

    // Shared Preferences
    var pref: SharedPreferences
    var editor: Editor
    var _context: Context

    // Shared pref mode
    var PRIVATE_MODE = 0

    // Shared preferences file name
    private val PREF_NAME = "LoginSQL"
    private val KEY_IS_LOGGEDIN = "isLoggedIn"

    constructor(context: Context) {
        this._context = context
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        editor.apply()
    }

    fun setLogin(isLoggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn)

        // commit changes
        editor.commit()
        Log.d(TAG, "Sesion del Usuario modificado")
    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false)
    }

    fun saveDatosUsuario(id_maestro: String, nombre: String, apellidos: String, materia: String, telefono: String, email: String, contraseña: String) {
        editor.putString("MaestroId", id_maestro)
        editor.putString("Nombre", nombre)
        editor.putString("Apellidos", apellidos)
        editor.putString("Materia", materia)
        editor.putString("Telefono", telefono)
        editor.putString("Correo", email)
        editor.putString("Contraseña", contraseña)
        editor.commit()
        Log.d(TAG, "Datos del Usuario Guardados")
    }

    fun saveIdPadre(id_padre: String){
        editor.putString("Padre", id_padre)
        editor.commit()
        Log.d(TAG, "Dato del Padre guardado")
    }

    fun getIdPadre(): String{
        return pref.getString("Padre", "null").toString()
    }

    fun getId(): String {
        return pref.getString("MaestroId", "null").toString()
    }

    fun getMail(): String {
        return pref.getString("Correo", "sincorreo@null").toString()
    }

    fun getNombre(): String {
        return pref.getString("Nombre", "null").toString()
    }

    fun getApellidos(): String {
        return pref.getString("Apellidos", "null").toString()
    }

    fun getMateria(): String{
        return pref.getString("Materia", "null").toString()
    }

    fun getTel(): String {
        return pref.getString("Telefono", "null").toString()
    }

    fun getCont(): String {
        return pref.getString("Contraseña", "null").toString()
    }
}