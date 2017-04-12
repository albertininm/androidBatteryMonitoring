package ic.test.com.teste;

import android.app.ApplicationErrorReport;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DecimalFormat;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button b;
    TextView t;
    Long aux;
    Thread thread;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = (Button)findViewById(R.id.button);
        t = (TextView) findViewById(R.id.textView);

        try{
            thread = new Thread(){
                public void run(){
                    while(true) {
                        //Toast.makeText(getApplicationContext(),"Valor mudou", Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Context application = getApplicationContext();
                                    BatteryManager batteryManager = (BatteryManager) application.getSystemService(BATTERY_SERVICE);
                                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                                    Intent batteryStatus = application.registerReceiver(null, ifilter);
                                    Long v = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);

                                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                                    int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
                                    int capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                                    int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                                    
                                    t.setText("Level: "+level+"%"+
                                            "\nCurrent (Long): " + v + "µAh"+
                                            "\nVoltage: "+voltage+" voltage"+
                                            "\nCapacity: "+capacity+
                                            "\nTemperature: "+(double)temperature/10+"º"+
                                            "\nActual Capacity: "+getActualBatteryCapacity(application));
                                }
                            });

                        try {
                            sleep(300);
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                //porcentagem atual
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                */

                //values in microampere
                BatteryManager batteryManager = (BatteryManager) getApplicationContext().getSystemService(BATTERY_SERVICE);
                int currInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                Long currLong = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                Long average = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);

                aux = currLong;

                Long energyCounter = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                Long  chargeCounter = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                /*
                //Another example
                BatteryManager mBatteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                Long avgCurrent = null, currentNow = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    avgCurrent = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                    currentNow = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                }
                t.setText("Level: "+level+"%" +
                            "\nCurrent (Int): " +currInt+"µAh" +
                            "\nCurrent (Long): " +currLong+"µAh" +
                            "\nCurrent average: "+average+"\n" +
                            "\nEnergy Counter: " +energyCounter+
                            "\nCharge Counter: " +chargeCounter);
                */
                //t.setText("Current (Long): " +currLong+"µAh");
                if(!thread.isAlive()){
                    thread.start();
                }
            }
        });

    }



    public double getBatteryCapacity() {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
            return batteryCapacity;
            //Toast.makeText(MainActivity.this, batteryCapacity + " mah", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getActualBatteryCapacity(final Context context) {
        Object mPowerProfile_ = null;
        int batteryCapacity = 0;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            batteryCapacity = (Integer) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
            //LOGI(TAG, batteryCapacity + " mah");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;
    }

}
