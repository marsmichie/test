package com.prototyp2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scichart.charting.model.dataSeries.IDataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.UniformHeatmapDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.ModifierGroup;
import com.scichart.charting.modifiers.PinchZoomModifier;
import com.scichart.charting.modifiers.ZoomExtentsModifier;
import com.scichart.charting.visuals.SciChartHeatmapColourMap;
import com.scichart.charting.visuals.SciChartSurface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;


import java.io.IOException;

import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint;
import com.scichart.charting.visuals.annotations.TextAnnotation;
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.DateAxis;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.ColorMap;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastUniformHeatmapRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IPaletteProvider;
import com.scichart.core.annotations.Orientation;
import com.scichart.data.model.DoubleRange;
import com.scichart.data.model.IRange;
import com.scichart.drawing.common.FontStyle;
import com.scichart.drawing.common.PenStyle;
import com.scichart.drawing.utility.ColorUtil;


import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.Channel;
import net.schmizz.sshj.connection.channel.ChannelOutputStream;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.util.Collections;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static com.scichart.drawing.utility.ColorUtil.Chartreuse;
import static com.scichart.drawing.utility.ColorUtil.CornflowerBlue;
import static com.scichart.drawing.utility.ColorUtil.DarkBlue;
import static com.scichart.drawing.utility.ColorUtil.DarkGreen;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;


import java.text.DateFormat;
import java.util.Date;
import java.lang.Object;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link sci.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link sci#newInstance} factory method to
 * create an instance of this fragment.
 */

public class sci extends Fragment {

    protected final SciChartBuilder sciChartBuilder = SciChartBuilder.instance();
    volatile UniformHeatmapDataSeries<Integer, Integer, Double> werte = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, 7, 1000);
    volatile UniformHeatmapDataSeries<Integer, Integer, Double> view_werte = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, 7, 1000);
    volatile UniformHeatmapDataSeries<Integer, Integer, Double> wertex = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, 7, 1000);
    volatile UniformHeatmapDataSeries<Integer, Integer, Double> wertey = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, 7, 1000);
    volatile UniformHeatmapDataSeries<Integer, Integer, Double> wertez = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, 7, 1000);

    volatile int ind=0;
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    public UniformHeatmapDataSeries<Integer, Integer, Double> createDataSeries() {
        final int w = 7;
        final int h = 1000;

        final UniformHeatmapDataSeries<Integer, Integer, Double> dataSeries = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, w, h);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                dataSeries.updateZAt(x, y, 0.0);
            }
        }

        return dataSeries;
    }

     void changwerte(float[][] data, int e_num) {
        final int w = 7;
        final int h = 1000;
         ind++;
         if(ind>999)
         {ind=0;}

        for (int x = 0; x < w; x++) {
           // Log.d("Changewerte","x="+Integer.toString(x)+" data="+Double.toString(Math.sqrt(data[x+1][0]*data[x+1][0]+data[x+1][1]*data[x+1][1]+data[x+1][2]*data[x+1][2])));
                werte.updateZAt(x,ind, Math.sqrt(data[x+1][0]*data[x+1][0]+data[x+1][1]*data[x+1][1]+data[x+1][2]*data[x+1][2]));
                wertex.updateZAt(x,ind,(double)data[x+1][0]);
                wertey.updateZAt(x,ind,(double)data[x+1][1]);
                wertez.updateZAt(x,ind,(double)data[x+1][2]);
                if (e_num < 3) {
                    view_werte.updateZAt(x,ind,(double)data[x+1][e_num]);
                }
                else {
                    view_werte.updateZAt(x,ind, Math.sqrt(data[x+1][0]*data[x+1][0]+data[x+1][1]*data[x+1][1]+data[x+1][2]*data[x+1][2]));
                }
        }
       // Log.d("Changewerte","Läuft und ind="+Integer.toString(ind));

    }

    void resetDataSeries() {
        view_werte = createDataSeries();
        werte = createDataSeries();
        wertex = createDataSeries();
        wertey = createDataSeries();
        wertez = createDataSeries();
        ind=0;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    void ausgabe() {
        if(isExternalStorageWritable())
        {}
        try {
            File imagePath = new File(getActivity().getFilesDir()+"/external_files");

            if (!imagePath.exists())
            {imagePath.mkdir();}
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
            Date date = new Date();
            String currentDateTimeString = dateFormat.format(date);

            File file = new File(imagePath.getPath(), currentDateTimeString+".txt");
           // File file = new File(imagePath.getPath(), "magnetic_field_data.txt");

            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            for (int x = 0; x < 7; x++) {
                for (int y = 0; y < 1000; y++) {
                    myOutWriter.append(Integer.toString(x)+"  "+Integer.toString(y)+"  "+Double.toString(werte.getZValueAt(x,y))+"  "+Double.toString(wertex.getZValueAt(x,y))+"  "+Double.toString(wertey.getZValueAt(x,y))+"  "+Double.toString(wertez.getZValueAt(x,y))+"\n");
                  }
                }
            myOutWriter.close();
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);

            Uri fileUri=null;
            try {
                fileUri = FileProvider.getUriForFile(
                        getActivity().getApplicationContext(),
                        "com.prototyp2",
                        file);
            } catch (IllegalArgumentException e) {
                Log.e("File Selector",
                        "The selected file can't be shared " );
            }
            Uri uri = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName(), file);


            getActivity().grantUriPermission( getActivity().getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                    .setStream(uri) // uri from FileProvider
                    .setType("text/html")
                    .getIntent()
                    .setAction(Intent.ACTION_SEND) //Change if needed
                    .setDataAndType(uri, "text/*")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void savedefecttxt() {
        if(isExternalStorageWritable())
        {}
        try {

            File imagePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"data_prototyp"+ File.separator +"defect");

            if (!imagePath.exists()) {
                Toast.makeText(getActivity().getApplicationContext(), "File can't be saved", Toast.LENGTH_LONG).show();
            }

            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date()).replace(" ","_");
            File file = new File(imagePath.getPath(), currentDateTimeString+".txt");

            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            for (int x = 0; x < 7; x++) {
                for (int y = 0; y < 1000; y++) {
                    myOutWriter.append(Integer.toString(x)+"  "+Integer.toString(y)+"  "+Double.toString(werte.getZValueAt(x,y))+"  "+Double.toString(wertex.getZValueAt(x,y))+"  "+Double.toString(wertey.getZValueAt(x,y))+"  "+Double.toString(wertez.getZValueAt(x,y))+"\n");
                }
            }
            myOutWriter.close();
            fOut.flush();
            fOut.close();
            file.setWritable(true, false);
            file.setReadable(true, false);

            //Toast.makeText(getActivity().getApplicationContext(), "File saved to external storage under: "+imagePath.getPath()+"/"+currentDateTimeString, Toast.LENGTH_LONG).show();

            //upload(imagePath.getPath(), currentDateTimeString+".txt");
            new UploadFileToServer().execute(imagePath.getPath(), currentDateTimeString+".txt", "defect");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void saveworkingtxt() {
        if(isExternalStorageWritable())
        {}
        try {
            File imagePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"data_prototyp"+ File.separator +"reference");

            if (!imagePath.exists()) {
                Toast.makeText(getActivity().getApplicationContext(), "File can't be saved", Toast.LENGTH_LONG).show();
            }

            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date()).replace(" ","_");

            File file = new File(imagePath.getPath(), currentDateTimeString+".txt");

            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            for (int x = 0; x < 7; x++) {
                for (int y = 0; y < 1000; y++) {
                    myOutWriter.append(Integer.toString(x)+"  "+Integer.toString(y)+"  "+Double.toString(werte.getZValueAt(x,y))+"  "+Double.toString(wertex.getZValueAt(x,y))+"  "+Double.toString(wertey.getZValueAt(x,y))+"  "+Double.toString(wertez.getZValueAt(x,y))+"\n");
                }
            }
            myOutWriter.close();
            fOut.flush();
            fOut.close();

            file.setWritable(true, false);
            file.setReadable(true, false);
            //Toast.makeText(getActivity().getApplicationContext(), "File saved to external storage under: "+imagePath.getPath()+"/"+currentDateTimeString, Toast.LENGTH_LONG).show();

            new UploadFileToServer().execute(imagePath.getPath(), currentDateTimeString+".txt", "working");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Uploading the file to (SSH) FTP-server
     * */
    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... paths) {
            try {
                upload(paths[0], paths[1], paths[2]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "upload started";
        }

        @SuppressWarnings("deprecation")
        private String upload(String directory, String filename, String status) throws IOException {
            String responseString = null;

            SSHClient ssh = new SSHClient();
            try {
                //ssh.loadKnownHosts();
                ssh.addHostKeyVerifier("57:6b:6d:f6:b6:76:27:3a:7f:39:d1:b3:21:d7:03:dd");
                ssh.connect("153.96.76.150", 22);
                try {
                    ssh.authPassword("user1user1", "user1user1");

                    SFTPClient sftp = ssh.newSFTPClient();

                    try {
                        sftp.put(new FileSystemFile(directory + File.separator + filename), File.separator + "var" + File.separator + "www" + File.separator + "html" + File.separator + "files" + File.separator + status + File.separator + filename);
                    }  catch (IOException e) {
                        e.printStackTrace();
                    }finally {

                        try {
                            Session session = ssh.startSession();
                            Session.Command cmd = session.exec("/var/www/html/files/working/auswertung.o "+filename);
                            System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());
                            cmd.join(10, TimeUnit.SECONDS);
                            //session.exec("/var/www/html/files/working/auswertung.o "+filename);
                            // SSH Channel



                        }  catch (IOException e) {
                            e.printStackTrace();
                        }

                        sftp.close();
                    }
                } catch (UserAuthException e) {
                    e.printStackTrace();
                    responseString = e.toString();
                } catch (TransportException e) {
                    e.printStackTrace();
                    responseString = e.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    responseString = e.toString();
                } finally {
                    ssh.disconnect();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return responseString;
        }


        @Override
        protected void onPostExecute(String result) {
            //Log.e(TAG, "Response from server: " + result);

            super.onPostExecute(result);
        }

    }


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public sci() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment sci.
     */
    // TODO: Rename and change types and number of parameters
    public static sci newInstance(String param1, String param2) {
        sci fragment = new sci();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @LayoutRes
    protected int getLayoutId() {
        return R.layout.fragment_sci;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sci, container, false);
         final int WIDTH = 300, HEIGHT = 200;

        // Find the surface by id
        SciChartSurface surface = (SciChartSurface) view.findViewById(R.id.chart);

        // Create a numeric X axis
        final IAxis xAxis = new NumericAxis(getActivity());
        // xAxis.setAxisTitle("X Axis Title");

        // Turn off AutoRanging and set the VisibleRange
        xAxis.setAutoRange(AutoRange.Never);
        xAxis.setVisibleRange(new DoubleRange(0d, 7d));

        // Add the X axis to the XAxes collection of the surface
        Collections.addAll(surface.getXAxes(), xAxis);

        // Create a numeric Y axis
        final IAxis yAxis = new NumericAxis(getActivity());

        // Set a title for Y axis
        //yAxis.setAxisTitle("Y Axis Title");

        // Turn off AutoRanging and set the VisibleRange
        yAxis.setAutoRange(AutoRange.Never);
        yAxis.setVisibleRange(new DoubleRange(0d, 1000d));

        // Add the Y axis to the YAxes collection of the surface
        Collections.addAll(surface.getYAxes(), yAxis);

        // Create a TextAnnotation
        TextAnnotation textAnnotation = new TextAnnotation(getActivity());

        // Specify the position and alignment for the TextAnnotation
        textAnnotation.setHorizontalAnchorPoint(HorizontalAnchorPoint.Center);
        textAnnotation.setVerticalAnchorPoint(VerticalAnchorPoint.Center);
        textAnnotation.setX1(5.0);
        textAnnotation.setY1(50.0);

        // Set a FontStyle for the TextAnnotation
        FontStyle fontStyle = new FontStyle(70, ColorUtil.Orange);
        textAnnotation.setFontStyle(fontStyle);

        // Set the inscription
        textAnnotation.setText("");

        // Add the annotation to the Annotations collection of the surface
        Collections.addAll(surface.getAnnotations(), textAnnotation);

        // Create interactivity modifiers
        PinchZoomModifier pinchZoom = new PinchZoomModifier();
        ZoomExtentsModifier zoomExtents = new ZoomExtentsModifier();
        ModifierGroup chartModifiers = new ModifierGroup(pinchZoom, zoomExtents);

        // Add the interactions to the ChartModifiers collection of the surface
        Collections.addAll(surface.getChartModifiers(), chartModifiers);

        // Create a Heatmap Series
        final FastUniformHeatmapRenderableSeries heatmapRenderableSeries = new FastUniformHeatmapRenderableSeries<>();

        // Specify ZValue range
        heatmapRenderableSeries.setMinimum(-200);
        heatmapRenderableSeries.setMaximum(200);

        // Create a ColorMap
        ColorMap colorMap = new ColorMap(new int[]{DarkBlue, CornflowerBlue, DarkGreen, Chartreuse, Yellow, Red}, new float[]{0f, 0.2f, 0.4f, 0.6f, 0.8f, 1});

        // Apply the ColorMap
        heatmapRenderableSeries.setColorMap(colorMap);



        // Create a ColorMap Legend
        // It requires to be added to the Layout somewhere
        SciChartHeatmapColourMap colorMapView = new SciChartHeatmapColourMap(getActivity());

        // Configure the ColorMap Legend
        colorMapView.setColorMap(heatmapRenderableSeries.getColorMap());
        colorMapView.setMinimum(heatmapRenderableSeries.getMinimum());
        colorMapView.setMaximum(heatmapRenderableSeries.getMaximum());

        // Create a DataSeries for the Heatmap
        UniformHeatmapDataSeries<Integer, Integer, Double> dataSeries = new UniformHeatmapDataSeries<>(Integer.class, Integer.class, Double.class, WIDTH, HEIGHT);
        // Assume some data has been added to the dataSeries here
        //dataSeries.updateZValues());

        // Assign the dataSeries to the Heatmap

        view_werte=createDataSeries();
        werte=createDataSeries();
        wertex=createDataSeries();
        wertey=createDataSeries();
        wertez=createDataSeries();

        heatmapRenderableSeries.setDataSeries(view_werte);

        final  Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                heatmapRenderableSeries.setDataSeries(view_werte);
                handler.postDelayed(this, 100);
            }
        };

        handler.postDelayed(r, 1000);


        // Add the Heatmap to the RenderableSeriesCollection of the surface
        Collections.addAll(surface.getRenderableSeries(), heatmapRenderableSeries);



        return view;

      //  return inflater.inflate(R.layout.fragment_sci, container, false);
    }

    public void change_component() {

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}






