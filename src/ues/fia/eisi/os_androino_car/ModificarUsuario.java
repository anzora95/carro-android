package ues.fia.eisi.os_androino_car;

import java.io.File;
import ues.fia.eisi.os_androino_car.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ModificarUsuario extends Activity {
	Bundle bundle;
	Intent intent;
	Context contexto = this;
	EditText edt1,edt2,edt3,edt4;
	File file = new File(Environment.getExternalStorageDirectory()+"/Andrino/images/");
	Bitmap bm;
	ImageView iv;
	String nombre = "";
	String usuario="";
	ControlDB base;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.modificar_usuario);
		System.gc();
		bundle = getIntent().getExtras();
		if(!file.exists())
			file.mkdirs();
		base = new ControlDB(contexto);
		base.abrir();
		String[] datosU = base.consultaUsuario(bundle.getString("usuario").toString());
		base.cerrar();
		if(datosU!=null){
		edt1 = (EditText)findViewById(R.id.medt1);
		edt1.setText(bundle.getString("usuario").toString());
		edt1.setEnabled(false);
		edt2 = (EditText)findViewById(R.id.medt2);
		edt3 = (EditText)findViewById(R.id.medt3);
		edt4 = (EditText)findViewById(R.id.medt4);
		iv = (ImageView)findViewById(R.id.mimgView);
		configBMFactory();
		dibujarFoto();
		}
		Button btn1 = (Button)findViewById(R.id.mbtn1);
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				usuario=edt1.getText().toString()+".jpg";
				nombre = file.getAbsolutePath().toString()+"/"+usuario;

				intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri output = Uri.fromFile(new File(nombre));
				intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
				startActivityForResult(intent, 1);
			}
		});
		Button btn2 = (Button)findViewById(R.id.mbtn2);
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String val = "";
				base.abrir();
				if(!base.consultaPass(bundle.get("usuario").toString(),edt2.getText().toString())){
					val+="Contraseña incorrecta";
				}
				base.cerrar();
				if((edt3.getText().toString().length()>0&&edt4.getText().toString().length()>0)){
					if(edt3.getText().toString().length()<8||edt4.getText().toString().length()<8){
					if(!val.equals(""))
						val+="\n";
					val+="Campo(s) de contraseña nueva con menos de 8 caracteres";
					}
					if(!edt4.getText().toString().equals(edt3.getText().toString())){
					if(!val.equals(""))
						val+="\n";
					val+="Contraseñas Nuevas no coinciden";
					}
				}
				if(edt3.getText().toString().length()>0&&edt4.getText().toString().length()<1){
					if(!val.equals(""))
						val+="\n";
					val+="Contraseña Nueva no comprobada";
				}
				if(edt4.getText().toString().length()>0&&edt3.getText().toString().length()<1){
					if(!val.equals(""))
						val+="\n";
					val+="Se valida una nueva contraseña sin colocarla";
				}
				
				
				
				if(!val.equals("")){
					Toast.makeText(ModificarUsuario.this, val, Toast.LENGTH_LONG).show();
					edt2.setText(null);
					edt3.setText(null);
					edt4.setText(null);
				}
				else{
					//escribir en la base
					base.abrir();
					Usuario user=new Usuario();
					user.setNombreUsuario(edt1.getText().toString());
					if(edt3.getText().toString().length()>0)
						user.setPasswordUsuario(edt3.getText().toString());
					else user.setPasswordUsuario(edt2.getText().toString());
					if(bundle.getString("usuario").toString().equals(edt1.getText().toString()))
						nombre = file.getAbsolutePath().toString()+"/"+bundle.getString("usuario").toString()+".jpg";
					else nombre = file.getAbsolutePath().toString()+"/"+edt1.getText().toString()+".jpg";
					user.setRutaImagen(nombre);
					
					base.actualizaUsuario(user,bundle.get("usuario").toString());
					base.cerrar();
					Toast.makeText(ModificarUsuario.this, "Modificacion de usuario exitosa", Toast.LENGTH_LONG).show();
					edt2.setText(null);
					edt3.setText(null);
					edt4.setText(null);
					finish();
					
				}

			}
		});
	}

	public void dibujarFoto(){
		try {
			if(bundle.getString("usuario").toString().equals(edt1.getText().toString()))
				nombre = file.getAbsolutePath().toString()+"/"+bundle.getString("usuario").toString()+".jpg";
			else nombre = file.getAbsolutePath().toString()+"/"+edt1.getText().toString()+".jpg";
			bm.prepareToDraw();
			bm = BitmapFactory.decodeFile(nombre);
		} catch (Exception e) {
			if(bundle.getString("usuario").toString().equals(edt1.getText().toString()))
				nombre = file.getAbsolutePath().toString()+"/"+bundle.getString("usuario").toString()+".jpg";
			else nombre = file.getAbsolutePath().toString()+"/"+edt1.getText().toString()+".jpg";
			new BitmapFactory();
			bm = BitmapFactory.decodeFile(nombre);
		}
		iv.setImageBitmap(bm);
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

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		dibujarFoto();
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
