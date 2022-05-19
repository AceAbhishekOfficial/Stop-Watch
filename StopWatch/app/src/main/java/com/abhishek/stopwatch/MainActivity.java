package com.abhishek.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    int millis=0;
    int millis2=0;
    int millis_lap=0;
    int millis2_lap=0;

    int laps=0;
    Handler runner;

    Chronometer stopwatch;
    long stopwatch_offset=0;

    Chronometer stopwatch_lap;
    long stopwatch_offset_lap=0;

    TextView txt_millis;
    TextView txt_millis2;
    TextView txt_millis_lap;
    TextView txt_millis2_lap;


    TextView laprecord;
    TextView lapinfo;
    TextView lap_dot;
    View divider;
    View scroll;

    Button btn1;
    Button btn2;
    Runnable run_millis1_lap;
    Runnable run_millis2_lap;

    boolean STOPWATCH_RUNNING;
    boolean LAP_RUNNING;

    String Lap="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        showName();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        stopwatch = findViewById(R.id.stopwatch);
        stopwatch_lap = findViewById(R.id.stopwatch_lap);

        txt_millis = findViewById(R.id.time_milli1);
        txt_millis2 = findViewById(R.id.time_milli2);
        txt_millis_lap = findViewById(R.id.time_lap_milli1);
        txt_millis2_lap = findViewById(R.id.time_lap_milli2);

        laprecord = findViewById(R.id.lap);
        lapinfo= findViewById(R.id.lap_info);
        divider = findViewById(R.id.divider);
        lap_dot = findViewById(R.id.lap_dot);
        scroll = findViewById(R.id.scroll);
        hidelap();
        runner = new Handler();

        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);

        btn1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!STOPWATCH_RUNNING)
                    start();
                else
                    stop();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!STOPWATCH_RUNNING)
                    reset();
                else
                    startLap();
            }
        });
    }


    public void start()
    {
        stopwatch.setBase(SystemClock.elapsedRealtime()-stopwatch_offset);
        stopwatch.start();
        if(LAP_RUNNING) {
            stopwatch_lap.setBase((SystemClock.elapsedRealtime()-stopwatch_offset_lap));
            stopwatch_lap.start();
            startMillisLap();
            startMillis2Lap();
        }
        startMillis();
        startMillis2();
        STOPWATCH_RUNNING = true;
        btn1.setText("Stop");
        btn2.setText("Lap");
    }
    public void stop()
    {
        stopwatch.stop();
        stopwatch_lap.stop();
        stopwatch_offset= SystemClock.elapsedRealtime() - stopwatch.getBase();
        stopwatch_offset_lap= SystemClock.elapsedRealtime() - stopwatch_lap.getBase();

        STOPWATCH_RUNNING = false;
        btn1.setText("Resume");
        btn2.setText("Reset");
    }
    public void reset()
    {
        stopwatch_offset=0;
        stopwatch_offset_lap=0;
        millis=0;
        millis2=0;
        millis_lap=0;
        millis2_lap=0;
        stopwatch.setBase(SystemClock.elapsedRealtime());
        stopwatch_lap.setBase(SystemClock.elapsedRealtime());
        txt_millis.setText("0");
        txt_millis2.setText("0");
        txt_millis_lap.setText("0");
        txt_millis2_lap.setText("0");
        btn1.setText("Start");
        btn2.setText("lap");
        Lap="";
        laps=0;
        LAP_RUNNING=false;
        hidelap();
    }
    public void startLap()
    {
        if(!LAP_RUNNING)
            showlap();
        runner.removeCallbacks(run_millis1_lap);
        runner.removeCallbacks(run_millis2_lap);
        recordLap();
        millis_lap=0;
        millis2_lap=0;
        stopwatch_lap.setBase(SystemClock.elapsedRealtime());
        stopwatch_lap.start();
        startMillisLap();
        startMillis2Lap();
        LAP_RUNNING=true;

    }
    public void recordLap()
    {
        String t2 = (String) stopwatch.getText();
        String t1 = (String) stopwatch_lap.getText();
        laps++;
        String t="";
        if(Lap.length()==0)
            t=t2+"."+millis+millis2+"\t\t\t\t\t"+t2+"."+millis+millis2;
        else
         t=t1+"."+millis_lap+millis2_lap+"\t\t\t\t\t"+t2+"."+millis+millis2;
        if(laps<10)
            t= "0"+laps+"\t\t\t\t\t\t"+t;
        else
            t=laps+"\t\t\t\t\t\t"+t;
        Lap=t+"\n"+Lap;
        laprecord.setText(Lap);
    }
    public void showName()
    {
        Toast toast = Toast.makeText(getApplicationContext(),
                "App Made by Abhishek Srivastava",
                Toast.LENGTH_SHORT);

        toast.show();
        toast.setGravity(Gravity.CENTER, 0, 0);
    }


    ////////////////////////////////////////////////// internal fucntions ////////////////////////////

    public void hidelap()
    {
        laprecord.setVisibility(View.INVISIBLE);
       lapinfo.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.INVISIBLE);
        stopwatch_lap.setVisibility(View.INVISIBLE);
        txt_millis_lap.setVisibility(View.INVISIBLE);
        txt_millis2_lap.setVisibility(View.INVISIBLE);
        lap_dot.setVisibility(View.INVISIBLE);
        scroll.setVisibility(View.INVISIBLE);

    }
    public void showlap()
    {
        laprecord.setVisibility(View.VISIBLE);
        lapinfo.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
        stopwatch_lap.setVisibility(View.VISIBLE);
        txt_millis_lap.setVisibility(View.VISIBLE);
        txt_millis2_lap.setVisibility(View.VISIBLE);
        lap_dot.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.VISIBLE);

    }
    public void startMillis()
    {
        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                if(STOPWATCH_RUNNING)
                {
                    millis++;
                    if(millis==10) millis=0;
                    txt_millis.setText(""+millis);
                    runner.postDelayed(this,100);
                }

            }

        };
        runner.postDelayed(r,100);
    }
    public void startMillis2()
    {
       Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                if(STOPWATCH_RUNNING)
                {
                    millis2++;
                    if(millis2==10) millis2=0;
                    txt_millis2.setText(""+millis2);
                    runner.postDelayed(this,10);
                }

            }

        };
        runner.postDelayed(r,10);
    }
    public void startMillisLap()
    {
        run_millis1_lap = new Runnable()
        {
            @Override
            public void run()
            {
                if(STOPWATCH_RUNNING)
                {
                    millis_lap++;
                    if(millis_lap==10) millis_lap=0;
                    txt_millis_lap.setText(""+millis_lap);
                    runner.postDelayed(this,100);
                }

            }

        };
        runner.postDelayed(run_millis1_lap,100);
    }
    public void startMillis2Lap()
    {
        run_millis2_lap = new Runnable()
        {
            @Override
            public void run()
            {
                if(STOPWATCH_RUNNING)
                {
                    millis2_lap++;
                    if(millis2_lap==10) millis2_lap=0;
                    txt_millis2_lap.setText(""+millis2_lap);
                    runner.postDelayed(this,10);
                }

            }

        };
        runner.postDelayed(run_millis2_lap,10);
    }

}