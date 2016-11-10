package ch.festigeek.festiscan.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ch.festigeek.festiscan.R;
import ch.festigeek.festiscan.activities.UserActivity;
import ch.festigeek.festiscan.communications.RequestPOST;
import ch.festigeek.festiscan.interfaces.ICallback;
import ch.festigeek.festiscan.interfaces.IConstant;
import ch.festigeek.festiscan.interfaces.IKeys;
import ch.festigeek.festiscan.interfaces.IURL;
import ch.festigeek.festiscan.utils.Utilities;

public class ScannerFragment extends Fragment implements IConstant, IURL, IKeys {

    private SurfaceView mCameraView;
    private CameraSource mCameraSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scanner, container, false);
        mCameraView = (SurfaceView) v.findViewById(R.id.camera_view);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getActivity()).setBarcodeFormats(Barcode.QR_CODE).build();

        mCameraSource = new CameraSource.Builder(getActivity(), barcodeDetector).setRequestedPreviewSize(640, 480).build();

        mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        mCameraSource.start(mCameraView.getHolder());
                        cameraFocus(mCameraSource, Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    }
                } catch (IOException ex) {
                    Log.e(LOG_NAME, ex.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                /* Nothing to do here ! */
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                /* Nothing to do here ! */
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Utilities.initProgressDialog(getActivity(), getString(R.string.dialog_decoding_qrcode));
                            Map<String, String> map = new HashMap<>();
                            map.put(KEY_PAYLOAD, barcodes.valueAt(0).displayValue);
                            mCameraSource.stop();
                            new RequestPOST(new ICallback<String>() {
                                @Override
                                public void success(String result) {
                                    Utilities.dismissProgressDialog();
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        Intent intent = new Intent(getContext(), UserActivity.class);
                                        intent.putExtra(KEY_ID, obj.getInt(KEY_ID));
                                        getContext().startActivity(intent);
                                    } catch (JSONException ex) {
                                        Log.e(LOG_NAME, ex.getMessage());
                                    }
                                }

                                @Override
                                public void failure(Exception ex) {
                                    Utilities.dismissProgressDialog();
                                    Log.e(LOG_NAME, ex.getMessage());
                                }
                            }, getString(R.string.token), IURL.BASE_URL + IURL.QR_DECRYPT, map).execute();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser) {
                mCameraSource.stop();
            } else {
                Utilities.closeKeyboardWhenNecessary(getActivity(), mCameraView);
                try {
                    mCameraSource.start(mCameraView.getHolder());
                    cameraFocus(mCameraSource, Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } catch (IOException ex) {
                    Log.e(LOG_NAME, ex.getMessage());
                }
            }
        }
    }

    private boolean cameraFocus(@NonNull CameraSource cameraSource, @NonNull String focusMode) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(focusMode);
                        camera.setParameters(params);
                        return true;
                    }
                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return false;
    }
}
