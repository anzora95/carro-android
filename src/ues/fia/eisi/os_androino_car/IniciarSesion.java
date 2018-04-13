package ues.fia.eisi.os_androino_car;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class IniciarSesion extends Activity {


	String usuario="";
	ImageView iv;
	Intent intent;
	LinearLayout lv1;
	TextView edt1;
	EditText edt2;
	Dialog listDialog;
	String seleccion, rutaImagen;
	File file = new File(Environment.getExternalStorageDirectory()+"/Andrino/images/");
	Bitmap bm;
	ControlDB base;
	Context contexto = this;
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

						usuario = list1.getItemAtPosition(arg2).toString();
						edt1.setText(usuario);
						listDialog.cancel();
						dibujar();

					}
				});
				listDialog.show();
			} else Toast.makeText(contexto, "No hay usuarios registrados", Toast.LENGTH_SHORT).show();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.iniciar_sesion);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.iniciar_sesion);

		// Definimos posicion horizontal
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		base=new ControlDB(contexto);
		base.abrir();
		base.cerrar();

		//creando archivo para guardar la imagen
		if(!file.exists())
			file.mkdirs();
		LinearLayout linear1 = (LinearLayout)findViewById(R.id.linear1);
		LinearLayout linear2 = (LinearLayout)linear1.findViewById(R.id.linear3);
		iv = (ImageView)linear2.findViewById(R.id.imgView);
		edt1 = (TextView)linear1.findViewById(R.id.edt1);
		edt1.setOnClickListener(OCListenerC);
		edt2 = (EditText)linear1.findViewById(R.id.edt2);
		Button btn1 = (Button)linear1.findViewById(R.id.btnUsuarioNuevo);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Class<?> clase = null;

				try {
					clase = Class
							.forName("ues.fia.eisi.os_androino_car.CrearUsuario");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(clase!=null){
					Intent inte = new Intent(contexto, clase);
					startActivity(inte);}else Toast.makeText(contexto, "No se puede", Toast.LENGTH_SHORT).show();
			}
		});
		Button btn2 = (Button)linear1.findViewById(R.id.btnIniciarSesion);
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				base.abrir();
				if(base.consultaPass(edt1.getText().toString(), edt2.getText().toString())){
					base.cerrar();
					try {
						iniciarSig();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						base.cerrar();
						e.printStackTrace();
					}
				}else Toast.makeText(contexto, "Ingrese correctamente Usuario y contraseña", Toast.LENGTH_SHORT).show(); 
				base.cerrar();
			}

		});
		configBMFactory();
	}

	public void iniciarSig() throws ClassNotFoundException{
		
		Class<?> clase;
		clase = Class
				.forName("ues.fia.eisi.os_androino_car.Os_Andrino_Car_Activity");
		Intent inte = new Intent(this, clase);
		startActivity(inte);
	}
	public void dibujar() {
		base.abrir();
		rutaImagen = base.buscarImagen(usuario);
		
		base.cerrar();
		if(rutaImagen.length()>0){
			bm = BitmapFactory.decodeFile(rutaImagen);
			iv.setImageBitmap(bm);
		}
		else{
			Toast.makeText(contexto, "No se encontro la imagen de Usuario", Toast.LENGTH_SHORT).show();
		}

		System.gc();
	}

	public void configBMFactory(){
		BitmapFactory.Options opts=new BitmapFactory.Options();
		opts.inDither=false;                     //Disable Dithering mode
		opts.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		opts.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
		opts.inTempStorage=new byte[32 * 1024]; 
		opts.inSampleSize=6;
	}

	boolean validarNombreUsuario(String nombre){

		for(int i=0;i<nombre.length();i++){
			if(!Character.isLetter(nombre.charAt(i))){
				return false;
			}
		}	
		return true;
	}

}
