package com.fourshape.a4mcqplus.mcq_test;

import android.os.Handler;

public class TestClock {

    private int time;
    private Handler handler;
    private Runnable runnable;
    private TestClockListener testClockListener;

    public TestClock () {

        if (handler == null)
            handler = new Handler();

        if (runnable == null)
            runnable = new Runnable() {
                @Override
                public void run() {
                    tickClock();
                }
            };

    }

    public void setClockTickListener (TestClockListener testClockListener) {
        this.testClockListener = testClockListener;
    }

    private void tickClock () {

        time++;

        if (testClockListener != null) {
            testClockListener.onTick(time);
        }

        if (handler != null && runnable != null)
            handler.postDelayed(runnable, 1000);

    }

    public void startClock () {
        if (handler != null && runnable != null)
            handler.postDelayed(runnable, 1000);
    }

    public void stopClock () {
        if (handler != null && runnable != null)
            handler.removeCallbacks(runnable);
    }

}
