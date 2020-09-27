package com.rhino.viewpager.transformers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;


public class BookPageWidget extends View {
    private final static String TAG = "BookPageWidget";

    /**
     * view的宽度
     **/
    private int mViewWidth;
    /**
     * view的高度
     **/
    private int mViewHeight;

    private int mCornerX = 1; // 拖拽点对应的页脚
    private int mCornerY = 1;
    private Path mPath0;
    private Path mPath1;
    // 适配 android 高版本无法使用 XOR 的问题
    private Path mXORPath;

    private Bitmap mCurPageBitmap = null; // 当前页
    private Bitmap mNextPageBitmap = null;

    private PointF mTouch = new PointF(); // 拖拽点
    private PointF mBezierStart1 = new PointF(); // 贝塞尔曲线起始点
    private PointF mBezierControl1 = new PointF(); // 贝塞尔曲线控制点
    private PointF mBeziervertex1 = new PointF(); // 贝塞尔曲线顶点
    private PointF mBezierEnd1 = new PointF(); // 贝塞尔曲线结束点

    private PointF mBezierStart2 = new PointF(); // 另一条贝塞尔曲线
    private PointF mBezierControl2 = new PointF();
    private PointF mBeziervertex2 = new PointF();
    private PointF mBezierEnd2 = new PointF();

    float mMiddleX;
    float mMiddleY;
    float mDegrees;
    float mTouchToCornerDis;
    private ColorMatrixColorFilter mColorMatrixFilter;
    private Matrix mMatrix;
    float[] mMatrixArray = {0, 0, 0, 0, 0, 0, 0, 0, 1.0f};

    boolean mIsRTandLB; // 是否属于右上左下
    private float mMaxLength;
    private GradientDrawable mBackShadowDrawableLR; // 有阴影的GradientDrawable
    private GradientDrawable mBackShadowDrawableRL;
    private GradientDrawable mFolderShadowDrawableLR;
    private GradientDrawable mFolderShadowDrawableRL;

    private GradientDrawable mFrontShadowDrawableHBT;
    private GradientDrawable mFrontShadowDrawableHTB;
    private GradientDrawable mFrontShadowDrawableVLR;
    private GradientDrawable mFrontShadowDrawableVRL;

    private Paint mPaint;
    private Scroller mScroller;

    private TouchListener mTouchListener;
    // 初始化参数
    private int mBgColor = 0xFFCEC29C;

    public BookPageWidget(Context context) {
        this(context, null);
    }

    public BookPageWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookPageWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPath0 = new Path();
        mPath1 = new Path();
        mXORPath = new Path();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setAlpha(150);

        createDrawable();

        ColorMatrix cm = new ColorMatrix();//设置颜色数组
        float array[] = {1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 1, 0};
        cm.set(array);
        mColorMatrixFilter = new ColorMatrixColorFilter(cm);
        mMatrix = new Matrix();
        mScroller = new Scroller(getContext(), new LinearInterpolator());

        mTouch.x = 0.01f; // 不让x,y为0,否则在点计算时会有问题
        mTouch.y = 0.01f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getHeight();
        }
        setMeasuredDimension(width, height);
        initView(width, height);
    }

    /**
     * 初始化视图
     **/
    public void initView(int width, int height) {
        if (0 >= width || 0 >= height) {
            return;
        }
        mViewWidth = width;
        mViewHeight = height;
        mMaxLength = (float) Math.hypot(mViewWidth, mViewHeight);
        if (mCurPageBitmap == null || mNextPageBitmap == null) {
            mCurPageBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);      //android:LargeHeap=true  use in  manifest application
            mNextPageBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
        }
    }

    private void loadBitmapFromView(Bitmap bitmap, View view) {
        if (bitmap == null || view == null) {
            Log.d("xxx", "bitmap = " + bitmap + ", view = " + view);
            return;
        }
        int w = view.getWidth();
        int h = view.getHeight();
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, w, h);
        view.draw(canvas);
    }

    public void loadCurPageView(View view) {
        loadBitmapFromView(mCurPageBitmap, view);
    }

    public void loadNextPageView(View view) {
        loadBitmapFromView(mNextPageBitmap, view);
    }

    public Bitmap getCurPage() {
        return mCurPageBitmap;
    }

    public Bitmap getNextPage() {
        return mNextPageBitmap;
    }

    public void changePage() {
        Bitmap bitmap = mCurPageBitmap;
        mCurPageBitmap = mNextPageBitmap;
        mNextPageBitmap = bitmap;
        postInvalidate();
    }

    /**
     * 创建阴影的GradientDrawable
     */
    private void createDrawable() {
        int[] color = {0x333333, 0xb0333333};
        mFolderShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, color);
        mFolderShadowDrawableRL
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFolderShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, color);
        mFolderShadowDrawableLR
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        // 背面颜色组
        int[] mBackShadowColors = new int[]{0xff111111, 0x111111};
        mBackShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
        mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        // 前面颜色组
        int[] mFrontShadowColors = new int[]{0x80111111, 0x111111};
        mFrontShadowDrawableVLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
        mFrontShadowDrawableVLR
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mFrontShadowDrawableVRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
        mFrontShadowDrawableVRL
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHTB = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
        mFrontShadowDrawableHTB
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHBT = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
        mFrontShadowDrawableHBT
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }
    public void setBgColor(int color) {
        mBgColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBgColor);
        Log.d("xxx", "isRuning = " + isRuning + ", isNext = " + isNext);
        if (isRuning) {
            drawMove(canvas);
        } else {
            if (cancelPage){
                mNextPageBitmap = mCurPageBitmap.copy(Bitmap.Config.RGB_565, true);
            }
            drawStatic(canvas);
        }
    }

    private void drawStatic(Canvas canvas){
        if (cancelPage) {
            mNextPageBitmap = mCurPageBitmap.copy(Bitmap.Config.RGB_565, true);
            canvas.drawBitmap(mCurPageBitmap, 0, 0, null);
        } else {
            canvas.drawBitmap(mNextPageBitmap, 0, 0, null);
        }
    }

    private void drawMove(Canvas canvas){
        if (isNext) {
            calcPoints();
            drawCurrentPageArea(canvas, mCurPageBitmap, mPath0);
            drawNextPageAreaAndShadow(canvas, mNextPageBitmap);
            drawCurrentPageShadow(canvas);
            drawCurrentBackArea(canvas, mCurPageBitmap);
        } else {
            calcPoints();
            drawCurrentPageArea(canvas, mNextPageBitmap, mPath0);
            drawNextPageAreaAndShadow(canvas, mCurPageBitmap);
            drawCurrentPageShadow(canvas);
            drawCurrentBackArea(canvas, mNextPageBitmap);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            mTouch.x = x;
            mTouch.y = y;
            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y) {
                isRuning = false;
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    //是否移动了
    private Boolean isMove = false;
    //是否翻到下一页
    private Boolean isNext = false;
    //是否取消翻页
    private Boolean cancelPage = false;
    //是否没下一页或者上一页
    private Boolean noNext = false;
    private int downX = 0;
    private int downY = 0;

    private int moveX = 0;
    private int moveY = 0;
    //翻页动画是否在执行
    private Boolean isRuning = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int x = (int) event.getX();
        int y = (int) event.getY();
        mTouch.x = event.getX();
        mTouch.y = event.getY();
        //触摸y中间位置吧y变成屏幕高度
        if ((downY > mViewHeight / 3 && downY < mViewHeight * 2 / 3) || (isMove && !isNext)) {
            mTouch.y = mViewHeight;
        }

        if (downY > mViewHeight / 3 && downY < mViewHeight / 2 && isNext) {
            mTouch.y = 1;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) event.getX();
            downY = (int) event.getY();
            moveX = 0;
            moveY = 0;
            isMove = false;
//            cancelPage = false;
            noNext = false;
            isNext = false;
            isRuning = false;
            calcCornerXY(downX, downY);
            abortAnimation();
            Log.e(TAG, "ACTION_DOWN");
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            //判断是否移动了
            if (!isMove) {
                isMove = Math.abs(downX - x) > slop || Math.abs(downY - y) > slop;
            }

            if (isMove) {
                isMove = true;
                if (moveX == 0 && moveY == 0) {
                    Log.e(TAG, "isMove");
                    //判断翻得是上一页还是下一页
                    if (x - downX > 0) {
                        isNext = false;
                    } else {
                        isNext = true;
                    }
                    cancelPage = false;
                    if (isNext) {
                        Boolean isNext = mTouchListener.nextPage();
//                        calcCornerXY(downX,mViewHeight);
                        if (mViewWidth / 2 > downX) {
                            calcCornerXY(mViewWidth - downX, downY);
                        }

                        if (!isNext) {
                            noNext = true;
                            return true;
                        }
                    } else {
                        Boolean isPre = mTouchListener.prePage();
                        //上一页滑动不出现对角
                        if (downX > mViewWidth / 2) {
                            calcCornerXY(downX, mViewHeight);
                        } else {
                            calcCornerXY(mViewWidth - downX, mViewHeight);
                        }

                        if (!isPre) {
                            noNext = true;
                            return true;
                        }
                    }
                    Log.e(TAG, "isNext:" + isNext);
                } else {
                    //判断是否取消翻页
                    if (isNext) {
                        if (x - moveX > 0) {
                            cancelPage = true;
                        } else {
                            cancelPage = false;
                        }
                    } else {
                        if (x - moveX < 0) {
                            cancelPage = true;
                        } else {
                            cancelPage = false;
                        }
                    }
                    Log.e(TAG, "cancelPage:" + cancelPage);
                }

                moveX = x;
                moveY = y;
                isRuning = true;
                this.postInvalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.e(TAG, "ACTION_UP");
            if (!isMove) {
                cancelPage = false;
                //是否点击了中间
                if (downX > mViewWidth / 5 && downX < mViewWidth * 4 / 5 && downY > mViewHeight / 3 && downY < mViewHeight * 2 / 3) {
                    if (mTouchListener != null) {
                        mTouchListener.center();
                    }
                    Log.e(TAG, "center");
//                    mCornerX = 1; // 拖拽点对应的页脚
//                    mCornerY = 1;
//                    mTouch.x = 0.1f;
//                    mTouch.y = 0.1f;
                    return true;
                } else if (x < mViewWidth / 2) {
                    isNext = false;
                } else {
                    isNext = true;
                }

                if (isNext) {
                    Boolean isNext = mTouchListener.nextPage();
                    if (!isNext) {
                        return true;
                    }
                } else {
                    Boolean isPre = mTouchListener.prePage();
                    calcCornerXY(mViewWidth - downX, mViewHeight);
                    mTouch.y = mViewHeight;
                    if (!isPre) {
                        return true;
                    }
                }
            }

            if (cancelPage && mTouchListener != null) {
                mTouchListener.cancel();
            }

            Log.e(TAG, "isNext:" + isNext);
            if (!noNext) {
                isRuning = true;
                startAnimation(400);
                this.postInvalidate();
            }
        }

        return true;
    }

    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    public interface TouchListener {
        void center();

        Boolean prePage();

        Boolean nextPage();

        void cancel();
    }

    private void startAnimation(int delayMillis) {
        int dx, dy;
        // dx 水平方向滑动的距离，负值会使滚动向左滚动
        // dy 垂直方向滑动的距离，负值会使滚动向上滚动
        if (cancelPage) {
            if (mCornerX > 0 && isNext) {
                dx = (int) (mViewWidth - mTouch.x);
            } else {
                dx = -(int) mTouch.x;
            }

            if (!isNext) {
                dx = (int) -(mViewWidth + mTouch.x);
            }

            if (mCornerY > 0) {
                dy = (int) (mViewHeight - mTouch.y);
            } else {
                dy = -(int) mTouch.y; // 防止mTouch.y最终变为0
            }
        } else {
            if (mCornerX > 0 && isNext) {
                dx = -(int) (mViewWidth + mTouch.x);
            } else {
                dx = (int) (mViewWidth - mTouch.x + mViewWidth);
            }
            if (mCornerY > 0) {
                dy = (int) (mViewHeight - mTouch.y);
            } else {
                dy = (int) (1 - mTouch.y); // 防止mTouch.y最终变为0
            }
        }
        mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
                delayMillis);
    }

    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            mTouch.x = mScroller.getFinalX();
            mTouch.y = mScroller.getFinalY();
            postInvalidate();
        }
    }

    public boolean isFinishAnim() {
        return mScroller.isFinished();
    }

    public boolean isRunning() {
        return isRuning;
    }

    /**
     * 是否能够拖动过去
     *
     * @return
     */
    public boolean canDragOver() {
        if (mTouchToCornerDis > mViewWidth / 10)
            return true;
        return false;
    }

    public boolean right() {
        return mCornerX <= -4;
    }

    /**
     * 绘制翻起页背面
     *
     * @param canvas
     * @param bitmap
     */
    private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
        int i = (int) (mBezierStart1.x + mBezierControl1.x) / 2;
        float f1 = Math.abs(i - mBezierControl1.x);
        int i1 = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
        float f2 = Math.abs(i1 - mBezierControl2.y);
        float f3 = Math.min(f1, f2);
        mPath1.reset();
        mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath1.close();
        GradientDrawable mFolderShadowDrawable;
        int left;
        int right;
        if (mIsRTandLB) {
            left = (int) (mBezierStart1.x - 1);
            right = (int) (mBezierStart1.x + f3 + 1);
            mFolderShadowDrawable = mFolderShadowDrawableLR;
        } else {
            left = (int) (mBezierStart1.x - f3 - 1);
            right = (int) (mBezierStart1.x + 1);
            mFolderShadowDrawable = mFolderShadowDrawableRL;
        }
        canvas.save();
        try {
            canvas.clipPath(mPath0);
            canvas.clipPath(mPath1, Region.Op.INTERSECT);
        } catch (Exception e) {
        }

        mPaint.setColorFilter(mColorMatrixFilter);
        //对Bitmap进行取色
        int color = bitmap.getPixel(1, 1);
        //获取对应的三色
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        //转换成含有透明度的颜色
        int tempColor = Color.argb(200, red, green, blue);


        float dis = (float) Math.hypot(mCornerX - mBezierControl1.x,
                mBezierControl2.y - mCornerY);
        float f8 = (mCornerX - mBezierControl1.x) / dis;
        float f9 = (mBezierControl2.y - mCornerY) / dis;
        mMatrixArray[0] = 1 - 2 * f9 * f9;
        mMatrixArray[1] = 2 * f8 * f9;
        mMatrixArray[3] = mMatrixArray[1];
        mMatrixArray[4] = 1 - 2 * f8 * f8;
        mMatrix.reset();
        mMatrix.setValues(mMatrixArray);
        mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
        mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        //背景叠加
        canvas.drawColor(tempColor);

        mPaint.setColorFilter(null);

        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right,
                (int) (mBezierStart1.y + mMaxLength));
        mFolderShadowDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制翻起页的阴影
     *
     * @param canvas
     */
    public void drawCurrentPageShadow(Canvas canvas) {
        double degree;
        if (mIsRTandLB) {
            degree = Math.PI
                    / 4
                    - Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x
                    - mBezierControl1.x);
        } else {
            degree = Math.PI
                    / 4
                    - Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x
                    - mBezierControl1.x);
        }
        // 翻起页阴影顶点与touch点的距离
        double d1 = (float) 25 * 1.414 * Math.cos(degree);
        double d2 = (float) 25 * 1.414 * Math.sin(degree);
        float x = (float) (mTouch.x + d1);
        float y;
        if (mIsRTandLB) {
            y = (float) (mTouch.y + d2);
        } else {
            y = (float) (mTouch.y - d2);
        }
        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.close();
        float rotateDegrees;
        canvas.save();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mXORPath.reset();
                mXORPath.moveTo(0f, 0f);
                mXORPath.lineTo(canvas.getWidth(), 0f);
                mXORPath.lineTo(canvas.getWidth(), canvas.getHeight());
                mXORPath.lineTo(0f, canvas.getHeight());
                mXORPath.close();

                // 取 path 的补集，作为 canvas 的交集
                mXORPath.op(mPath0, Path.Op.XOR);
                canvas.clipPath(mXORPath);
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR);
            }
            canvas.clipPath(mPath1, Region.Op.INTERSECT);
        } catch (Exception e) {
            // TODO: handle exception
        }

        int leftx;
        int rightx;
        GradientDrawable mCurrentPageShadow;
        if (mIsRTandLB) {
            leftx = (int) (mBezierControl1.x);
            rightx = (int) mBezierControl1.x + 25;
            mCurrentPageShadow = mFrontShadowDrawableVLR;
        } else {
            leftx = (int) (mBezierControl1.x - 25);
            rightx = (int) mBezierControl1.x + 1;
            mCurrentPageShadow = mFrontShadowDrawableVRL;
        }

        rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
                - mBezierControl1.x, mBezierControl1.y - mTouch.y));
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
        mCurrentPageShadow.setBounds(leftx,
                (int) (mBezierControl1.y - mMaxLength), rightx,
                (int) (mBezierControl1.y));
        mCurrentPageShadow.draw(canvas);
        canvas.restore();

        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.close();
        canvas.save();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mXORPath.reset();
                mXORPath.moveTo(0f, 0f);
                mXORPath.lineTo(canvas.getWidth(), 0f);
                mXORPath.lineTo(canvas.getWidth(), canvas.getHeight());
                mXORPath.lineTo(0f, canvas.getHeight());
                mXORPath.close();

                // 取 path 的补给，作为 canvas 的交集
                mXORPath.op(mPath0, Path.Op.XOR);
                canvas.clipPath(mXORPath);
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR);
            }
            canvas.clipPath(mPath1, Region.Op.INTERSECT);
        } catch (Exception e) {
        }

        if (mIsRTandLB) {
            leftx = (int) (mBezierControl2.y);
            rightx = (int) (mBezierControl2.y + 25);
            mCurrentPageShadow = mFrontShadowDrawableHTB;
        } else {
            leftx = (int) (mBezierControl2.y - 25);
            rightx = (int) (mBezierControl2.y + 1);
            mCurrentPageShadow = mFrontShadowDrawableHBT;
        }
        rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y
                - mTouch.y, mBezierControl2.x - mTouch.x));
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
        float temp;
        if (mBezierControl2.y < 0)
            temp = mBezierControl2.y - mViewHeight;
        else
            temp = mBezierControl2.y;

        int hmg = (int) Math.hypot(mBezierControl2.x, temp);
        if (hmg > mMaxLength)
            mCurrentPageShadow
                    .setBounds((int) (mBezierControl2.x - 25) - hmg, leftx,
                            (int) (mBezierControl2.x + mMaxLength) - hmg,
                            rightx);
        else
            mCurrentPageShadow.setBounds(
                    (int) (mBezierControl2.x - mMaxLength), leftx,
                    (int) (mBezierControl2.x), rightx);

        mCurrentPageShadow.draw(canvas);
        canvas.restore();
    }

    private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
        mPath1.reset();
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.lineTo(mCornerX, mCornerY);
        mPath1.close();

        mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
                - mCornerX, mBezierControl2.y - mCornerY));
        int leftx;
        int rightx;
        GradientDrawable mBackShadowDrawable;
        if (mIsRTandLB) {  //左下及右上
            leftx = (int) (mBezierStart1.x);
            rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
            mBackShadowDrawable = mBackShadowDrawableLR;
        } else {
            leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
            rightx = (int) mBezierStart1.x;
            mBackShadowDrawable = mBackShadowDrawableRL;
        }
        canvas.save();
        try {
            canvas.clipPath(mPath0);
            canvas.clipPath(mPath1, Region.Op.INTERSECT);
        } catch (Exception e) {
        }


        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
                (int) (mMaxLength + mBezierStart1.y));//左上及右下角的xy坐标值,构成一个矩形
        mBackShadowDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
        mPath0.reset();
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
                mBezierEnd1.y);
        mPath0.lineTo(mTouch.x, mTouch.y);
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
                mBezierStart2.y);
        mPath0.lineTo(mCornerX, mCornerY);
        mPath0.close();

        canvas.save();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mXORPath.reset();
            mXORPath.moveTo(0f, 0f);
            mXORPath.lineTo(canvas.getWidth(), 0f);
            mXORPath.lineTo(canvas.getWidth(), canvas.getHeight());
            mXORPath.lineTo(0f, canvas.getHeight());
            mXORPath.close();

            // 取 path 的补给，作为 canvas 的交集
            mXORPath.op(path, Path.Op.XOR);
            canvas.clipPath(mXORPath);
        } else {
            canvas.clipPath(path, Region.Op.XOR);
        }
        canvas.drawBitmap(bitmap, 0, 0, null);
        try {
            canvas.restore();
        } catch (Exception e) {

        }
    }

    /**
     * 计算拖拽点对应的拖拽脚
     *
     * @param x
     * @param y
     */
    public void calcCornerXY(float x, float y) {
        //  Log.i("hck", "PageWidget x:" + x + "      y" + y);
        if (x <= mViewWidth / 2) {
            mCornerX = 0;
        } else {
            mCornerX = mViewWidth;
        }
        if (y <= mViewHeight / 2) {
            mCornerY = 0;
        } else {
            mCornerY = mViewHeight;
        }

        if ((mCornerX == 0 && mCornerY == mViewHeight)
                || (mCornerX == mViewWidth && mCornerY == 0)) {
            mIsRTandLB = true;
        } else {
            mIsRTandLB = false;
        }

    }

    private void calcPoints() {
        mMiddleX = (mTouch.x + mCornerX) / 2;
        mMiddleY = (mTouch.y + mCornerY) / 2;
        mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
        mBezierControl1.y = mCornerY;
        mBezierControl2.x = mCornerX;

        float f4 = mCornerY - mMiddleY;
        if (f4 == 0) {
            mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                    * (mCornerX - mMiddleX) / 0.1f;
        } else {
            mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                    * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);
        }
        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
                / 2;
        mBezierStart1.y = mCornerY;

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (mTouch.x > 0 && mTouch.x < mViewWidth) {
            if (mBezierStart1.x < 0 || mBezierStart1.x > mViewWidth) {
                if (mBezierStart1.x < 0)
                    mBezierStart1.x = mViewWidth - mBezierStart1.x;

                float f1 = Math.abs(mCornerX - mTouch.x);
                float f2 = mViewWidth * f1 / mBezierStart1.x;
                mTouch.x = Math.abs(mCornerX - f2);

                float f3 = Math.abs(mCornerX - mTouch.x)
                        * Math.abs(mCornerY - mTouch.y) / f1;
                mTouch.y = Math.abs(mCornerY - f3);

                mMiddleX = (mTouch.x + mCornerX) / 2;
                mMiddleY = (mTouch.y + mCornerY) / 2;

                mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                        * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
                mBezierControl1.y = mCornerY;

                mBezierControl2.x = mCornerX;

                float f5 = mCornerY - mMiddleY;
                if (f5 == 0) {
                    mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                            * (mCornerX - mMiddleX) / 0.1f;
                } else {
                    mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                            * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);
                }

                mBezierStart1.x = mBezierControl1.x
                        - (mCornerX - mBezierControl1.x) / 2;
            }
        }
        mBezierStart2.x = mCornerX;
        mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
                / 2;

        mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
                (mTouch.y - mCornerY));

        mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
                mBezierStart2);
        mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
                mBezierStart2);

        mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
        mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
        mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
        mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
    }

    /**
     * 求解直线P1P2和直线P3P4的交点坐标
     *
     * @param P1
     * @param P2
     * @param P3
     * @param P4
     * @return
     */
    public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
        PointF CrossP = new PointF();
        // 二元函数通式： y=ax+b
        float a1 = (P2.y - P1.y) / (P2.x - P1.x);
        float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

        float a2 = (P4.y - P3.y) / (P4.x - P3.x);
        float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
        CrossP.x = (b2 - b1) / (a1 - a2);
        CrossP.y = a1 * CrossP.x + b1;
        return CrossP;
    }


}
