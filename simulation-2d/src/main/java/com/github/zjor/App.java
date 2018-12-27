package com.github.zjor;

import processing.core.PApplet;
import processing.event.MouseEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class App extends PApplet {

    private static final long DELAY_MILLIS = 1L;
    private static final float SPEED_OF_SOUND = 0.05f;

    enum State {
        PROBE_DEPLOYMENT,
        WAITING_FOR_SIGNAL,
        SIMULATION,
        DETECTION
    }

    private static final String DEPLOY_PROBES_MESSAGE = "Click to deploy probes ({0}/3)";
    private static final String EMIT_SIGNAL_MESSAGE = "Click anywhere to emit a signal";
    private static final String PROPAGATING_WAVE_MESSAGE = "Propagating the sound wave...";
    private static final String DETECTING_SOURCE_MESSAGE = "Detecting sound source";

    private List<Point> probes = new ArrayList<>();
    private volatile float[] activations = new float[]{-1, -1, -1};
    private volatile float[] activationsEst = new float[]{-1, -1, -1};
    private String statusBarText;
    private volatile State state;
    private Point signalSource;
    private volatile Point detectedSource;

    private volatile float time;
    private volatile long lastUpdateTimestamp;
    private volatile float waveRadius;

    private Runnable timerHandler = () -> {
        while (state == State.SIMULATION) {
            long now = System.currentTimeMillis();
            if (now - lastUpdateTimestamp >= DELAY_MILLIS) {
                evolve((float)(now - lastUpdateTimestamp));
                lastUpdateTimestamp = now;
            } else {
                try {
                    Thread.sleep(DELAY_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void settings() {
        super.settings();
        size(800, 600);
    }

    @Override
    public void setup() {
        super.setup();

        state = State.PROBE_DEPLOYMENT;

        background(0);
        ellipseMode(CENTER);
        textMode(SHAPE);
        statusBarText = MessageFormat.format(DEPLOY_PROBES_MESSAGE, 0);
    }

    @Override
    public void draw() {
        color(0);
        fill(0);
        stroke(0);
        rect(0, 0, width, height);

        stroke(255);

        if (state == State.SIMULATION || state == State.DETECTION) {
            noFill();
            stroke(100, 250, 100);
            ellipse(signalSource.getX(), signalSource.getY(), waveRadius * 2, waveRadius * 2);
            ellipse(signalSource.getX(), signalSource.getY(), 8, 8);
        }

        stroke(100, 100, 250);
        for (int i = 0; i < probes.size(); i++) {
            Point p = probes.get(i);
            noFill();
            ellipse(p.getX(), p.getY(), 16, 16);
            if (activations[i] != -1) {
                fill(255);
                text(activations[i], p.getX() + 8, p.getY() - 8);
                text(activationsEst[i], p.getX() + 8, p.getY() + 8);

                noFill();
                float r = activations[i] * SPEED_OF_SOUND;
                ellipse(p.getX(), p.getY(), r * 2, r * 2);
            }
        }


        if (detectedSource != null) {
            fill(250, 100, 100);
            stroke(250, 100, 100);
            ellipse(detectedSource.getX(), detectedSource.getY(), 16, 16);
        }

        fill(255);
        text(statusBarText, 4, height - 4);
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (state == State.PROBE_DEPLOYMENT) {
            probes.add(new Point(event.getX(), event.getY()));
            statusBarText = MessageFormat.format(DEPLOY_PROBES_MESSAGE, probes.size());
            if (probes.size() == 3) {
                state = State.WAITING_FOR_SIGNAL;
                statusBarText = EMIT_SIGNAL_MESSAGE;
            }
        } else if (state == State.WAITING_FOR_SIGNAL) {
            state = State.SIMULATION;
            statusBarText = PROPAGATING_WAVE_MESSAGE;
            signalSource = new Point(event.getX(), event.getY());
            time = 0L;
            new Thread(timerHandler).start();
            lastUpdateTimestamp = System.currentTimeMillis();
        }
    }

    private void evolve(float dt) {
        time += dt;
        waveRadius = SPEED_OF_SOUND * time;
        if (waveRadius >= Math.max(width, height) || updateActivations()) {
            state = State.DETECTION;
            statusBarText = DETECTING_SOURCE_MESSAGE;
            detectSoundSource();
        }
    }

    private boolean updateActivations() {
        for (int i = 0; i < probes.size(); i++) {
            if (activations[i] == -1 && probes.get(i).d(signalSource) <= waveRadius) {
                activations[i] = time;
                activationsEst[i] = probes.get(i).d(signalSource) / SPEED_OF_SOUND;
            }
        }
        return activations[0] != -1 && activations[1] != -1 && activations[2] != -1;
    }

    private void detectSoundSource() {
        float x1 = probes.get(0).getX();
        float x2 = probes.get(1).getX();
        float x3 = probes.get(2).getX();

        float y1 = probes.get(0).getY();
        float y2 = probes.get(1).getY();
        float y3 = probes.get(2).getY();

        float t1 = activations[0];
        float t2 = activations[1];
        float t3 = activations[2];

        float a11 = x1 - x2;
        float a21 = x2 - x3;
        float a12 = y1 - y2;
        float a22 = y2 - y3;

        final float c = SPEED_OF_SOUND;

        float b1 = (x1 * x1 - x2 * x2 + y1 * y1 - y2 * y2 - c * c * (t1 * t1 - t2 * t2)) / 2;
        float b2 = (x2 * x2 - x3 * x3 + y2 * y2 - y3 * y3 - c * c * (t2 * t2 - t3 * t3)) / 2;

        float det = a11 * a22 - a12 * a21;
        float detX = b1 * a22 - b2 * a12;
        float detY = a11 * b2 - a21 * b1;

        detectedSource = new Point(detX / det, detY / det);
        System.out.println(detectedSource);
        System.out.println(width + "; " + height);
    }

    public static void main(String[] args) {
        PApplet.main(App.class);
    }
}
