package austinguo550.testmultitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;


/**
 * Created by James on 7/5/2015.
 */
class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    //In this test, handle maximum of 2 pointer
    final int MAX_POINT_CNT = 2;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //float[] x = new float[MAX_POINT_CNT];
    //float[] y = new float[MAX_POINT_CNT];
    float pressedX;
    float pressedY;
    boolean[] isTouch = new boolean[MAX_POINT_CNT];

    //float[] x_last = new float[MAX_POINT_CNT];
    //float[] y_last = new float[MAX_POINT_CNT];
    float lastPressedX;
    float lastPressedY;
    boolean[] isTouch_last = new boolean[MAX_POINT_CNT];


    private MainThread thread;
    SurfaceHolder surfaceHolder;
    //volatile boolean running = false;

    //volatile boolean touched = false;
    //volatile float touched_x, touched_y;

    //registering touch
    /**
     * Max allowed duration for a "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 1000;
    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;
    private long pressStartTime;

    //for updating colors
    Random random = new Random();
    private int boxNum;
    //changes every time boxes change order
    private float[] box1Bounds = new float[4]; //red
    private float[] box2Bounds = new float[4]; //blue
    private float[] box3Bounds = new float[4]; //green
    private float[] box4Bounds = new float[4]; //yellow

    private float[] botLeftBox = new float[4]; //red
    private float[] botRightBox = new float[4]; //blue
    private float[] topLeftBox = new float[4]; //green
    private float[] topRightBox = new float[4]; //yellow

    public static boolean firstTime = true;

    //instance variables ended







    public GamePanel(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        surfaceHolder = getHolder();
        //callback to intercept events
        surfaceHolder.addCallback(this);
        thread = new MainThread(surfaceHolder, this);
        //make gamepanel focusable so it can handle events
        setFocusable(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
        firstTime=true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry){
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //makes no sense here retry = false;
        }
    }

    //TODO if errors with onResume or onPause, check v0.0.6

    public void onResumeGamePanel(){
        thread.setRunning(true);
        thread.start();
        firstTime=true;
    }

    public void onPauseGamePanel(){
        boolean retry = true;
        while(retry){
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //makes no sense here retry = false;
        }
    }

    public void initApp(Canvas canvas) {
        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        //assigning box coordinates
        botLeftBox[0] = (float) 0.05 * canvasWidth;
        botLeftBox[1] = (float) 0.775 * canvasHeight;
        botLeftBox[2] = (float) 0.45 * canvasWidth;
        botLeftBox[3] = (float) 0.525 * canvasHeight;

        botRightBox[0] = (float) .55 * canvasWidth;
        botRightBox[1] = (float) 0.775 * canvasHeight;
        botRightBox[2] = (float) .95 * canvasWidth;
        botRightBox[3] = (float) 0.525 * canvasHeight;

        topLeftBox[0] = (float) 0.05 * canvasWidth;
        topLeftBox[1] = (float) .475 * canvasHeight;
        topLeftBox[2] = (float) 0.45 * canvasWidth;
        topLeftBox[3] = (float) .225 * canvasHeight;

        topRightBox[0] = (float) .55 * canvasWidth;
        topRightBox[1] = (float) .475 * canvasHeight;
        topRightBox[2] = (float) .95 * canvasWidth;
        topRightBox[3] = (float) .225 * canvasHeight;

        //paint the boxes
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawRect((float) 0.05 * canvasWidth, (float) 0.775 * canvasHeight, (float) 0.45 * canvasWidth, (float) 0.525 * canvasHeight, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        canvas.drawRect((float) .55 * canvasWidth, (float) .775 * canvasHeight, (float) .95 * canvasWidth, (float) .525 * canvasHeight, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        canvas.drawRect((float) 0.05 * canvasWidth, (float) .475 * canvasHeight, (float) .45 * canvasWidth, (float) .225 * canvasHeight, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        canvas.drawRect((float).55*canvasWidth, (float).475*canvasHeight, (float).95*canvasWidth, (float).225*canvasHeight, paint);

        //assigning boxBounds to the same as the top, bot, left, right boxes
        box1Bounds = botLeftBox.clone();
        box2Bounds = botRightBox.clone();
        box3Bounds = topLeftBox.clone();
        box4Bounds = topRightBox.clone();
    }

    private void refresh() {

    }

    private void change(Canvas canvas) {

        //reset canvas
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        surfaceHolder.unlockCanvasAndPost(canvas);

        int newRed = random.nextInt((4 - 1) + 1) + 1;
        int newBlue = random.nextInt((4 - 1) + 1) + 1;
        if(newBlue == newRed) {
            while(newBlue == newRed) newBlue = random.nextInt((4 - 1) + 1) + 1;
        }
        int newGreen = random.nextInt((4 - 1) + 1) + 1;
        if(newGreen == newRed || newGreen == newBlue){
            while(newGreen == newRed || newGreen == newBlue) newGreen = random.nextInt((4 - 1) + 1) + 1;
        }
        int newYellow = random.nextInt((4 - 1) + 1) + 1;
        if(newYellow == newRed || newYellow == newBlue || newYellow == newGreen){
           while(newYellow == newRed || newYellow == newBlue || newYellow == newGreen) newYellow = random.nextInt((4 - 1) + 1) + 1;
        }

        canvas = surfaceHolder.lockCanvas();

        //draw red
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        switch(newRed){
            case 1: canvas.drawRect(botLeftBox[0],botLeftBox[1], botLeftBox[2], botLeftBox[3], paint); box1Bounds = botLeftBox.clone(); break;
            case 2: canvas.drawRect(botRightBox[0],botRightBox[1], botRightBox[2], botRightBox[3], paint); box1Bounds = botRightBox.clone(); break;
            case 3: canvas.drawRect(topLeftBox[0],topLeftBox[1], topLeftBox[2], topLeftBox[3], paint); box1Bounds = topLeftBox.clone(); break;
            case 4: canvas.drawRect(topRightBox[0],topRightBox[1], topRightBox[2], topRightBox[3], paint); box1Bounds = topRightBox.clone(); break;
        }
        //draw blue
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        switch(newBlue){
            case 1: canvas.drawRect(botLeftBox[0],botLeftBox[1], botLeftBox[2], botLeftBox[3], paint); box2Bounds = botLeftBox.clone(); break;
            case 2: canvas.drawRect(botRightBox[0],botRightBox[1], botRightBox[2], botRightBox[3], paint); box2Bounds = botRightBox.clone(); break;
            case 3: canvas.drawRect(topLeftBox[0],topLeftBox[1], topLeftBox[2], topLeftBox[3], paint); box2Bounds = topLeftBox.clone(); break;
            case 4: canvas.drawRect(topRightBox[0],topRightBox[1], topRightBox[2], topRightBox[3], paint); box2Bounds = topRightBox.clone(); break;
        }
        //draw green
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        switch(newGreen){
            case 1: canvas.drawRect(botLeftBox[0],botLeftBox[1], botLeftBox[2], botLeftBox[3], paint); box3Bounds = botLeftBox.clone(); break;
            case 2: canvas.drawRect(botRightBox[0],botRightBox[1], botRightBox[2], botRightBox[3], paint); box3Bounds = botRightBox.clone(); break;
            case 3: canvas.drawRect(topLeftBox[0],topLeftBox[1], topLeftBox[2], topLeftBox[3], paint); box3Bounds = topLeftBox.clone(); break;
            case 4: canvas.drawRect(topRightBox[0],topRightBox[1], topRightBox[2], topRightBox[3], paint); box3Bounds = topRightBox.clone(); break;
        }
        //draw yellow
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        switch(newYellow){
            case 1: canvas.drawRect(botLeftBox[0],botLeftBox[1], botLeftBox[2], botLeftBox[3], paint); box4Bounds = botLeftBox.clone(); break;
            case 2: canvas.drawRect(botRightBox[0],botRightBox[1], botRightBox[2], botRightBox[3], paint); box4Bounds = botRightBox.clone(); break;
            case 3: canvas.drawRect(topLeftBox[0],topLeftBox[1], topLeftBox[2], topLeftBox[3], paint); box4Bounds = topLeftBox.clone(); break;
            case 4: canvas.drawRect(topRightBox[0],topRightBox[1], topRightBox[2], topRightBox[3], paint); box4Bounds = topRightBox.clone(); break;
        }
        /*try {
            thread.sleep(3000,5000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }*/

        surfaceHolder.unlockCanvasAndPost(canvas);

    }

    //might need to delete onDraw() later

    /*@Override
    protected void onDraw(Canvas canvas) {
        paint.setStrokeWidth(6f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawPath(path, paint);
    }*/

    /*@Override
    public void run() {
        try {
            while(running) {
                if (surfaceHolder.getSurface().isValid()) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    //todo delete this
                    float canvasWidth = canvas.getWidth();
                    float canvasHeight = canvas.getHeight();

                    if(firstTime) {
                        //drawing the app
                        initApp(canvas);
                        //onDraw(canvas);

                        if (isTouch[0]) {
                            if (isTouch_last[0]) {
                                //if you touch box 1 first
                                if (pressedX > box1Bounds[0] && pressedX < box1Bounds[2] && pressedY > box1Bounds[3] && pressedY < box1Bounds[1]) {
                                    boxNum = 1;
                                    paint.setColor(Color.BLACK);
                                    canvas.drawRect((float) 0.05 * canvasWidth, (float) 0.775 * canvasHeight, (float) 0.45 * canvasWidth, (float) 0.525 * canvasHeight, paint);
                                    firstTime = false;
                                    //change(canvas);
                                }
                                //if you touch box 2 first
                                if (pressedX > box2Bounds[0] && pressedX < box2Bounds[2] && pressedY > box2Bounds[3] && pressedY < box2Bounds[1]) {
                                    boxNum = 2;
                                    paint.setColor(Color.BLACK);
                                    canvas.drawRect((float) .55 * canvasWidth, (float) .775 * canvasHeight, (float) .95 * canvasWidth, (float) .525 * canvasHeight, paint);
                                    firstTime = false;
                                    //change(canvas);
                                }
                                //if you touch box 3 first
                                if (pressedX > box3Bounds[0] && pressedX < box3Bounds[2] && pressedY > box3Bounds[3] && pressedY < box3Bounds[1]) {
                                    boxNum = 3;
                                    paint.setColor(Color.BLACK);
                                    canvas.drawRect((float) 0.05 * canvasWidth, (float) .475 * canvasHeight, (float) .45 * canvasWidth, (float) .225 * canvasHeight, paint);
                                    firstTime = false;
                                    //change(canvas);
                                }
                                //if you touch box 4 first
                                if (pressedX > box4Bounds[0] && pressedX < box4Bounds[2] && pressedY > box4Bounds[3] && pressedY < box4Bounds[1]) {
                                    boxNum = 4;
                                    paint.setColor(Color.BLACK);
                                    canvas.drawRect((float) .55 * canvasWidth, (float).475*canvasHeight, (float).95*canvasWidth, (float).225*canvasHeight, paint);
                                    firstTime = false;
                                    //change(canvas);
                                }
                            }
                        }
                        if (isTouch[1]) {
                            if (isTouch_last[1]) {

                            }
                        }
                    }
                    //for firstTime else
                    else {
                        if (isTouch[0]) {
                            if (isTouch_last[0]) {
                                //if you touch box 1
                                if (pressedX > box1Bounds[0] && pressedX < box1Bounds[2] && pressedY > box1Bounds[3] && pressedY < box1Bounds[1]) {
                                    //if(boxNum!=1) gameOver(canvas);
                                    //else{
                                    //TODO add function
                                    change(canvas);
                                    //}
                                }
                                //if you touch box 2
                                if (pressedX > box2Bounds[0] && pressedX < box2Bounds[2] && pressedY > box2Bounds[3] && pressedY < box2Bounds[1]) {
                                    //if(boxNum!=2) gameOver(canvas);
                                    //else{
                                    //TODO add function
                                    //change(canvas);
                                    //}
                                }
                                //if you touch box 3
                                if (pressedX > box3Bounds[0] && pressedX < box3Bounds[2] && pressedY > box3Bounds[3] && pressedY < box3Bounds[1]) {
                                    //if(boxNum!=3) gameOver(canvas);
                                    //else{
                                    //TODO add function
                                    //change(canvas);
                                    //}
                                }
                                //if you touch box 4
                                if (pressedX > box4Bounds[0] && pressedX < box4Bounds[2] && pressedY > box4Bounds[3] && pressedY < box4Bounds[1]) {
                                    //if(boxNum!=4) gameOver(canvas);
                                    //else{
                                    //TODO add function
                                    //change(canvas);
                                    //}
                                }
                            }
                        }
                    }

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

            }
        }
        catch (Exception e){
            //TODO handle exception
            e.printStackTrace();
        }
    }*/

    private final float SCROLL_THRESHOLD = 200;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isTouch[0] = true;
                isTouch_last[0] = true;
                pressedX = ev.getX();
                pressedY = ev.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isTouch[0]) {
                    //Log.i(LOG_TAG, "onClick ");
                    //TODO onClick code
                    invalidate();
                }
                isTouch[0] = false;
                isTouch[0] = false;
                pressedX = 0;
                pressedY = 0;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouch[0] && (Math.abs(lastPressedX - ev.getX()) > SCROLL_THRESHOLD || Math.abs(lastPressedY - ev.getY()) > SCROLL_THRESHOLD)) {
                    //Log.i(LOG_TAG, "movement detected");
                    isTouch[0] = false;
                    isTouch_last[0] = false;
                }
                invalidate();
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }


    public void gameOver(Canvas canvas) {
        thread.setRunning(false);
        canvas.drawText("You suck so you lost.", (float) .45 * canvas.getWidth(), (float) .75 * canvas.getHeight(), paint);
        //TODO make a retry method that resets everything and makes 'firstTime = true' again
    }

    public void draw(Canvas canvas) {
        initApp(canvas);
    }


    public void update(Canvas canvas) {
        //TODO auto code stub
        if (surfaceHolder.getSurface().isValid()) {
            //canvas = surfaceHolder.lockCanvas();
            //todo delete this
            float canvasWidth = canvas.getWidth();
            float canvasHeight = canvas.getHeight();

            if(firstTime) {
                //drawing the app
                //initApp(canvas);
                //onDraw(canvas);

                if (isTouch[0]) {
                    if (isTouch_last[0]) {
                        //if you touch box 1 first
                        if (pressedX > box1Bounds[0] && pressedX < box1Bounds[2] && pressedY > box1Bounds[3] && pressedY < box1Bounds[1]) {
                            boxNum = 1;
                            //paint.setColor(Color.BLACK);
                            //canvas.drawRect((float) 0.05 * canvasWidth, (float) 0.775 * canvasHeight, (float) 0.45 * canvasWidth, (float) 0.525 * canvasHeight, paint);
                            firstTime = false;
                            change(canvas);
                        }
                        //if you touch box 2 first
                        if (pressedX > box2Bounds[0] && pressedX < box2Bounds[2] && pressedY > box2Bounds[3] && pressedY < box2Bounds[1]) {
                            boxNum = 2;
                            //paint.setColor(Color.BLACK);
                            //canvas.drawRect((float) .55 * canvasWidth, (float) .775 * canvasHeight, (float) .95 * canvasWidth, (float) .525 * canvasHeight, paint);
                            firstTime = false;
                            change(canvas);
                        }
                        //if you touch box 3 first
                        if (pressedX > box3Bounds[0] && pressedX < box3Bounds[2] && pressedY > box3Bounds[3] && pressedY < box3Bounds[1]) {
                            boxNum = 3;
                            //paint.setColor(Color.BLACK);
                            //canvas.drawRect((float) 0.05 * canvasWidth, (float) .475 * canvasHeight, (float) .45 * canvasWidth, (float) .225 * canvasHeight, paint);
                            firstTime = false;
                            change(canvas);
                        }
                        //if you touch box 4 first
                        if (pressedX > box4Bounds[0] && pressedX < box4Bounds[2] && pressedY > box4Bounds[3] && pressedY < box4Bounds[1]) {
                            boxNum = 4;
                            //paint.setColor(Color.BLACK);
                            //canvas.drawRect((float) .55 * canvasWidth, (float).475*canvasHeight, (float).95*canvasWidth, (float).225*canvasHeight, paint);
                            firstTime = false;
                            change(canvas);
                        }
                    }
                }
                if (isTouch[1]) {
                    if (isTouch_last[1]) {

                    }
                }
            }
            //for firstTime else
            else {
                if (isTouch[0]) {
                    if (isTouch_last[0]) {
                        //if you touch box 1
                        if (pressedX > box1Bounds[0] && pressedX < box1Bounds[2] && pressedY > box1Bounds[3] && pressedY < box1Bounds[1]) {
                            //if(boxNum!=1) gameOver(canvas);
                            //else{
                            //TODO add function
                            change(canvas);
                            //}
                        }
                        //if you touch box 2
                        if (pressedX > box2Bounds[0] && pressedX < box2Bounds[2] && pressedY > box2Bounds[3] && pressedY < box2Bounds[1]) {
                            //if(boxNum!=2) gameOver(canvas);
                            //else{
                            //TODO add function
                            change(canvas);
                            //}
                        }
                        //if you touch box 3
                        if (pressedX > box3Bounds[0] && pressedX < box3Bounds[2] && pressedY > box3Bounds[3] && pressedY < box3Bounds[1]) {
                            //if(boxNum!=3) gameOver(canvas);
                            //else{
                            //TODO add function
                            change(canvas);
                            //}
                        }
                        //if you touch box 4
                        if (pressedX > box4Bounds[0] && pressedX < box4Bounds[2] && pressedY > box4Bounds[3] && pressedY < box4Bounds[1]) {
                            //if(boxNum!=4) gameOver(canvas);
                            //else{
                            //TODO add function
                            change(canvas);
                            //}
                        }
                    }
                }
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }




    //WARNING YOU ARE ENTERING NO MANS LAND



    //WARNING YOU ARE ENTERING NO MANS LAND





    //WARNING YOU ARE ENTERING NO MANS LAND






    //WARNING YOU ARE ENTERING NO MANS LAND





    //WARNING YOU ARE ENTERING NO MANS LAND





    //WARNING YOU ARE ENTERING NO MANS LAND





    //WARNING YOU ARE ENTERING NO MANS LAND




    //WARNING YOU ARE ENTERING NO MANS LAND






    //WARNING YOU ARE ENTERING NO MANS LAND





    //WARNING YOU ARE ENTERING NO MANS LAND





    //WARNING YOU ARE ENTERING NO MANS LAND






    //WARNING YOU ARE ENTERING NO MANS LAND














//cruddy run, should prob delete
    /*@Override
    public void run() {
        // TODO Auto-generated method stub
        while(running){
            if(surfaceHolder.getSurface().isValid()){
                Canvas canvas = surfaceHolder.lockCanvas();
                //... actual drawing on canvas

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(30);

                if(isTouch[0]){
                    if(isTouch_last[0]){
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(30);
                        paint.setColor(Color.RED);
                        canvas.drawLine(x_last[0], y_last[0], x[0], y[0], paint);
                    }
                }
                if(isTouch[1]){
                    if(isTouch_last[1]){
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(30);
                        paint.setColor(Color.BLUE);
                        canvas.drawLine(x_last[1], y_last[1], x[1], y[1], paint);
                    }
                }

                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }*/

    /*@Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK)
                >> MotionEvent.ACTION_POINTER_ID_SHIFT);
        int pointerId = motionEvent.getPointerId(pointerIndex);
        int action = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
        int pointCnt = motionEvent.getPointerCount();

        if (pointCnt <= MAX_POINT_CNT){
            if (pointerIndex <= MAX_POINT_CNT - 1){

                for (int i = 0; i < pointCnt; i++) {
                    int id = motionEvent.getPointerId(i);
                    x_last[id] = x[id];
                    y_last[id] = y[id];
                    isTouch_last[id] = isTouch[id];
                    x[id] = motionEvent.getX(i);
                    y[id] = motionEvent.getY(i);
                }

                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        isTouch[pointerId] = true;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        isTouch[pointerId] = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isTouch[pointerId] = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isTouch[pointerId] = false;
                        isTouch_last[pointerId] = false;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        isTouch[pointerId] = false;
                        isTouch_last[pointerId] = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isTouch[pointerId] = false;
                        isTouch_last[pointerId] = false;
                        break;
                    default:
                        isTouch[pointerId] = false;
                        isTouch_last[pointerId] = false;
                }
            }
        }
        invalidate();

        return true;
    }*/



//Jonik
    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        pressedX = event.getX();
        pressedY = event.getY();
        //float eventX = event.getX();
        //float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //path.moveTo(x[0], y[0]);
                pressStartTime = System.currentTimeMillis();
                isTouch[0] = true;
                pressedX = event.getX();
                pressedY = event.getY();

                lastPressedX = pressedX;
                lastPressedY = pressedY;
                return true;
            case MotionEvent.ACTION_MOVE:
                //path.lineTo(x[0], y[0]);
                long pressDuration = System.currentTimeMillis() - pressStartTime;
                final float x = event.getX();
                final float y = event.getY();
                if (pressDuration < MAX_CLICK_DURATION && distance(x, y, lastPressedX, lastPressedY) < MAX_CLICK_DISTANCE) {
                    // Click event has occurred
                    isTouch[0] = true;
                }
                lastPressedY = event.getY();
                lastPressedX = event.getX();

                break;
            case MotionEvent.ACTION_UP:
                // nothing to do
                isTouch[0] = false;
                break;
            default:
                return false;
        }

        // Schedules a repaint.
        invalidate();
        return true;
    }


    //helper methods
    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private float pxToDp(float px) {
        return px / getResources().getDisplayMetrics().density;
    }*/

}