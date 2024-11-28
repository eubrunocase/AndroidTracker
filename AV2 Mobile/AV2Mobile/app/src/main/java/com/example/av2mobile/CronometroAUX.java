//SERVIÇO_AUXILIAR
package com.example.av2mobile;

import android.os.Handler;
import android.widget.TextView;

public class CronometroAUX {
    private final TextView textView;
    private long initialTime;
    private Handler handler;
    private boolean isRunning;

    private final static long MILLIS_IN_SEC = 1000L;
    private final static int SECS_IN_MIN = 60;

    public CronometroAUX(TextView textView, long initialTime) {
        this.textView = textView;
        this.initialTime = initialTime;
        this.handler = new Handler();
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long elapsedTime = System.currentTimeMillis() - initialTime;
                long seconds = elapsedTime / MILLIS_IN_SEC;
                String formattedTime = String.format("%02d:%02d", seconds / SECS_IN_MIN, seconds % SECS_IN_MIN);
                textView.setText(formattedTime);
                handler.postDelayed(runnable, MILLIS_IN_SEC);
            }
        }
    };

    public void start() {
        if (!isRunning) {
            isRunning = true;
            initialTime = System.currentTimeMillis();
            handler.postDelayed(runnable, MILLIS_IN_SEC);
        }
    }

    public void pause() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(runnable);
        }
    }

    public void stop() {
        isRunning = false;
        handler.removeCallbacks(runnable);
        textView.setText("00:00");
        initialTime = System.currentTimeMillis();
    }

    public void reset() {
        isRunning = false;
        handler.removeCallbacks(runnable);
        textView.setText(String.format("%02d:%02d", initialTime / SECS_IN_MIN, (int) (initialTime % SECS_IN_MIN)));
    }
}
