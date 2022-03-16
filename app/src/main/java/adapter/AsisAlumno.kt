package adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.uaeh.aame.cobmaestros.R

class AsisAlumno(private val _context: Activity, private val id: ArrayList<String>, private val nombre: ArrayList<String>, private val asistencia: ArrayList<Int>):
    ArrayAdapter<String>(_context, R.layout.list_asistencias, id){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = _context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_asistencias, null, true)

        val nom = rowView.findViewById<TextView>(R.id.asis_alumno)
        val numero = rowView.findViewById<TextView>(R.id.num_asis)
        val imagen = rowView.findViewById<ImageView>(R.id.asistencia)

        nom.text = nombre[position]
        numero.text = "${position + 1}.-"

        if (asistencia[position] == 1) {
            imagen.setImageResource(R.drawable.ic_positive)
        } else if (asistencia[position] == 0) {
            imagen.setImageResource(R.drawable.ic_negative)
        }

        return rowView
    }

}