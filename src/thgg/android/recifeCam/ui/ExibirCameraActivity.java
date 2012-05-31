package thgg.android.recifeCam.ui;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import thgg.android.recifeCam.R;
import thgg.android.recifeCam.model.Camera;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdView;


public class ExibirCameraActivity extends Activity implements OnClickListener, AdListener, Runnable {

	public static final int CARREGOU_IMAGEM = 1;
	public static final int NAO_CARREGOU_IMAGEM = -1;
	
	Intent parentIntent;
	
	TextView txtTitulo; 
	
	AdView adView;
	
	ImageView ivCamera;
	
	ProgressBar pbCamera;
	
//	String descricaoCamera;
//	String nomeCamera;
//	String coordenadasCamera;
	
	Camera camera;
	
	MeuHandlerCamera handler;
	
	ArrayList<Drawable> listaCameraShots;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exibir_camera_activity);
		
		txtTitulo = (TextView)findViewById(R.id.txtTitulo);
		ivCamera = (ImageView)findViewById(R.id.ivCamera);
		pbCamera = (ProgressBar)findViewById(R.id.pbCamera);
		parentIntent = getIntent();
		camera = (Camera)parentIntent.getSerializableExtra("camera");
//		descricaoCamera = parentIntent.getStringExtra("descricao_camera");
//		nomeCamera = parentIntent.getStringExtra("nome_camera");
//		coordenadasCamera = parentIntent.getStringExtra("coordenadas_camera");
		handler = new MeuHandlerCamera();
		listaCameraShots = new ArrayList<Drawable>();

		txtTitulo.setText(camera.getDescricao());
		ivCamera.setOnClickListener(this);
    
        adView = (AdView)findViewById(R.id.adMob2);
        adView.setAdListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
        if (adView != null)
        {
        	adView.requestFreshAd();
        } 
        handler.post(this);
	}
	
	@Override
	public void run() {
		
		//ivCamera.setVisibility(View.GONE);
		pbCamera.setVisibility(View.VISIBLE);
		
		Message msg = new Message();
		
		Drawable cameraShot1 = LoadImageFromWebOperations(getResources()
				.getString(R.string.url_cameras)
				+ camera.getNome().replace("#", "1"));
		if (cameraShot1 != null) {
			msg.what = CARREGOU_IMAGEM;
			msg.obj = cameraShot1;
		} else {
			msg.what = NAO_CARREGOU_IMAGEM;
		}
		handler.sendMessage(msg);
		handler.removeCallbacks(ExibirCameraActivity.this);
		handler.postDelayed(ExibirCameraActivity.this, 30000);		
	}
	
	private Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, camera.getDescricao());
			return d;
		} catch (Exception e) {
			System.out.println("Exc=" + e);
			return null;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(this);
	}

	@Override
	public void onFailedToReceiveAd(AdView adView)
	{
		Log.d("RecifeCam", "onFailedToReceiveAd");
		adView.setVisibility(View.GONE);
		adView.requestFreshAd();
	}

	@Override
	public void onFailedToReceiveRefreshedAd(AdView adView)
	{
		Log.d("RecifeCam", "onFailedToReceiveRefreshedAd, requesting again");
		adView.setVisibility(View.GONE);
		adView.requestFreshAd();
	}

	@Override
	public void onReceiveAd(AdView adView)
	{
		Log.d("RecifeCam", "onReceiveAd");
		adView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onReceiveRefreshedAd(AdView adView)
	{
		Log.d("RecifeCam", "onReceiveRefreshedAd");
		adView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View arg0) {
		Intent intent;
		if (camera.getCoordenadas().equals("0,0")) {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + camera.getDescricao()));
		} else {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + camera.getCoordenadas() + "?z=18"));
		}
		startActivity(intent);
	}
	
	class MeuHandlerCamera extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == CARREGOU_IMAGEM) {
				Drawable imagem = (Drawable)msg.obj;
				imagem.setBounds(0, 0, 200, 160);
				ivCamera.setImageDrawable(imagem);
				pbCamera.setVisibility(View.GONE);
				//ivCamera.setVisibility(View.VISIBLE);
			} else if (msg.what == NAO_CARREGOU_IMAGEM){
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ExibirCameraActivity.this);
				builder.setMessage(
						getResources().getString(
								R.string.nao_foi_possivel_contactar_servidor))
						.setCancelable(false).setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										ExibirCameraActivity.this.finish();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}
}
