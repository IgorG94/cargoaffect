package com.igorgoldstein.cargoaffect;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    Button buttonRecord, buttonStop, buttonPlayLastAudio, buttonStopPlaying;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String SavePathDevice = null;
    Random random;
    String RandomFileName = "ABCDE";
    public static final int RequestPermissionCode = 1;

    private static final String BASE_URL = "http://10.90.69.35:5000/";
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRecord = (Button) findViewById(R.id.record);
        buttonStop = (Button) findViewById(R.id.stop);
        buttonPlayLastAudio = (Button) findViewById(R.id.playlast);
        buttonStopPlaying = (Button) findViewById(R.id.stopplay);

        buttonStop.setEnabled(false);
        buttonPlayLastAudio.setEnabled(false);
        buttonStopPlaying.setEnabled(false);

        random = new Random();

        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {
                    SavePathDevice = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/" + CreateRandomFileName(5) + "AudioRecording.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonRecord.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Gravação iniciada",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastAudio.setEnabled(true);
                buttonRecord.setEnabled(true);
                buttonStopPlaying.setEnabled(false);

                Toast.makeText(MainActivity.this, "Gravação concluída",
                        Toast.LENGTH_LONG).show();

                uploadFile(SavePathDevice);
            }
        });

        buttonPlayLastAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException, SecurityException,
                    IllegalStateException {

                buttonStop.setEnabled(false);
                buttonRecord.setEnabled(false);
                buttonStopPlaying.setEnabled(true);

                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(SavePathDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

                Toast.makeText(MainActivity.this, "Reproduzindo",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonStopPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonStop.setEnabled(false);
                buttonRecord.setEnabled(true);
                buttonStopPlaying.setEnabled(false);
                buttonPlayLastAudio.setEnabled(true);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }

    public final void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(SavePathDevice);
    }

    public interface FileUploadService {
        @Multipart
        @POST("detect")
        Call<ResponseBody> upload(
                @Part("description") RequestBody description,
                @Part MultipartBody.Part file
        );
    }

    public class ServiceGenerator {


    }

    private void uploadFile(String savePathDevice) {
        // create upload service client
        FileUploadService service = createService(FileUploadService.class);

        File file = new File(savePathDevice);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
        // String descriptionString = "hello, this is description speaking";
        // RequestBody description =
        //         RequestBody.create(
        //                 okhttp3.MultipartBody.FORM, descriptionString);
        RequestBody fullName =
                RequestBody.create(MediaType.parse("multipart/form-data"), "AudioFile");

        // finally, execute the request
        Call<ResponseBody> call = service.upload(fullName, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("RETROFIT", response.code()+"");
                Log.v("RETROFIT", response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public String CreateRandomFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < string; i++) {
            stringBuilder.append(RandomFileName.charAt(random.nextInt(RandomFileName.length())));
            i++;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permissão cedida",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permissão negada",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

}