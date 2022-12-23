package com.fourshape.a4mcqplus.popups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.Image;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.bumptech.glide.Glide;
import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.material.card.MaterialCardView;

public class ImageEnlargerPopupWindow {

    private float mScaleFactor = 1.0f;
    ImageView imageView;

    private static final String TAG = "Touch";
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    public ImageEnlargerPopupWindow (Context context, String imageUrl) {

        View view = LayoutInflater.from(context).inflate(R.layout.popup_image_enlarger, null);

        int width = MaterialCardView.LayoutParams.WRAP_CONTENT;
        int height = MaterialCardView.LayoutParams.WRAP_CONTENT;
        boolean focusable = false;

        final PopupWindow popupWindow = new PopupWindow(view, width, height, focusable);

        ImageView closeImageView;
        imageView = view.findViewById(R.id.image);
        closeImageView = view.findViewById(R.id.close_popup);
        imageView.setClickable(true);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                handleTouchEvents(view, motionEvent);
                return true;
            }
        });

        Glide.with(context).load(imageUrl).into(imageView);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null)
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
            }
        });

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    private void handleTouchEvents (View v, MotionEvent event) {

        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                matrix.set(view.getImageMatrix());
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                MakeLog.info(TAG, "mode=DRAG");
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                MakeLog.info(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                MakeLog.info(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    MakeLog.info(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                } else if (mode == ZOOM){
                    float newDist = spacing(event);
                    MakeLog.info(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;

        }

        view.setImageMatrix(matrix);

    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }

    private void dumpEvent(MotionEvent event)
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        MakeLog.info("Touch Events ---------", sb.toString());
    }

}
