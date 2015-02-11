package com.googry.hellomyo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;


public class HelloMYOActivity extends ActionBarActivity {
    private TextView tv_name, tv_macAddress, tv_sync, tv_arm, tv_xDirection, tv_lock, tv_pose;

    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            super.onConnect(myo, timestamp);
            tv_name.setText(getString(R.string.connect));
            tv_macAddress.setText(myo.getMacAddress());
            setTextOutOfSync();
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            super.onDisconnect(myo, timestamp);
            setTextDisconnect();
        }

        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            super.onArmSync(myo, timestamp, arm, xDirection);
            tv_sync.setText(getString(R.string.sync));
            tv_arm.setText(getString(myo.getArm() == Arm.LEFT ? R.string.leftArm : R.string.rightArm));
            tv_xDirection.setText(getString((myo.getXDirection() == XDirection.TOWARD_WRIST ? R.string.usb_towards_elbow : R.string.usb_towards_wrist)));
            setTextLock();
        }

        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            super.onArmUnsync(myo, timestamp);
            setTextOutOfSync();
        }

        @Override
        public void onUnlock(Myo myo, long timestamp) {
            super.onUnlock(myo, timestamp);
            tv_lock.setText(getString(R.string.unlock));
            tv_pose.setText(getString(R.string.pose_rest));
        }

        @Override
        public void onLock(Myo myo, long timestamp) {
            super.onLock(myo, timestamp);
            setTextLock();
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            super.onPose(myo, timestamp, pose);
            String strPose = null;
            switch (myo.getPose()) {
                case REST:
                    strPose = getString(R.string.pose_rest);
                    break;
                case UNKNOWN:
                    strPose = getString(R.string.pose_unknown);
                    break;
                case DOUBLE_TAP:
                    strPose = getString(R.string.pose_double_tap);
                    break;
                case FIST:
                    strPose = getString(R.string.pose_fist);
                    break;
                case FINGERS_SPREAD:
                    strPose = getString(R.string.pose_fingers_spread);
                    break;
                case WAVE_OUT:
                    strPose = getString(R.string.pose_wave_out);
                    break;
                case WAVE_IN:
                    strPose = getString(R.string.pose_wave_in);
                    break;
            }
            //
            tv_pose.setText(strPose);
            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                myo.unlock(Myo.UnlockType.HOLD);
                myo.notifyUserAction();
            } else {
                myo.unlock(Myo.UnlockType.TIMED);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_myo);
        initId();
        setTextDisconnect();

        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            finish();
            return;
        }
        hub.addListener(mListener);
        hub.attachToAdjacentMyo();

    }

    private void initId() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_macAddress = (TextView) findViewById(R.id.tv_macAddress);
        tv_sync = (TextView) findViewById(R.id.tv_sync);
        tv_arm = (TextView) findViewById(R.id.tv_arm);
        tv_xDirection = (TextView) findViewById(R.id.tv_xDirection);
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_pose = (TextView) findViewById(R.id.tv_pose);
    }

    private void setTextDisconnect() {
        tv_name.setText(getString(R.string.disconnect));
        tv_macAddress.setText(getString(R.string.disconnect));
        tv_sync.setText(getString(R.string.disconnect));
        tv_arm.setText(getString(R.string.disconnect));
        tv_xDirection.setText(getString(R.string.disconnect));
        tv_lock.setText(getString(R.string.disconnect));
        tv_pose.setText(getString(R.string.disconnect));
    }

    private void setTextOutOfSync() {
        tv_sync.setText(getString(R.string.out_of_sync));
        tv_arm.setText(getString(R.string.out_of_sync));
        tv_xDirection.setText(getString(R.string.out_of_sync));
        tv_lock.setText(getString(R.string.out_of_sync));
        tv_pose.setText(getString(R.string.out_of_sync));
    }

    private void setTextLock() {
        tv_lock.setText(getString(R.string.lock));
        tv_pose.setText(getString(R.string.lock));
    }

    @Override
    protected void onDestroy() {
        Hub.getInstance().removeListener(mListener);
        if (isFinishing()) {
            Hub.getInstance().shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hello_myo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
