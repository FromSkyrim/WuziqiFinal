package com.fatty.wuziqifinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 17255 on 2016/5/3.
 */
public class WuziqiPanel extends View {

    private int mPanelWidth;
    private float mlineHeight;
    private int MAX_LINE = 10;

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float pieceScaleOfLineHeight = (3 * 1.0f) / 4;

    private boolean mIsWhite = true;//白棋下
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    private boolean gameOver;

    private int MAX_PIECE_IN_LINE = 5;

    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPanelWidth = w;
        mlineHeight = (mPanelWidth * 1.0f) / MAX_LINE;

        int pieceWidth = (int) (mlineHeight * pieceScaleOfLineHeight);

        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (gameOver) {
            return false;
        }

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point p = getValidPoint(x, y);

            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }

            if (mIsWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite = !mIsWhite;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mlineHeight),(int) (y / mlineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);

        drawPieces(canvas);

        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        if (whiteWin || blackWin) {
            gameOver = true;

            if (whiteWin) {
                Toast.makeText(getContext(), "白棋赢了", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "黑棋赢了", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            if (checkHorizontalFiveInLine(x, y, points)) {
                return true;
            } else if (checkTipRightFiveInLine(x, y, points)) {
                return true;
            } else if (checkVerticalFiveInLine(x, y, points)) {
                return true;
            } else if (checkVerticalFiveInLine(x, y, points)) {
                return true;
            }
        }
        return false;
    }

//  判断横向是否五子一线
    private boolean checkHorizontalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        //往左数
        for (int i = 1; i < MAX_PIECE_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_IN_LINE) {
            return true;
        }

        //往右数
        for (int i = 1; i < MAX_PIECE_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_IN_LINE) {
            return true;
        }
        return false;
    }

    //  判断纵向是否五子一线
    private boolean checkVerticalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        //往下数
        for (int i = 1; i < MAX_PIECE_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_IN_LINE) {
            return true;
        }

        //往上数
        for (int i = 1; i < MAX_PIECE_IN_LINE; i++) {
            if (points.contains(new Point(x , y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_IN_LINE) {
            return true;
        }
        return false;
    }

    //  判断正斜向是否五子一线
    private boolean checkTipRightFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        //往右上数
        for (int i = 1; i < MAX_PIECE_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_IN_LINE) {
            return true;
        }

        //往左下数
        for (int i = 1; i < MAX_PIECE_IN_LINE; i++) {
            if (points.contains(new Point(x - i , y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_IN_LINE) {
            return true;
        }
        return false;
    }

    //  判断反斜向是否五子一线
    private boolean checkTipLeftFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        //往左上数
        for (int i = 1; i < MAX_PIECE_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count ++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_IN_LINE) {
            return true;
        }

        //往右下数
        for (int i = 1; i < MAX_PIECE_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_IN_LINE) {
            return true;
        }
        return false;
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - pieceScaleOfLineHeight) / 2) * mlineHeight,
                    (whitePoint.y + (1 - pieceScaleOfLineHeight) / 2) * mlineHeight, null);
        }

        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - pieceScaleOfLineHeight) / 2) * mlineHeight,
                    (blackPoint.y + (1 - pieceScaleOfLineHeight) / 2) * mlineHeight, null);
        }
    }

    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mlineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight/2);
            int stopX = (int) (w-(lineHeight/2));
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, stopX, y, mPaint);
        }

        for (int i = 0; i < MAX_LINE; i++) {
            int startY = (int) (lineHeight/2);
            int stopY = (int) (w-(lineHeight/2));
            int x = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(x, startY, x, stopY, mPaint);
        }
    }

    public void reStart() {
        mWhiteArray.clear();
        mBlackArray.clear();
        gameOver = false;
        invalidate();
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, gameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            gameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));

            return;
        }
        super.onRestoreInstanceState(state);
    }
}
