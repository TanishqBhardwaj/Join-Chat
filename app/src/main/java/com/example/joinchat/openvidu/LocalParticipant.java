package com.example.joinchat.openvidu;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import org.webrtc.AudioSource;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;

import java.util.ArrayList;
import java.util.Collection;

public class LocalParticipant extends Participant {

    private Context context;
    private SurfaceViewRenderer localVideoView;
    private SurfaceTextureHelper surfaceTextureHelper;
    private VideoCapturer videoCapturer;

    private Collection<IceCandidate> localIceCandidates;
    private SessionDescription localSessionDescription;
    private PeerConnectionFactory peerConnectionFactory;

    public LocalParticipant(String participantName, Session session, Context context,
                            SurfaceViewRenderer localVideoView) {
        super(participantName, session);
        this.localVideoView = localVideoView;
        this.context = context;
        this.participantName = participantName;
        this.localIceCandidates = new ArrayList<>();
        session.setLocalParticipant(this);
    }

    public void startCamera(int flag) {

        final EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
        peerConnectionFactory = this.session.getPeerConnectionFactory();

        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext);
        // create VideoCapturer
        videoCapturer = createCameraCapturer(flag);
        VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
        videoCapturer.startCapture(480, 640, 30);

        // create VideoTrack
        this.videoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
        this.videoTrack.setEnabled(true);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);

        // create AudioSource
        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        this.audioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
        this.audioTrack.setEnabled(true);

        // display in localView
        this.videoTrack.addSink(localVideoView);
    }

    public void setVideoOff() {
        videoTrack.setEnabled(false);
    }

    public void setVideoOn() {
        videoTrack.setEnabled(true);
    }

    public void setAudioOff() {
        audioTrack.setEnabled(false);
    }

    public void setAudioOn() {
        audioTrack.setEnabled(true);
    }

    public void stopCamera() {
        dispose();
    }

    private VideoCapturer createCameraCapturer(int flag) {
        CameraEnumerator enumerator;
        enumerator = new Camera2Enumerator(this.context);
        final String[] deviceNames = enumerator.getDeviceNames();

        if(flag == 0) {

            for (String deviceName : deviceNames) {
                if (enumerator.isFrontFacing(deviceName)) {
                    videoCapturer = enumerator.createCapturer(deviceName, null);
                    if(videoCapturer!=null) {
                        return videoCapturer;
                    }
                }
            }

            for (String deviceName : deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    videoCapturer = enumerator.createCapturer(deviceName, null);
                    if (videoCapturer != null) {
                        return videoCapturer;
                    }
                }
            }
        }
        else {

            for (String deviceName : deviceNames) {
                if (enumerator.isBackFacing(deviceName)) {
                    videoCapturer = enumerator.createCapturer(deviceName, null);
                    if (videoCapturer != null) {
                        return videoCapturer;
                    }
                }
            }

            for (String deviceName : deviceNames) {
                if (!enumerator.isBackFacing(deviceName)) {
                    videoCapturer = enumerator.createCapturer(deviceName, null);
                    if (videoCapturer != null) {
                        return videoCapturer;
                    }
                }
            }
        }

        return null;
    }

    public void storeIceCandidate(IceCandidate iceCandidate) {
        localIceCandidates.add(iceCandidate);
    }

    public Collection<IceCandidate> getLocalIceCandidates() {
        return this.localIceCandidates;
    }

    public void storeLocalSessionDescription(SessionDescription sessionDescription) {
        localSessionDescription = sessionDescription;
    }

    public SessionDescription getLocalSessionDescription() {
        return this.localSessionDescription;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (videoTrack != null) {
            videoTrack.removeSink(localVideoView);
            videoCapturer.dispose();
            videoCapturer = null;
        }
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
            surfaceTextureHelper = null;
        }
    }
}
