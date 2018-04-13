package ues.fia.eisi.os_androino_car;

import java.io.File;
import ues.fia.eisi.os_androino_car.R;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CrearUsuario extends Activity {
	Context contexto = this;
	ControlDB base;
	private String rutaImagen = "";
	String usuario="";
	ImageView iv;
	Intent intent;
	LinearLayout linear1,linear2;
	RelativeLayout relative1;
	EditText edt1, edt2,edt3;
	File file = new File(Environment.getExternalStorageDirectory()+"/Andrino/images/");
	Bitmap bm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.crear_usuario);
		// Definimos posicion horizontal
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		base = new ControlDB(contexto);
		base.abrir();
		base.cerrar();

		//creando archivo para guardar la imagen
		if(!file.exists())
			file.mkdirs();

		linear1 = (LinearLayout)findViewById(R.id.linear1);
		linear2 = (LinearLayout)linear1.findViewById(R.id.linear2);
		relative1 = (RelativeLayout)linear1.findViewById(R.id.relative1);
		iv = (ImageView)linear2.findViewById(R.id.imgView);		
		edt1 = (EditText)linear1.findViewById(R.id.edt1);
		edt2 = (EditText)linear1.findViewById(R.id.edt2);
		edt3 = (EditText)linear1.findViewById(R.id.edt3);
		Button btn1 = (Button)relative1.findViewById(R.id.btn1);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(validarNombreUsuario(edt1.getText().toString())){
					usuario=edt1.getText().toString()+".jpg";
					rutaImagen = file.getAbsolutePath().toString()+"/"+usuario;
					intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Uri output = Uri.fromFile(new File(rutaImagen));
					intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
					startActivityForResult(intent, 1);}
				else{
					Toast.makeText(CrearUsuario.this, "Ingrese un nombre de usuario valido", Toast.LENGTH_LONG).show();
				}
			}
		});
		Button btn2 = (Button)relative1.findViewById(R.id.btn2);
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String val = "";
				if(edt1.getText().toString().length()<1){
					val+="No ha ingresado un nombre para el Usuario";
				}
				if(!validarNombreUsuario(edt1.getText().toString())){
					if(!val.equals(""))
						val+="\n";
					val+="Nombre de usuario no valido";
				}
				if(edt2.getText().toString().length()<8||edt3.getText().toString().length()<8){
					if(!val.equals(""))
						val+="\n";
					val+="Campo(s) de contraseña con menos de 8 caracteres";
				}
				if(!edt2.getText().toString().equals(edt3.getText().toString())){
					if(!val.equals(""))
						val+="\n";
					val+="Contraseñas no coinciden";

				}
				if(rutaImagen.length()<1){
					val+="\n";
					val+="Foto de perfil no ha sido tomada";
				}
				if(!val.equals("")){
					Toast.makeText(CrearUsuario.this, val, Toast.LENGTH_LONG).show();
				}
				else{

					Usuario user = new Usuario();
					user.setNombreUsuario(edt1.getText().toString());
					user.setPasswordUsuario(edt2.getText().toString());
					user.setRutaImagen(rutaImagen);

					base.abrir();
					if(base.consultaUsuario(user.getNombreUsuario())==null){
						if(base.ingresaUsuario(user)){
							Toast.makeText(CrearUsuario.this, "Usuario ingresado con exito", Toast.LENGTH_LONG).show();
							finish();
						}
						else {Toast.makeText(CrearUsuario.this, "Usuario no ingresado al sistema", Toast.LENGTH_LONG).show();
						iv.setImageResource(R.drawable.icono1);
						edt1.setText(null);
						edt2.setText(null);
						edt3.setText(null);
						}
					}
					else  Toast.makeText(CrearUsuario.this, "Usuario ya existe", Toast.LENGTH_LONG).show();}
			}
		});

		configBMFactory();
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		usuario=edt1.getText().toString()+".jpg";
		rutaImagen = file.getAbsolutePath().toString()+"/"+usuario;
		if(rutaImagen.length()>0){
			bm = BitmapFactory.decodeFile(rutaImagen);
			iv.setImageBitmap(bm);
		}
		else{
			Toast.makeText(contexto, "No se encontro la imagen de Usuario", Toast.LENGTH_SHORT).show();
		}

		//Toast.makeText(CrearUsuario.this, nombre, Toast.LENGTH_LONG).show();
		//iv.setImageBitmap(bm);
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
