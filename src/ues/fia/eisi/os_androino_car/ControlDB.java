package ues.fia.eisi.os_androino_car;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class ControlDB{

	private static final String[]GPS = new String[]{"idPunto","nombrePunto","latitud","longitud","distancia"};
	private final Context context; 
	private DatabaseHelper DBHelper; 
	private SQLiteDatabase db;


	public ControlDB(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}


	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static final String BASE_DATOS = "Andrino.s3db";
		private static final int VERSION = 1;
		
		private String tablaRestriccion= "CREATE TABLE if not exists Restriccion("
				+"idPunto integer primary key autoincrement,"//0
				+"idUsuario varchar(25) not null,"//1
				+"nombrePunto varchar(25) not null,"//2
				+"latitud varchar(25) not null,"//3
				+"longitud varchar(25) not null,"//4
				+"distancia varchar(25) not null);";//5
		private String tablaUsuario="CREATE TABLE if not exists Usuario("
				+"idUsuario integer primary key autoincrement,"
				+"nombreUsuario varchar(25) not null,"
				+"passwordUsuario varchar(30) not null,"
				+"rutaImagen varchar(254) not null);";
		private String triggerFKRestriccionUsuario = "CREATE TRIGGER if not exists fk_restriccion_Usuario before insert on Restriccion"
				+"FOR EACH ROW BEGIN SELECT CASE WHEN ((SELECT idUsuario FROM Usuario WHERE idUSuario = NEW.idUsuario) IS NULL)"
				+"THEN RAISE(ABORT, 'No existe el usuario; no se puede insertar la Restriccion') END;	END;";
		private String triggerTablaRestriccion = "CREATE TRIGGER if not exists Trestriccion before insert on Restriccion"
				+"FOR EACH ROW BEGIN SELECT CASE WHEN ((SELECT nombrePunto FROM Restriccion WHERE nombrePunto = NEW.nombrePunto) IS NOT NULL)"
				+"THEN RAISE(ABORT, 'No puede crearse nuevo punto de restriccion debido a que ya existe') END;	END;";
		private String triggerTablaUsuario = "CREATE TRIGGER if not exists Tusuario before insert on Usuario"
				+"FOR EACH ROW BEGIN SELECT CASE WHEN ((SELECT nombreUsuario FROM Usuario WHERE nombreUsuario = NEW.nombreUsuario) IS NOT NULL)"
				+"THEN RAISE(ABORT, 'No puede crearse nuevo usuario debido a que ya existe') END;	END;";

		public DatabaseHelper(Context context) {
			super(context, BASE_DATOS, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				//creacion de tablas para manejar los puntos GPS
				db.execSQL(tablaRestriccion);
				db.execSQL(tablaUsuario);
				db.execSQL(triggerFKRestriccionUsuario);
				db.execSQL(triggerTablaRestriccion);
				db.execSQL(triggerTablaUsuario);
			}catch(SQLException e){

				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}


	}

	public void abrir() throws SQLException{ 
		db = DBHelper.getWritableDatabase(); return;
	}

	public void cerrar(){
		DBHelper.close();
	}

	public boolean insertar(String[] datos){		
		try {
			db.execSQL("insert into Restriccion values " +
					"(null,"+"0,'"+datos[0]+"','"+datos[1]+"','"+datos[2]+"',"+datos[3]+");");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@SuppressWarnings("static-access")
	public String[] verificarRestriccion(Location loc){
		String[] encontrado={"","","",""};
		Cursor c = db.rawQuery("select * from Restriccion", null);
		String aux[] = new String[c.getCount()];
		
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				double endLatitude = Double.parseDouble(c.getString(3));//se asigna latitud y longitud
				double endLongitude = Double.parseDouble(c.getString(4));
				float  results = 0;
				 float[] a= {0,0,0};
				Location.distanceBetween((double) endLatitude,(double) endLongitude,//Punto Inicial: Tomado de la base de datos
						(double)loc.getLatitude(),(double) loc.getLongitude(),//Punto final: Punto actual
						a);// arreglo Float donde se guardan los resultados... nos interesa a[0] --> distancia en metros
				/*
				results=distFrom(Float.parseFloat(String.valueOf(loc.getLatitude())),Float.parseFloat(String.valueOf(loc.getLongitude())),
				Float.parseFloat(String.valueOf(endLatitude)),Float.parseFloat(String.valueOf(endLongitude))); */
				results = a[0];
				loc.reset();
				if(results<=Float.parseFloat(c.getString(5))){
					encontrado[0] = String.valueOf(results);
					encontrado[1] = c.getString(2);//asigna el nombre del punto para regresarlo como resultado
					encontrado[2] = c.getString(3);
					encontrado[3] = c.getString(4);
					//esta ubicado en el punto "NOMBRE DEL PUNTO"
					
					break;
				}else{
				try {
					c.moveToNext();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				}
			}
			return encontrado;
		} else {
			return null;
		}
	}
	
	//***********************************************METODOS DE GESTION DE TABLAS****************************************************}
	
	//ingresar usuario
	public boolean ingresaUsuario(Usuario usaurio){
		ContentValues contenido = new ContentValues();
		//contenido.put("idUsuario",usaurio.getIdUsuario());  se lo comente pq no se si lo va agregar, aunque lo logico es que no..¬¬ pero no se ¬¬
		contenido.put("nombreUsuario",usaurio.getNombreUsuario());
		contenido.put("passwordUsuario",usaurio.getPasswordUsuario());
		contenido.put("rutaImagen",usaurio.getRutaImagen());
		long ingresado = db.insert("Usuario", null, contenido);
		if(ingresado != 0 || ingresado != -1)
			return true;
		return false;
	}//fin de ingresar usuario
	
	//eliminar usuario
	public boolean eliminaUsuario(String nomUsuario){
		int borrado=db.delete("Usuario", "nombreUsuario=?", new String[]{nomUsuario});
		if(borrado != 0 || borrado != -1)
			return true;
		return false;
	}//fin de elimina usuario
	
	//actualizar usuario
	public boolean actualizaUsuario(Usuario usaurio, String nombUser){
		ContentValues modifica = new ContentValues();
		modifica.put("nombreUsuario", usaurio.getNombreUsuario());
		modifica.put("passwordUsuario",usaurio.getPasswordUsuario());
		modifica.put("rutaImagen",usaurio.getRutaImagen());
		int actualiza = db.update("Usuario", modifica, "nombreUsuario=?", new String[]{usaurio.getNombreUsuario()});
		if(actualiza != 0 || actualiza != -1)
			return true;
		return false;
	}//fin de actualiza
	
	public String buscarImagen(String nomUsuario){
		Cursor t;
		t = db.rawQuery("select rutaImagen from Usuario where nombreUsuario = '" + nomUsuario+"'", null);
		if (t.moveToFirst()) {
			return t.getString(0);
			}
		return null;
	}
	
	//consulta Contraseña Usuario
		boolean consultaPass(String nomUsuario, String passW){
			Cursor t;
			t = db.rawQuery("select * from Usuario where nombreUsuario = '" + nomUsuario+"' and passwordUsuario ='"+passW+"'", null);
			if (t.moveToFirst()) {
				return true;
				}// fin del for
			// fin del if
			return false;
		}//fin de consulta contraseña
	
	
	//consulta Usuario
	public String[] consultaUsuario(String nomUsuario){
		Cursor t;
		String[] unico=null;
		t = db.rawQuery("select * from Usuario where nombreUsuario = '" + nomUsuario+"'", null);
		if (t.moveToFirst()) {
			unico = new String[t.getColumnCount()];
			for (int i = 0; i < unico.length; i++) {
				unico[i] = t.getString(i);
			}// fin del for
		}// fin del if
		return unico;
	}//fin de consulta usuario
	
	//consulta todos los usuarios en la base de datos
	public String[] consultarTodosUsuarios(){
		Cursor t;
		t = db.rawQuery("select nombreUsuario from Usuario", null);
		String[] unicos = null;
		if (t.moveToFirst()) {
			unicos = new String[t.getCount()];
			for (int i = 0; i < unicos.length; i++) {
				unicos[i] = t.getString(0);
				try {
					t.moveToNext();
				} catch (Exception e) {
					
				}
			}// fin del for
		}// fin del if
		else {
			unicos = null;
		}
		return unicos;
	}//fin de consultar todos los usuarios
	
	//eliminar restriccion
		public boolean eliminaRestriccion(String nomPunto){
			int borrado=db.delete("Restriccion", "nombrePunto=?", new String[]{nomPunto});
			if(borrado != 0 || borrado != -1)//Si borro algo
				return true;
			return false;
		}//fin de elimina restriccion
	
	//consulta Restriccion
		public String[] consultaRestriccion(String nomPunto){
			Cursor t;
			String[] unicoPt = null;
			t = db.rawQuery("select * from Restriccion where nombrePunto = '"+ nomPunto+"'", null);
			
			if (t.moveToFirst()) {
				unicoPt = new String[t.getColumnCount()];
				for (int i = 0; i < unicoPt.length; i++) {
					unicoPt[i] = t.getString(i);
					
				}// fin del for
			}// fin del if
			return unicoPt;
		}//fin de consulta restriccion
		
		public boolean actualizaRestriccion(Restriccion res, String nombreAntiguo){
			ContentValues modifica = new ContentValues();
			//modifica.put("idUsuario",usaurio.getIdUsuario());  se lo comente pq no se si lo va actualizar, aunque lo logico es que no..¬¬ pero no se ¬¬
			modifica.put("nombrePunto", res.getNombrePunto());
			modifica.put("distancia",res.getDistancia());
			int actualiza = db.update("Restriccion", modifica, "nombrePunto=?", new String[]{nombreAntiguo});
			if(actualiza != 0 || actualiza != -1)
				return true;
			return false;
		}//fin de actualiza
		
		//consulta todos las restricciones en la base de datos
		public String[] consultarTodasRestricciones(){
			Cursor t;
			String[] unicosPt =null;
			t = db.rawQuery("select nombrePunto from Restriccion", null);
			if (t.moveToFirst()) {
				unicosPt = new String[t.getCount()];
				for (int i = 0; i < unicosPt.length; i++) {
					unicosPt[i] = t.getString(0);
					try {
						t.moveToNext();
					} catch (Exception e) {
						
					}
				}// fin del for
			}// fin del if
			return unicosPt;
		}//fin de consultar todas las restricciones
}//fin de la clase
