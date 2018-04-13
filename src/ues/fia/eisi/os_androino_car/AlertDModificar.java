package ues.fia.eisi.os_androino_car;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ues.fia.eisi.os_androino_car.R;

public class AlertDModificar {

	TextView texto;
	EditText nombre;
	EditText radio;
	Context contexto;
	String lat, lon, dist, nombreA;
	AlertDialog b;
	ControlDB base;
	public AlertDModificar(Context context,String nombreM, ControlDB bas) {
		nombreA = nombreM;
		base = bas;
		base.abrir();
		String [] datosR = base.consultaRestriccion(nombreM);
		base.cerrar();
		if(datosR != null){
		lat = datosR[3];
		lon = datosR[4];
		dist = datosR[5];
		contexto = context;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inf = ((Activity) context).getLayoutInflater();
		View layout = inf.inflate(R.layout.insertar,null);
		texto = (TextView)layout.findViewById(R.id.tv1);
		texto.setText("Latitud: "+lat+"\nLongitud: "+lon);
		nombre = (EditText)layout.findViewById(R.id.edit1);
		nombre.setText(nombreM);
		radio = (EditText)layout.findViewById(R.id.edit2);
		radio.setText(dist);
		builder.setView(layout);
		builder.setTitle("Modificar lugar");
		builder.setPositiveButton("Modificar", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(radio.getText().toString().length()>0&&nombre.getText().toString().length()>0)	{		
				Restriccion rest = new Restriccion();
				rest.setDistancia(radio.getText().toString());
				rest.setNombrePunto(nombre.getText().toString());
				
				base.abrir();
				if(base.actualizaRestriccion(rest,nombreA)){
					Toast.makeText(contexto, "Modificado con exito", Toast.LENGTH_SHORT).show();
				} else Toast.makeText(contexto, "NO ha sido modificado", Toast.LENGTH_SHORT).show();
				
				b.cancel();}
			}
		});
		builder.setNegativeButton("Cancelar", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setCancelable(false);
		b = builder.create();
		b.show();}
		else{
			Toast.makeText(contexto, "No hay registros de Usuario", Toast.LENGTH_SHORT).show();
		}
	}


	public void modificar(String[] datos){
		
		base.abrir();
		
		if(base.insertar(datos)){
			Toast.makeText((Activity)contexto, "Exito", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText((Activity)contexto, "Fallo insertar", Toast.LENGTH_SHORT).show();
		}
		
		base.cerrar();
		
	}
}
