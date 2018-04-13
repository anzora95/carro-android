package ues.fia.eisi.os_androino_car;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import ues.fia.eisi.os_androino_car.R;

public class GestionUsuario extends Activity {
	ListView lista;
	Dialog listDialog;
	LayoutInflater inflater;
	String seleccion="";
	Context contexto = this;
	ControlDB base;
	OnClickListener OCListenerC = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			base.abrir();
			String[] listaUsuarios=base.consultarTodosUsuarios();
			base.cerrar();
			if(listaUsuarios!=null){
			listDialog = new Dialog(contexto);
			LayoutInflater li;
			View view;
			
			listDialog.setTitle("Seleccionar Usuario");
			li = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = li.inflate(R.layout.lista_usuarios,null,false);
			listDialog.setContentView(view);
			listDialog.setCancelable(true);
			
			final ListView list1 = (ListView)listDialog.findViewById(R.id.listv);
			list1.setAdapter(new ArrayAdapter<String>(contexto,android.R.layout.simple_list_item_1,listaUsuarios));
			list1.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					seleccion = list1.getItemAtPosition(arg2).toString();
					Intent SelectedActivity = new Intent(getApplicationContext(),
							ModificarUsuario.class);
					SelectedActivity.putExtra("usuario",seleccion);
							startActivity(SelectedActivity);
							listDialog.cancel();
				}
			});
			listDialog.show();
			} else Toast.makeText(contexto, "No hay usuarios registrados", Toast.LENGTH_SHORT).show();
		}
	};
OnClickListener OCListenerE = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			base.abrir();
			String[] listaUsuarios=base.consultarTodosUsuarios();
			base.cerrar();
			if(listaUsuarios!=null){
			listDialog = new Dialog(contexto);
			LayoutInflater li;
			View view;
			
			listDialog.setTitle("Seleccionar Usuario");
			li = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = li.inflate(R.layout.lista_usuarios,null,false);
			listDialog.setContentView(view);
			listDialog.setCancelable(true);
			
			final ListView list1 = (ListView)listDialog.findViewById(R.id.listv);
			list1.setAdapter(new ArrayAdapter<String>(contexto,android.R.layout.simple_list_item_1,listaUsuarios));
			list1.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					seleccion = list1.getItemAtPosition(arg2).toString();
					base.abrir();
					if(base.eliminaUsuario(seleccion)){
						Toast.makeText(contexto, "Usuario eliminado", Toast.LENGTH_SHORT).show();
					} else Toast.makeText(contexto, "Usuario no eliminado", Toast.LENGTH_SHORT).show();
					base.cerrar();
					listDialog.cancel();
				}
			});
			listDialog.show();
			}
			else Toast.makeText(contexto, "no hay usuarios registrados", Toast.LENGTH_SHORT).show();
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.gestion_usuario);
		base=new ControlDB(contexto);
		base.abrir();
		base.cerrar();
		Button btn1 = (Button)findViewById(R.id.bnt1);
		btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Agregar un nuevo usuario en la base de datos de la aplicacion
				Intent SelectedActivity = new Intent(getApplicationContext(),
						CrearUsuario.class);
						startActivity(SelectedActivity);
			}
		});
		Button btn2 = (Button)findViewById(R.id.bnt2);
		btn2.setOnClickListener(OCListenerC);
		Button btn3 = (Button)findViewById(R.id.bnt3);
		btn3.setOnClickListener(OCListenerE);
	}
}
