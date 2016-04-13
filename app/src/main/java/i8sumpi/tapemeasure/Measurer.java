package i8sumpi.tapemeasure;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;


public class Measurer extends ActionBarActivity implements SensorEventListener{
    private static TextView setState;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGravity;

    private float[] accVals;
    private float[] gravVals;
    private float[] moved = new float[3];
    private static boolean running;

    private long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tape);

        // courtesy of tuts+: see tuts+ Region
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // gets sensor system context
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // sets accelerometer to default acceleration sensor
        senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY); // sets Gyro to default Gyro sensor
        //senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        RelativeLayout rLay = (RelativeLayout) findViewById(R.id.layoutText);
        rLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetRunState();
            }
        });
    }

    public void SetRunState() {
        setState = (TextView) findViewById(R.id.txtMovingState);

        if (!running) {
            setState.setText("Moving...");
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST); // creates listener for the sensor
            senSensorManager.registerListener(this, senGravity, SensorManager.SENSOR_DELAY_FASTEST);
            startTime = Calendar.getInstance().getTimeInMillis();
            running = true;

            moved[0] = 0;
            moved[1] = 0;
            moved[2] = 0;
        } else {
            senSensorManager.unregisterListener(this);
            // courtesy of Android4Beginners: http://android4beginners.com/2013/06/lesson-1-3-how-to-modify-textview-in-java-code-findviewbyid-settext-and-gettext-methods/
            //long curTime = Calendar.getInstance().getTimeInMillis() - startTime;
            //DecimalFormat df = new DecimalFormat("#.##");
            setState.setText("X-Moved: "+ moved[0] + ". \nY-Moved: " + moved[1] + ". \nZ-Moved: " + moved[2] + ". \n\nX-Acc: " + accVals[1] + ". \nY-Acc: " + accVals[1] + ". \nZ-Acc: " + accVals[2] + ".");
            running = false;
        }
    }

    //region courtesy of Google Tech Talks: https://www.youtube.com/watch?v=C7JQ7Rpwn2k and Thousand Thoughts: http://www.thousand-thoughts.com/2012/03/android-sensor-fusion-tutorial/
    @Override
    public void onSensorChanged(SensorEvent event){
        Sensor mySensor = event.sensor;

        if(mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            accVals = event.values;

            moved[0] += accVals[0];
            moved[1] += accVals[1];
            moved[2] += accVals[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    // stops listener when the application is paused
    protected void onPause(){
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    // restarts listener when application is resumed
    protected void onResume(){
        super.onResume();
        if(running){
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    //endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_measurer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
