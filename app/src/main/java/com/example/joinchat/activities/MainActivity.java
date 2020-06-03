package com.example.joinchat.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joinchat.Models.SessionResponse;
import com.example.joinchat.Models.TokenBody;
import com.example.joinchat.Models.TokenResponse;
import com.example.joinchat.R;
import com.example.joinchat.fragments.PermissionsDialogFragment;
import com.example.joinchat.openvidu.LocalParticipant;
import com.example.joinchat.openvidu.RemoteParticipant;
import com.example.joinchat.openvidu.Session;
import com.example.joinchat.utils.CustomHttpClient;
import com.example.joinchat.utils.JsonApiHolder;
import com.example.joinchat.utils.RetrofitInstance;
import com.example.joinchat.utils.prefUtils;
import com.example.joinchat.websocket.CustomWebSocket;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String SESSION_ID;
    public String PARTICIPANT_NAME="name";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private static final int MY_PERMISSIONS_REQUEST = 102;
    private static final int PERMISSION_CODE = 1;
    private static final int REQUEST_MEDIA_PROJECTION = 2;
    public static String FLAG = "0";
    private final String TAG = "SessionActivity";
    @BindView(R.id.views_container)
    LinearLayout views_container;
    @BindView(R.id.start_finish_call)
    Button start_finish_call;
    @BindView(R.id.local_gl_surface_view)
    SurfaceViewRenderer localVideoView;
    @BindView(R.id.peer_container)
    FrameLayout peer_container;
    @BindView(R.id.log_out_button)
    Button log_out_button;

    @BindView(R.id.video_off)
    ImageButton buttonVideoOff;
    @BindView(R.id.video_on)
    ImageButton buttonVideoOn;
    @BindView(R.id.audio_on)
    ImageButton buttonAudioOn;
    @BindView(R.id.audio_off)
    ImageButton buttonAudioOff;
    @BindView(R.id.imageButtonCameraSwitch)
    ImageButton buttonCameraSwitch;
    @BindView(R.id.screenShareButton)
    ImageButton buttonScreenShare;
    @BindView(R.id.textViewRoomName)
    TextView textViewRoomName;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String OPENVIDU_URL = "https://ec2-13-235-159-249.ap-south-1.compute.amazonaws.com";
    private String OPENVIDU_SECRET = "qwerty@321";
    private Session session;
    private CustomHttpClient httpClient;
    private LocalParticipant localParticipant;
    private prefUtils pr;
    private JsonApiHolder jsonApiHolder;

    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";
    private int mScreenDensity;
    private int mResultCode;
    private Intent mResultData;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        jsonApiHolder = RetrofitInstance.getRetrofitInstance(this).create(JsonApiHolder.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ///////////////////////////////////////
        if (savedInstanceState != null) {
            mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE);
            mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA);
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        /////////////////////////////////

        askForPermissions();
        pr = new prefUtils(this);
        ButterKnife.bind(this);
        PARTICIPANT_NAME= pr.getUserName();
        if(getIntent().getStringExtra(FLAG).equals("0")) {
            getToken(pr.getVideoSession());
        }
        else {
            getSessionId();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode);
            outState.putParcelable(STATE_RESULT_DATA, mResultData);
        }
    }

    public void askForPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST);
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    public void buttonPressed(View view) {
        if (start_finish_call.getText().equals(getResources().getString(R.string.hang_up))) {
            // Already connected to a session
            leaveSession();
            return;
        }
        if (arePermissionGranted()) {
            initViews();
            viewToConnectingState();

            getTokenSuccess(pr.getVideoToken(), pr.getVideoSession());
        }
        else {
            DialogFragment permissionsFragment = new PermissionsDialogFragment();
            permissionsFragment.show(getSupportFragmentManager(), "Permissions Fragment");
        }
    }

    private void getSessionId() {

        retrofit2.Call<SessionResponse> call = jsonApiHolder.getSession("Bearer " + prefUtils.getAuthToken());
        call.enqueue(new retrofit2.Callback<SessionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SessionResponse> call, retrofit2.Response<SessionResponse> response) {
                if(response.isSuccessful()){
                    SessionResponse sessionResponse = response.body();
                    SESSION_ID = sessionResponse.getSessionId();
                    Log.d("SESSION ID: ", String.valueOf(SESSION_ID));
                    getToken(SESSION_ID);
                }
                else{
                    Toast.makeText(MainActivity.this, "Session ID not fetched!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SessionResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getToken(String sessionId){

        Call<TokenResponse> call = jsonApiHolder.getVideoToken("Bearer " + prefUtils.getAuthToken(), new TokenBody(sessionId));
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if(response.isSuccessful()){
                    TokenResponse tokenResponse = response.body();
                    String token = tokenResponse.getToken();
                    pr.setVideoSession(sessionId);
                    pr.setVideoToken(token);
                    textViewRoomName.setText("Room Name : " + pr.getVideoSession());
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                else{
                    Toast.makeText(MainActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getTokenSuccess(String token, String sessionId) {
        // Initialize our session
        session = new Session(sessionId, token, views_container, this);

        localParticipant = new LocalParticipant(PARTICIPANT_NAME, session,
                this.getApplicationContext(), localVideoView);
        localParticipant.startCamera();
        runOnUiThread(() -> {
        });

         startWebSocket();
    }

    public void setVideoOff(View view) {
            localParticipant.setVideoOff();
            localVideoView.clearImage();
            buttonVideoOff.setVisibility(View.GONE);
            buttonVideoOn.setVisibility(View.VISIBLE);
    }

    public void setVideoOn(View view) {
        localParticipant.setVideoOn();
        buttonVideoOff.setVisibility(View.VISIBLE);
        buttonVideoOn.setVisibility(View.GONE);
    }

    public void setAudioOff(View view) {
        localParticipant.setAudioOff();
        buttonAudioOff.setVisibility(View.GONE);
        buttonAudioOn.setVisibility(View.VISIBLE);
    }

    public void setAudioOn(View view) {
        localParticipant.setAudioOn();
        buttonAudioOff.setVisibility(View.VISIBLE);
        buttonAudioOn.setVisibility(View.GONE);
    }

    public void cameraSwitch(View view) {
        localParticipant.switchCamera();
    }

    public void screenShare(View view) {
        if (mVirtualDisplay == null) {
            startScreenCapture();
        } else {
//            stopScreenCapture();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PERMISSION_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
        mResultCode = resultCode;
        mResultData = data;
        setUpMediaProjection();
        setUpVirtualDisplay();
    }

    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    private void startScreenCapture() {
        if (mMediaProjection != null) {
            setUpVirtualDisplay();
        } else if (mResultCode != 0 && mResultData != null) {
            setUpMediaProjection();
            setUpVirtualDisplay();
        } else {
            Log.i(TAG, "Requesting confirmation");
            // This initiates a prompt dialog for the user to confirm screen projection.
            startActivityForResult(
                    mMediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION);
        }
    }

    private void setUpVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                480, 640, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                localVideoView.getHolder().getSurface(), null, null);
    }

    private void startWebSocket() {
        CustomWebSocket webSocket = new CustomWebSocket(session, OPENVIDU_URL, this);
        webSocket.execute();
        session.setWebSocket(webSocket);
    }

    private void connectionError() {
        Runnable myRunnable = () -> {
            Toast toast = Toast.makeText(this, "Error connecting to " +
                    OPENVIDU_URL, Toast.LENGTH_LONG);
            toast.show();
            viewToDisconnectedState();
        };
        new Handler(this.getMainLooper()).post(myRunnable);
    }

    private void initViews() {
        EglBase rootEglBase = EglBase.create();
        localVideoView.init(rootEglBase.getEglBaseContext(), null);
        localVideoView.setMirror(true);
        localVideoView.setEnableHardwareScaler(true);
        localVideoView.setZOrderMediaOverlay(true);
    }

    public void viewToDisconnectedState() {
        runOnUiThread(() -> {
            localVideoView.clearImage();
            localVideoView.release();
            start_finish_call.setText(getResources().getString(R.string.start_button));
            start_finish_call.setEnabled(true);
        });
    }

    public void viewToConnectingState() {
        runOnUiThread(() -> {
            start_finish_call.setEnabled(false);
        });
    }

    public void viewToConnectedState() {
        runOnUiThread(() -> {
            start_finish_call.setText(getResources().getString(R.string.hang_up));
            start_finish_call.setEnabled(true);
        });
    }

    public void createRemoteParticipantVideo(final RemoteParticipant remoteParticipant) {
        Handler mainHandler = new Handler(this.getMainLooper());
        Runnable myRunnable = () -> {
            View rowView = this.getLayoutInflater().inflate(R.layout.peer_video, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 20);
            rowView.setLayoutParams(lp);
            int rowId = View.generateViewId();
            rowView.setId(rowId);
            views_container.addView(rowView);
            SurfaceViewRenderer videoView = (SurfaceViewRenderer) ((ViewGroup) rowView).getChildAt(0);
            remoteParticipant.setVideoView(videoView);
            videoView.setMirror(false);
            EglBase rootEglBase = EglBase.create();
            videoView.init(rootEglBase.getEglBaseContext(), null);
            videoView.setZOrderMediaOverlay(true);
            View textView = ((ViewGroup) rowView).getChildAt(1);
            remoteParticipant.setParticipantNameText((TextView) textView);
            remoteParticipant.setView(rowView);

            remoteParticipant.getParticipantNameText().setText(remoteParticipant.getParticipantName());
            remoteParticipant.getParticipantNameText().setPadding(20, 3, 20, 3);
        };
        mainHandler.post(myRunnable);
    }

    public void setRemoteMediaStream(MediaStream stream, final RemoteParticipant remoteParticipant) {
        final VideoTrack videoTrack = stream.videoTracks.get(0);
        videoTrack.addSink(remoteParticipant.getVideoView());
        runOnUiThread(() -> {
            remoteParticipant.getVideoView().setVisibility(View.VISIBLE);
        });
    }

    public void leaveSession() {
        this.session.leaveSession();
        viewToDisconnectedState();
    }

    private boolean arePermissionGranted() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_DENIED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_DENIED);
    }

    @Override
    protected void onDestroy() {
        leaveSession();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        leaveSession();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        leaveSession();
        super.onStop();
    }

    public void logout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {
                        pr.logoutUser();
                        finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}