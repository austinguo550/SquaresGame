package austinguo550.testmultitouch;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by James on 7/6/2015.
 */
public class MainThread extends Thread {
    private int FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel){
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/FPS;

        while(running)
        {
            startTime = System.currentTimeMillis();
            //canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    if(GamePanel.firstTime){
                        this.gamePanel.initApp(canvas);
                        //GamePanel.firstTime = false;
                    }
                    this.gamePanel.update(canvas);
                    this.gamePanel.draw(canvas);

                }
            }
            catch(Exception e){
                //e.printStackTrace();
            }
            finally {
                if(canvas!=null){
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime)/1000000;
            waitTime = targetTime - timeMillis;

            try{
                this.sleep(waitTime);
            }
            catch(Exception e){

            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == FPS) {
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }
        }
    }

    public void setRunning(boolean run){
        running = run;
    }

}
