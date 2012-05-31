package thgg.android.recifeCam.ui;

import java.util.ArrayList;

import thgg.android.recifeCam.R;
import thgg.android.recifeCam.model.Camera;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdView;

public class RecifeCamActivity extends Activity implements AdListener, OnItemClickListener {	
	
	private ArrayList<Camera> listaCameras;
	
	private AdView adView;
	
	private ProgressDialog progressDialog;
	
	private ListView lvCameras;
	
	private ArrayAdapter<Camera> arrayAdapter;
	
	private AlertDialog adAdicionarCamera;
	
	private SharedPreferences preferences;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        carregarListaPrincipalCameras();
    }

	private void carregarListaPrincipalCameras() {
		listaCameras = carregarCameras(false);
        
        adView = (AdView)findViewById(R.id.adMob1);
        adView.setAdListener(this);
        
        arrayAdapter = new ArrayAdapter<Camera>(this, 
        		android.R.layout.simple_list_item_1,
        		listaCameras);

        lvCameras = (ListView)findViewById(R.id.lvCameras);
        lvCameras.invalidate();
        if (lvCameras != null) {
        	lvCameras.setAdapter(arrayAdapter);
        	lvCameras.setOnItemClickListener(this);
        	registerForContextMenu(this.lvCameras);
        }
	}
    
    @Override
    protected void onStart() {
    	super.onStart();
        if (adView != null)
        {
        	adView.requestFreshAd();
        }        
    }
    
	@Override
	protected void onRestart() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		super.onRestart();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Adicionar Camera");
		menu.add(0, 2, 0, "Exibir Todas");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			View v = LayoutInflater.from(this).inflate(R.layout.adicionar_camera, null); 
			ListView lvAdicionarCamera = (ListView)v.findViewById(R.id.lvAdicionarCameras);
			
			ArrayList<Camera> listaTodasCameras = carregarCameras(true);
			
	        ArrayAdapter<Camera> todasCamerasArrayAdapter = new ArrayAdapter<Camera>(this, 
	        		android.R.layout.simple_list_item_1,
	        		listaTodasCameras);
			lvAdicionarCamera.setAdapter(todasCamerasArrayAdapter);
			
			lvAdicionarCamera.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					setCameraAtiva("camera"+(arg2+1), true);
					adAdicionarCamera.cancel();
					carregarListaPrincipalCameras();
				}
			});
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.selecione_camera));
			builder.setView(v);
			
			adAdicionarCamera = builder.create();
			adAdicionarCamera.show();
			break;
		case 2:
			ativarTodasCameras();
			carregarListaPrincipalCameras();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, 3, 0, "Remover");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
		case 3:
			setCameraAtiva("camera"+(info.position+1), false);
			carregarListaPrincipalCameras();
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	getProgressDialog().setMessage(getString(R.string.carregando_imagem));
    	getProgressDialog().show();
    	Intent exibirCameraIntent = new Intent(RecifeCamActivity.this, ExibirCameraActivity.class);
    	exibirCameraIntent.putExtra("camera", listaCameras.get(position));
    	startActivity(exibirCameraIntent);
	} 
	
	private void ativarTodasCameras() {
		for (int i = 1; i <= 24; i++) {
			setCameraAtiva("camera"+i, true);
		}
	}
	
	public void onFailedToReceiveAd(AdView adView)
	{
		Log.d("RecifeCam", "onFailedToReceiveAd");
		adView.setVisibility(View.GONE);
	}

	public void onFailedToReceiveRefreshedAd(AdView adView)
	{
		Log.d("RecifeCam", "onFailedToReceiveRefreshedAd");
	}

	public void onReceiveAd(AdView adView)
	{
		Log.d("RecifeCam", "onReceiveAd");
		adView.setVisibility(View.VISIBLE);
	}

	public void onReceiveRefreshedAd(AdView adView)
	{
		Log.d("RecifeCam", "onReceiveRefreshedAd");
	}
	
	private ArrayList<Camera> carregarCameras(boolean todasCameras) {
		
		ArrayList<Camera> listaCamerasRetorno = new ArrayList<Camera>();
		
		if (isCameraAtiva("camera1") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_1).toString(),
					getResources().getText(R.string.nome_camera_1).toString(),
					getResources().getText(R.string.coordenadas_camera_1).toString()));
		}
		if (isCameraAtiva("camera2") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_2).toString(),
					getResources().getText(R.string.nome_camera_2).toString(),
					getResources().getText(R.string.coordenadas_camera_2).toString()));
		}
		if (isCameraAtiva("camera3") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_3).toString(),
					getResources().getText(R.string.nome_camera_3).toString(),
					getResources().getText(R.string.coordenadas_camera_3).toString()));
		}
		if (isCameraAtiva("camera4") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_4).toString(),
					getResources().getText(R.string.nome_camera_4).toString(),
					getResources().getText(R.string.coordenadas_camera_4).toString()));
		}
		if (isCameraAtiva("camera5") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_5).toString(),
					getResources().getText(R.string.nome_camera_5).toString(),
					getResources().getText(R.string.coordenadas_camera_5).toString()));
		}
		if (isCameraAtiva("camera6") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_6).toString(),
					getResources().getText(R.string.nome_camera_6).toString(),
					getResources().getText(R.string.coordenadas_camera_6).toString()));
		}
		if (isCameraAtiva("camera7") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_7).toString(),
					getResources().getText(R.string.nome_camera_7).toString(),
					getResources().getText(R.string.coordenadas_camera_7).toString()));
		}
		if (isCameraAtiva("camera8") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_8).toString(),
					getResources().getText(R.string.nome_camera_8).toString(),
					getResources().getText(R.string.coordenadas_camera_8).toString()));
		}
		if (isCameraAtiva("camera9") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_9).toString(),
					getResources().getText(R.string.nome_camera_9).toString(),
					getResources().getText(R.string.coordenadas_camera_9).toString()));
		}
		if (isCameraAtiva("camera10") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_10).toString(),
					getResources().getText(R.string.nome_camera_10).toString(),
					getResources().getText(R.string.coordenadas_camera_10).toString()));
		}
		if (isCameraAtiva("camera11") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_11).toString(),
					getResources().getText(R.string.nome_camera_11).toString(),
					getResources().getText(R.string.coordenadas_camera_11).toString()));
		}
		if (isCameraAtiva("camera12") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_12).toString(),
					getResources().getText(R.string.nome_camera_12).toString(),
					getResources().getText(R.string.coordenadas_camera_12).toString()));
		}
		if (isCameraAtiva("camera13") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_13).toString(),
					getResources().getText(R.string.nome_camera_13).toString(),
					getResources().getText(R.string.coordenadas_camera_13).toString()));
		}
		if (isCameraAtiva("camera14") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_14).toString(),
					getResources().getText(R.string.nome_camera_14).toString(),
					getResources().getText(R.string.coordenadas_camera_14).toString()));
		}
		if (isCameraAtiva("camera15") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_15).toString(),
					getResources().getText(R.string.nome_camera_15).toString(),
					getResources().getText(R.string.coordenadas_camera_15).toString()));
		}
		if (isCameraAtiva("camera16") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_16).toString(),
					getResources().getText(R.string.nome_camera_16).toString(),
					getResources().getText(R.string.coordenadas_camera_16).toString()));
		}
		if (isCameraAtiva("camera17") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_17).toString(),
					getResources().getText(R.string.nome_camera_17).toString(),
					getResources().getText(R.string.coordenadas_camera_17).toString()));
		}
		if (isCameraAtiva("camera18") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_18).toString(),
					getResources().getText(R.string.nome_camera_18).toString(),
					getResources().getText(R.string.coordenadas_camera_18).toString()));
		}
		if (isCameraAtiva("camera19") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_19).toString(),
					getResources().getText(R.string.nome_camera_19).toString(),
					getResources().getText(R.string.coordenadas_camera_19).toString()));
		}
		if (isCameraAtiva("camera20") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_20).toString(),
					getResources().getText(R.string.nome_camera_20).toString(),
					getResources().getText(R.string.coordenadas_camera_20).toString()));
		}
		if (isCameraAtiva("camera21") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_21).toString(),
					getResources().getText(R.string.nome_camera_21).toString(),
					getResources().getText(R.string.coordenadas_camera_21).toString()));
		}
		if (isCameraAtiva("camera22") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_22).toString(),
					getResources().getText(R.string.nome_camera_22).toString(),
					getResources().getText(R.string.coordenadas_camera_22).toString()));
		}
		if (isCameraAtiva("camera23") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_23).toString(),
					getResources().getText(R.string.nome_camera_23).toString(),
					getResources().getText(R.string.coordenadas_camera_23).toString()));
		}
		if (isCameraAtiva("camera24") || todasCameras) {
			listaCamerasRetorno.add(new Camera(getResources().getText(R.string.descricao_camera_24).toString(),
					getResources().getText(R.string.nome_camera_24).toString(),
					getResources().getText(R.string.coordenadas_camera_24).toString()));
		}
		
		return listaCamerasRetorno;
	}
	
	public ProgressDialog getProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(true);
			progressDialog.setIndeterminate(true);
		}
		return progressDialog;
	}
	
	public void setCameraAtiva(String camera, boolean ativa) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putBoolean(camera, ativa);
		editor.commit();
	}
	
	public boolean isCameraAtiva(String camera) {
		return getPreferences().getBoolean(camera, true);
	}
	
	public SharedPreferences getPreferences() {
		if (preferences == null) {
	        preferences = getApplicationContext().getSharedPreferences("camera1Ativa", Context.MODE_PRIVATE);
		}
		return preferences;
	}
}