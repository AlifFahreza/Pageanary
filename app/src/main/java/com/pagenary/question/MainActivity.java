package com.pagenary.question;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Chronometer chronometer;
    private ImageView imageViewRecord, imageViewPlay, imageViewStop;
    private SeekBar seekBar;
    private LinearLayout linearLayoutRecorder, linearLayoutPlay;
    private MediaRecorder mRecorder;
    private TextView quizQuestion;
    private MediaPlayer mPlayer;
    private String fileName = null, soal;
    private int b, i = 1;
    private int lastProgress = 0;
    private Handler mHandler = new Handler();
    private int RECORD_AUDIO_REQUEST_CODE = 123, coba;
    private boolean isPlaying = false;
    private TextView kategorii, soalkini;
    private String mPostKeyNama = null;
    private int soalnext;
    private String mPostKeyID = null;
    private String idKtegori, ID_SOAL;
    private String date;
    private int quizCount;
    private String JSON_STRING, pertanyaan;
    private int currentQuizQuestion;
    private List<QuizWrapper> parsedObject;
    private QuizWrapper firstQuestion;
    private Button nextButton, previousButton, jawaban, btnBack;
    private String namaFolder, namaSoal, jumlah, kini;
    private TextView totalSoal;
    private int urutanSekarang = 0;
    private boolean recorded_once = false;
    private Intent intentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordAudio();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            urutanSekarang = bundle.getInt("urutan_skrg");
        }

        Log.d("Getter urutanSkrg :",  urutanSekarang + "");

        initViews();

        intentList = new Intent(MainActivity.this, RecordingListActivity.class);
        Log.d("onCreate Folder : ", namaFolder + " - Soal : " + namaSoal + " - Urutan : " + urutanSekarang);
    }

    public void setDate() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMMyyyyhhmmss");
        date = formatter.format(today);
    }

    private void initViews() {

        quizQuestion = (TextView) findViewById(R.id.quiz_question);
        soalkini = (TextView) findViewById(R.id.soalkini);
        nextButton = (Button) findViewById(R.id.nextquiz);
        previousButton = (Button) findViewById(R.id.previousquiz);
        btnBack = findViewById(R.id.btn_back);

        linearLayoutRecorder = (LinearLayout) findViewById(R.id.linearLayoutRecorder);
        totalSoal = (TextView) findViewById(R.id.jumlahsoal);
        chronometer = (Chronometer) findViewById(R.id.chronometerTimer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        imageViewRecord = (ImageView) findViewById(R.id.imageViewRecord);
        imageViewStop = (ImageView) findViewById(R.id.imageViewStop);
        imageViewPlay = (ImageView) findViewById(R.id.imageViewPlay);
        linearLayoutPlay = (LinearLayout) findViewById(R.id.linearLayoutPlay);
        kategorii = (TextView) findViewById(R.id.kategorii);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        mPostKeyNama = getIntent().getExtras().getString("nama");
        mPostKeyID = getIntent().getExtras().getString("id_kategory");

        kategorii.setText(mPostKeyNama);
        jawaban = findViewById(R.id.jawaban);

        imageViewRecord.setOnClickListener(this);
        imageViewStop.setOnClickListener(this);
        imageViewPlay.setOnClickListener(this);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(urutanSekarang < parsedObject.size() - 1){
                    urutanSekarang++;
                    Log.d("Getter btnNext :", urutanSekarang + "");
                    Intent intent = getIntent();
                    intent.putExtra("urutan_skrg", urutanSekarang);
                    finish();
                    startActivity(intent);
//                    setPertanyaan(urutanSekarang);
                }

            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(urutanSekarang > 0){
                    urutanSekarang--;
                    Log.d("Getter btnNext :", urutanSekarang + "");
                    Intent intent = getIntent();
                    intent.putExtra("urutan_skrg", urutanSekarang);
                    finish();
                    startActivity(intent);
//                    setPertanyaan(urutanSekarang);
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.leftanimation,
                        R.anim.rightanimation);
            }
        });

        nextButton.setClickable(false);
        previousButton.setClickable(false);

        getJSON();

        setDate();
        loadJumlah();
    }

    private void loadJumlah() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showData();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(konfigurasi.URL_GET_JUMLAH);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void showData() {
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(konfigurasi.TAG_JSON_JUMLAH);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                jumlah = jo.getString(konfigurasi.TAG_JSON_SOAL);

                HashMap<String, String> data = new HashMap<>();
                data.put(konfigurasi.TAG_JSON_SOAL, jumlah);

                list.add(data);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;

                parsedObject = showData(s);
                if (parsedObject == null) {
                    return;
                }
                totalSoal.setText("" + parsedObject.size());
                setPertanyaan(urutanSekarang);

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(konfigurasi.URL_GET_SOAL);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private List<QuizWrapper> showData(String s) {

        List<QuizWrapper> jsonObject = new ArrayList<QuizWrapper>();
        JSONObject resultObject = null;
        JSONArray jsonArray = null;
        QuizWrapper newItemObject = null;

        try {
            resultObject = new JSONObject(s);
            jsonArray = resultObject.optJSONArray(konfigurasi.TAG_JSON_ARRAY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int urutan = 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonChildNode = null;
            try {
                jsonChildNode = jsonArray.getJSONObject(i);
                pertanyaan = jsonChildNode.getString(konfigurasi.TAG_SOAL);
                idKtegori = jsonChildNode.getString(konfigurasi.TAG_ID);
                ID_SOAL = jsonChildNode.getString(konfigurasi.TAG_ID_SOAL);
                if(idKtegori.equals(mPostKeyID)){
                    newItemObject = new QuizWrapper(pertanyaan, idKtegori, ID_SOAL, urutan);
                    urutan++;
                    jsonObject.add(newItemObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private void setPertanyaan(int urutan) {
        Log.d(TAG, "setPertanyaan: " + parsedObject);
        soalkini.setText(parsedObject.get(urutan).getUrutan() + "");
        quizQuestion.setText(parsedObject.get(urutan).getSoal());
        soalnext = Integer.parseInt(parsedObject.get(urutan).getId());
        namaSoal = quizQuestion.getText().toString();
        namaFolder = kategorii.getText().toString();

        jawaban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentList);
                overridePendingTransition(R.anim.bottomanimation, R.anim.topanimation);
            }
        });

        intentList.putExtra("folder", namaFolder);
        intentList.putExtra("soal", namaSoal);
        nextButton.setClickable(true);
        previousButton.setClickable(true);
        Log.d("setPertanyaan Folder : ", namaFolder + " - Soal : " + namaSoal + " - Urutan : " + urutanSekarang);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_list:
                gotoRecodingListActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void gotoRecodingListActivity() {
        Intent intent = new Intent(this, RecordingListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        if( view == imageViewRecord ){
//            prepareforRecording();
//            startRecording();
            if(recorded_once) {
                recorded_once = false;
                restartActivity();
            } else {
                prepareforRecording();
                startRecording();
            }

        }else if( view == imageViewStop ){
            prepareforStop();
            stopRecording();
            recorded_once = true;
        }else if( view == imageViewPlay ){
            if( !isPlaying && fileName != null ){
                isPlaying = true;
                startPlaying();
            }else{
                isPlaying = false;
                stopPlaying();
            }
        }

    }

    private void prepareforStop() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder);
        imageViewRecord.setVisibility(View.VISIBLE);
        imageViewRecord.setImageResource(R.drawable.ic_replay);
        imageViewStop.setVisibility(View.GONE);
        linearLayoutPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        finish();
        this.overridePendingTransition(R.anim.leftanimation,
                R.anim.rightanimation);
    }

    private void prepareforRecording() {
        TransitionManager.beginDelayedTransition(linearLayoutRecorder);
        imageViewRecord.setVisibility(View.GONE);
        imageViewStop.setVisibility(View.VISIBLE);
        linearLayoutPlay.setVisibility(View.GONE);
    }

    private void stopPlaying() {
        try {
            mPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer = null;
        //showing the play button
        imageViewPlay.setImageResource(R.drawable.ic_play);
        chronometer.stop();

    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios/" + namaFolder + "/" + namaSoal);
        if (!file.exists()) {
            file.mkdirs();
        }

        fileName = root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios/" + namaFolder + "/" + namaSoal + "/" + String.valueOf(date + ".mp3");
        Log.d("filename", fileName);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastProgress = 0;
        seekBar.setProgress(0);
        stopPlaying();
        // making the imageview a stop button
        //starting the chronometer
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRecorder = null;
        //starting the chronometer
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        //showing the play button
        Toast.makeText(this, "Recording saved successfully.", Toast.LENGTH_SHORT).show();
    }


    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
        //making the imageview pause button
        imageViewPlay.setImageResource(R.drawable.ic_pause);

        seekBar.setProgress(lastProgress);
        mPlayer.seekTo(lastProgress);
        seekBar.setMax(mPlayer.getDuration());
        seekUpdation();
        chronometer.start();


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imageViewPlay.setImageResource(R.drawable.ic_play);
                isPlaying = false;
                chronometer.stop();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( mPlayer!=null && fromUser ){
                    mPlayer.seekTo(progress);
                    chronometer.setBase(SystemClock.elapsedRealtime() - mPlayer.getCurrentPosition());
                    lastProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation() {
        if (mPlayer != null) {
            int mCurrentPosition = mPlayer.getCurrentPosition();
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        mHandler.postDelayed(runnable, 100);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_REQUEST_CODE);

        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }

    }
}