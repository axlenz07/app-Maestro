package adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.uaeh.aame.cobmaestros.R

class Fecha(private val _context: Activity, private val id: ArrayList<String>, private val date: ArrayList<String> ):
    ArrayAdapter<String>(_context, R.layout.list_fecha, id){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = _context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_fecha, null, true)

        val num = rowView.findViewById<TextView>(R.id.num)
        val fecha = rowView.findViewById<TextView>(R.id.date)

        num.text = "${position + 1}.- "
        fecha.text = date[position]

        return rowView
    }
}