package adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.uaeh.aame.cobmaestros.R

class Alumno(private val _context: Activity, private val id: ArrayList<String>, private val nombre: ArrayList<String> ):
    ArrayAdapter<String>(_context, R.layout.list_alumnos, id){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = _context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_alumnos, null, true)

        val num = rowView.findViewById<TextView>(R.id.numAl)
        val Nombre = rowView.findViewById<TextView>(R.id.nombreAl)

        num.text = "${position + 1}.- "
        Nombre.text = nombre[position]

        return rowView
    }
}