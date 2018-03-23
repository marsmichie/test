package com.prototyp2;

import android.app.Activity;


import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;


import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;



import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.scichart.charting.model.AxisCollection;
import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.model.dataSeries.UniformHeatmapDataSeries;
import com.scichart.charting.visuals.SciChartHeatmapColourMap;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.DateAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.ColorMap;
import com.scichart.charting.visuals.renderableSeries.FastUniformHeatmapRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IPaletteProvider;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.IRange;




import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.scichart.drawing.utility.ColorUtil.Chartreuse;
import static com.scichart.drawing.utility.ColorUtil.CornflowerBlue;
import static com.scichart.drawing.utility.ColorUtil.DarkBlue;
import static com.scichart.drawing.utility.ColorUtil.DarkGreen;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;



import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;


public class MainActivity extends AppCompatActivity implements sci.OnFragmentInteractionListener {
    private static final int TAG = 1;
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    int e_num = 0;
    private Spinner dropdown;
    byte[] readBuffer;
    int readBufferPosition;
    int int_connected = 0;
    volatile boolean stopWorker;
    final Handler handler = new Handler();
    volatile boolean run=false;
    volatile boolean first=true;
    volatile boolean first2=true;
    volatile boolean check=false;
    String[] msg_client = {"no connection", "receiving data"};


    private void startServerSocket() {
        final ExecutorService pool = Executors.newSingleThreadExecutor();

        Runnable r = new Runnable() {

            private String stringData = null;

            @Override
            public void run() {
                // Get UsbManager from Android.
                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

// Find the first available driver.
                UsbSerialDriver driver = UsbSerialProber.acquire(manager);

                if (driver != null) {
                    try {
                        driver.open();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        driver.setBaudRate(115200);

                        byte buffer[] = new byte[16];
                        int numBytesRead = driver.read(buffer, 1000);
                        Toast.makeText(MainActivity.this, "Read " + numBytesRead + " bytes.", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // Deal with error.
                    } finally {
                        try {
                            driver.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                    byte[] msg = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(msg, msg.length);
                    DatagramSocket ds = null;
                    try {
                        ds = new DatagramSocket(9001);
                        //ds.setSoTimeout(50000);

                        ds.receive(dp);

                        stringData = new String(msg, 0, dp.getLength());
                        check=true;

                        String[] parts = stringData.trim().split("#");

                        float[][] data = new float[8][3];

                        for (int sensori = 1; sensori < 8; sensori++) {
                            data[sensori][0] = Float.parseFloat(parts[(sensori - 1) * 4 + 2]);
                            data[sensori][1] = Float.parseFloat(parts[(sensori - 1) * 4 + 3]);
                            data[sensori][2] = Float.parseFloat(parts[(sensori - 1) * 4 + 4]);
                            // Log.d("Changewerte","x="+Integer.toString(sensori)+" data="+Double.toString(Math.sqrt(data[sensori][0]*data[sensori][0]
                            //+data[sensori][1]*data[sensori][1]+data[sensori][2]*data[sensori][2])));

                        }

                        //Log.d("Data:","1:  "+Double.toString(Math.sqrt(data[1][0]*data[1][0]+data[1][1]*data[1][1]+data[1][2]*data[1][2])));
                        if(run) {

                        sci fragment = (sci) getSupportFragmentManager().findFragmentByTag("fragment");
                        fragment.changwerte(data, e_num);}

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (ds != null) {
                            ds.close();
                            pool.submit(this);
                        }
                    }

            }

        };
        pool.submit(r);
       // thread.start();
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment mFragment = new sci();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container,mFragment , "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        setWifiTetheringEnabled(true);
        //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST);
        // create directories
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("MyApp", "No SDCARD");
            Toast.makeText(MainActivity.this, "No external storage available (SAVE Button won't work)", Toast.LENGTH_LONG).show();
        } else {
            File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"data_prototyp");
            Toast.makeText(MainActivity.this, "External storage available: "+ Environment.getExternalStorageDirectory(), Toast.LENGTH_LONG).show();
            directory.mkdirs();
            File defDir = new File(directory+File.separator + "defect");
            defDir.mkdir();
            File refDir = new File(directory+File.separator + "reference");
            refDir.mkdir();
        }

        Button openButton = (Button) findViewById(R.id.open);
        Button sendButton = (Button) findViewById(R.id.send);
        Button uploadButton = (Button) findViewById(R.id.close);
        Button resetButton = (Button) findViewById(R.id.reset);

        /************* spinner stuff ************/
        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"x-component", "y-component", "z-component", "magnitude"};//create an adapter to describe how the items are displayed, adapters are used in several places in android.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                //Toast.makeText(MainActivity.this, parentView.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();

                sci fragment = (sci) getSupportFragmentManager().findFragmentByTag("fragment");
                if (parentView.getItemAtPosition(position).toString() == "x-component") {
                    fragment.view_werte=fragment.wertex;
                    e_num = 0;
                } else if (parentView.getItemAtPosition(position).toString() == "y-component") {
                    fragment.view_werte=fragment.wertey;
                    e_num = 1;
                } else if (parentView.getItemAtPosition(position).toString() == "z-component") {
                    fragment.view_werte=fragment.wertez;
                    e_num = 2;
                } else if (parentView.getItemAtPosition(position).toString() == "magnitude") {
                    fragment.view_werte=fragment.werte;
                    e_num = 3;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        /******************end spinner stuff ************/

        /******************textswitcher stuff ***********/
        TextSwitcher simpleT=(TextSwitcher)findViewById(R.id. TxtSw); // get reference of TextSwitcher

        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        simpleT.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // create a TextView
                TextView t = new TextView(MainActivity.this);
                // set the gravity of text to top and center horizontal
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                // set displayed text size
                t.setTextSize(16);
                return t;
            }
        });

        // load an animation by using AnimationUtils class
        /*Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        // set the animation type to TextSwitcher
        simpleT.setInAnimation(in);
        simpleT.setOutAnimation(out);
*/
        simpleT.setCurrentText("no connection"); // set current text in TextView that is currently showing
        /*********end textswitcher stuff */


        myLabel = (TextView) findViewById(R.id.label);
        myTextbox = (EditText) findViewById(R.id.entry);

        //Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(first) {
                 //   WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                 //   String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                 //   myTextbox = (EditText) findViewById(R.id.entry);
                 //   myTextbox.setTextColor(Color.BLACK);
                 //   myTextbox.setText(ip);
                 //   startServerSocket();
                    first=!first;
                }
                run=!run;

            }
        });

        // Reset Button
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sci fragment = (sci) getSupportFragmentManager().findFragmentByTag("fragment");
                fragment.resetDataSeries();

            }
        });

        //Share Button
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sci fragment = (sci) getSupportFragmentManager().findFragmentByTag("fragment");
                fragment.ausgabe();
            }
        });

        //Upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sci fragment = (sci) getSupportFragmentManager().findFragmentByTag("fragment");
                Switch switch1 = (Switch) findViewById(R.id.switch1);

                if (switch1.isChecked())
                {fragment.savedefecttxt();}
                else
                {fragment.saveworkingtxt();}
            }
        });


    }

    Handler h1 = new Handler();
    int delay = 500; //0.5 seconds
    Runnable runnable1;
    @Override
    protected void onStart() {
        //start handler as activity become visible

        h1.postDelayed(new Runnable() {
            public void run() {
                //do something
                TextSwitcher simpleT=(TextSwitcher)findViewById(R.id. TxtSw);
                simpleT.setText(msg_client[getClientList()]); // set current text in TextView that is currently showing
                //ping("192.168.43.116");

                runnable1=this;

                h1.postDelayed(runnable1, delay);
            }
        }, delay);

        super.onStart();
    }

    @Override
    protected void onPause() {
        h1.removeCallbacks(runnable1); //stop handler when activity not visible
        super.onPause();
    }

    public int getClientList() {
        int macCount = 0;
        int counter = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null ) {
                    // Basic sanity check
                    String mac = splitted[3];
                    String ip_prot = splitted[0];

                    if (counter > 0) {

                        if (mac.matches("..:..:..:..:..:..")) {
                            if (Objects.equals(ip_prot,"192.168.43.116")) {
                                System.out.println("Mac : Outside If " + mac);
                                if ((first2==false)&&(check==false)&&(first==false)) {
                                    setWifiTetheringEnabled(false);
                                    setWifiTetheringEnabled(true);
                                    Toast.makeText(MainActivity.this, "Restart/Reset Device", Toast.LENGTH_LONG).show();
                                    first2=true;
                                }
                                else if (first2)  {
                                    WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                                    startServerSocket();
                                    macCount = 1;
                                    first2=false;
                                    Thread.sleep(5000);
                                }
                                else if (check) {
                                    macCount = 1;
                                    check=false;
                                }
                            }
                    /* ClientList.add("Client(" + macCount + ")");
                    IpAddr.add(splitted[0]);
                    HWAddr.add(splitted[3]);
                    Device.add(splitted[5]);*/
                            //System.out.println("Mac : "+ mac + " IP Address : "+splitted[0] );
                            //System.out.println("Mac_Count  " + macCount + " MAC_ADDRESS  "+ mac);
                        }
                        for (int i = 0; i < splitted.length; i++)
                            System.out.println("Address     " + splitted[i]);
                    }
                }
                counter++;
            }
        } catch(Exception e) {

        }
        return macCount;
    }

    private void setWifiTetheringEnabled(boolean enable) {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(wifiManager, null, enable);
                } catch (Exception ex) {
                }
                break;
            }
        }
    }
    /****************** USB Serial connection **********/




    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            myLabel.setText(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }


    public String ping(String url) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -c 8 " + url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();

            // body.append(output.toString()+"\n");
            str = output.toString();
            // Log.d(TAG, str);
            System.out.println(str);
        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
        }
        return str;
    }

}