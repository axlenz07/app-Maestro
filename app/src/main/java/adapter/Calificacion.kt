package adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.uaeh.aame.cobmaestros.R

class Calificacion(private val _context: Activity, private val id: ArrayList<String>, private val aspecto: ArrayList<String>, private val valor: ArrayList<String>,
    private val calificacion: ArrayList<String>): ArrayAdapter<String>(_context, R.layout.list_calificacion , id){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = _context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_calificacion, null, true)

        val num = rowView.findViewById<TextView>(R.id.num_aspecto)
        val aspect = rowView.findViewById<TextView>(R.id.tv_aspec)
        val valEscala = rowView.findViewById<TextView>(R.id.tv_valor)
        val calif = rowView.findViewById<TextView>(R.id.tv_calif)

        val temp = "${position + 1}.- "

        num.text = temp
        aspect.text = aspecto[position]
        valEscala.text = "${valor[position]} %"

        if (calificacion.size != 0){
            if (calificacion.size > position) {
                val cali = "${calificacion[position]} %"
                calif.text = cali
                calif.visibility = View.VISIBLE
            }
            else
                calif.visibility = View.INVISIBLE
        } else {
            calif.visibility = View.INVISIBLE
        }

        return rowView
    }
}