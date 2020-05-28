package com.example.joinchat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.joinchat.R;
import com.example.joinchat.fragments.PermissionsDialogFragment;
import com.example.joinchat.openvidu.LocalParticipant;
import com.example.joinchat.openvidu.RemoteParticipant;
import com.example.joinchat.openvidu.Session;
import com.example.joinchat.utils.CustomHttpClient;
import com.example.joinchat.websocket.CustomWebSocket;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    public static final String SESSION_ID = "SessionA";
    public static final String PARTICIPANT_NAME = "participant99";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private static final int MY_PERMISSIONS_REQUEST = 102;
    private int flag;
    private final String TAG = "SessionActivity";
    @BindView(R.id.views_container)
    LinearLayout views_container;
    @BindView(R.id.start_finish_call)
    Button start_finish_call;
    @BindView(R.id.local_gl_surface_view)
    SurfaceViewRenderer localVideoView;
    @BindView(R.id.peer_container)
    FrameLayout peer_container;

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

    private String OPENVIDU_URL = "https://ec2-13-235-159-249.ap-south-1.compute.amazonaws.com";
    private String OPENVIDU_SECRET = "qwerty@321";
    private Session session;
    private CustomHttpClient httpClient;
    private LocalParticipant localParticipant;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        askForPermissions();
        ButterKnife.bind(this);
        flag = 0;
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

            httpClient = new CustomHttpClient(OPENVIDU_URL,
                    "Basic " + android.util.Base64.encodeToString(("OPENVIDUAPP:" + OPENVIDU_SECRET)
                            .getBytes(), android.util.Base64.DEFAULT).trim());

            getToken(getIntent().getStringExtra(SESSION_ID));
        } else {
            DialogFragment permissionsFragment = new PermissionsDialogFragment();
            permissionsFragment.show(getSupportFragmentManager(), "Permissions Fragment");
        }
    }

    private void getToken(String sessionId) {
        try {
            // Session Request
            RequestBody sessionBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    "{\"customSessionId\": \"" + sessionId + "\"}");
            this.httpClient.httpCall("/api/sessions", "POST", "application/json",
                    sessionBody, new Callback() {

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d(TAG, "responseString: " + response.body().string());

                    // Token Request
                    RequestBody tokenBody = RequestBody.create(MediaType.parse("application/json; " +
                            "charset=utf-8"), "{\"session\": \"" + sessionId + "\"}");

                    httpClient.httpCall("/api/tokens", "POST", "application/json",
                            tokenBody, new Callback() {

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {
                            String responseString = null;
                            try {
                                responseString = response.body().string();
                            } catch (IOException e) {
                                Log.e(TAG, "Error getting body", e);
                            }
                            Log.d(TAG, "responseString2: " + responseString);
                            JSONObject tokenJsonObject = null;
                            String token = null;
                            try {
                                tokenJsonObject = new JSONObject(responseString);
                                token = tokenJsonObject.getString("token");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getTokenSuccess(token, sessionId);
                        }

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e(TAG, "Error POST /api/tokens", e);
                            connectionError();
                        }
                    });
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e(TAG, "Error POST /api/sessions", e);
                    connectionError();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error getting token", e);
            e.printStackTrace();
            connectionError();
        }
    }

    private void getTokenSuccess(String token, String sessionId) {
        // Initialize our session
        session = new Session(sessionId, token, views_container, this);

        // Initialize our local participant and start local camera
        localParticipant = new LocalParticipant(getIntent().getStringExtra(PARTICIPANT_NAME), session,
                this.getApplicationContext(), localVideoView);
        localParticipant.startCamera(flag);
        runOnUiThread(() -> {
            // Update local participant view
//            main_participant.setText(participant_name.getText().toString());
//            main_participant.setPadding(20, 3, 20, 3);
        });

        // Initialize and connect the websocket to OpenVidu Server
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
        if(flag == 0) {
            flag = 1;
        }
        else {
            flag = 0;
        }
        localParticipant.stopCamera();
        localParticipant.startCamera(flag);
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
//        localVideoView.onFrameResolutionChanged(480, 640, 0);
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
        this.httpClient.dispose();
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

}