package Negocio;

import javax.swing.JProgressBar;

public class HiloProgreso extends Thread {

    JProgressBar progreso;

    public HiloProgreso(JProgressBar progreso1) {
        super();
        this.progreso = progreso1;
    }

    public void run() {
        for (int i = 1; i <= 100; i++) {
            progreso.setValue(i);
            try {
                // pausa para el splash
                Thread.sleep(30);
            } catch (Exception e) {
            }
        }
    }
}
