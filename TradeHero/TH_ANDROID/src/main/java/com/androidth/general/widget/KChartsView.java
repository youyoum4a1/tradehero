package com.androidth.general.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.androidth.general.R;
import com.androidth.general.api.fx.FXCandleDTO;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KChartsView extends KChartBase {

	private static int TOUCH_MODE;
	private final static int NONE = 0;
	private final static int DOWN = 1;
	private final static int MOVE = 2;
	private final static int ZOOM = 3;

	/** color of y axis title **/
	private static final int DEFAULT_AXIS_Y_TITLE_COLOR = Color.BLACK;

	/** color of x axis title **/
	private static final int DEFAULT_AXIS_X_TITLE_COLOR = Color.BLACK;

    /** candle detail dkGreen color **/
    private static final int DEFAULT_CANDLE_DETAIL_DKGREEN_COLOR = R.color.number_green;

    /** candle detail red color **/
    private static final int DEFAULT_CANDLE_DETAIL_RED_COLOR = Color.RED;

	/** min show number of candle */
	private final static int MIN_CANDLE_NUM = 10;

	/** 默认显示的Candle数 */
	private final static int DEFAULT_CANDLE_NUM = 30;

	/** 最小可识别的移动距离 */
	private final static int MIN_MOVE_DISTANCE = 15;

	/** Candle宽度 */
	private double mCandleWidth;

	/** 触摸点 */
	private float mStartX;
	private float mStartY;

	/** OHLC数据 */
	private List<FXCandleDTO> mOHLCData;

	/** 显示的OHLC数据起始位置 */
	private int mDataStartIndext;

	/** 显示的OHLC数据个数 */
	private int mShowDataNum;

	/** 是否显示蜡烛详情 */
	private boolean showDetails;

	/** 当前数据的最大最小值 */
	private double mMaxPrice;
	private double mMinPrice;

	/** MA数据 */
//    private static final boolean showMALine = false;

	public KChartsView(Context context) {
		super(context);
		init();
	}

	public KChartsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public KChartsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mShowDataNum = DEFAULT_CANDLE_NUM;
		mDataStartIndext = 0;
		showDetails = false;
		mMaxPrice = -1;
		mMinPrice = -1;

		mOHLCData = new ArrayList<>();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mOHLCData == null || mOHLCData.size() <= 0) {
			return;
		}
		drawUpperRegion(canvas);
		drawTitles(canvas);
		drawCandleDetails(canvas);
	}

	private void drawCandleDetails(Canvas canvas) {
		if (showDetails) {
			float width = getCandleWidth();
			float left = (float) DEFAULT_AXIS_TITLE_SIZE + DEFAULT_CANDLE_LEFT_MARGIN;
			float leftText = left + (float)DEFAULT_AXIS_TITLE_SIZE / 4;
			float top = (float) DEFAULT_AXIS_TITLE_SIZE;
			float lineHeight = (float) 7*DEFAULT_AXIS_TITLE_SIZE/6;
			float right = (float)9 * DEFAULT_AXIS_TITLE_SIZE + 4f + DEFAULT_CANDLE_LEFT_MARGIN;
			float bottom = (float)8 * DEFAULT_AXIS_TITLE_SIZE + DEFAULT_AXIS_TITLE_SIZE / 3;
			if (mStartX < width / 2.0f) {
				right = width - (float)DEFAULT_AXIS_TITLE_SIZE + 4f;
				left = width - (float)9 * DEFAULT_AXIS_TITLE_SIZE;
                leftText = left + (float)DEFAULT_AXIS_TITLE_SIZE / 4;
			}
			int selectIndext = (int) ((width - 2.0f - mStartX) / mCandleWidth + mDataStartIndext);
            if (selectIndext < 0 || selectIndext > mOHLCData.size() - 1)
            {
                return ;
            }
            //draw touch detail
			Paint paint = new Paint();
			paint.setColor(Color.LTGRAY);
			canvas.drawRect(left, top, right, bottom, paint);
            paint.setColor(Color.DKGRAY);
            paint.setStrokeWidth(DEFAULT_TWO_LINE_WIDTH);
			canvas.drawLine(left, top, left, bottom, paint);
			canvas.drawLine(left, top, right, top, paint);
			canvas.drawLine(right, bottom, right, top, paint);
			canvas.drawLine(right, bottom, left, bottom, paint);

            // draw touch line
            float startX = (float) (width - mCandleWidth * selectIndext - (mCandleWidth - DEFAULT_ONE_LINE_WIDTH) / 2);
			canvas.drawRect(startX - DEFAULT_ONE_LINE_WIDTH, 2.0f, startX + DEFAULT_ONE_LINE_WIDTH, UPER_CHART_BOTTOM, paint);

			// 绘制详情文字
			Paint textPaint = new Paint();
			textPaint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
			textPaint.setColor(Color.DKGRAY);
			textPaint.setFakeBoldText(true);
            FXCandleDTO data = mOHLCData.get(selectIndext);
            String date = data.getDate();
            if (date.contains("T") && date.contains("."))
            {
			    canvas.drawText(getResources().getString(R.string.date, date.substring(date.indexOf("T")+1, date.indexOf(":00."))), leftText, top + lineHeight, textPaint);
            }
            else
            {
			    canvas.drawText(getResources().getString(R.string.date, date), leftText, top + lineHeight, textPaint);
            }

            //count num after "."
            String s = mOHLCData.get(0).closeMid + "";
            int length = s.length() - s.indexOf(".") - 1;
            String yTitleFormat = "#.";
            for (int i=0;i<length;i++)
            {
                yTitleFormat += "#";
            }

            float leftmargin = DEFAULT_AXIS_TITLE_SIZE * 3.25f;
			canvas.drawText(getResources().getString(R.string.open), leftText, top + 2*lineHeight, textPaint);
			double open = data.getOpen();
			try {
				double ysdclose = mOHLCData.get(selectIndext + 1).getClose();
				if (open >= ysdclose) {
					textPaint.setColor(DEFAULT_CANDLE_DETAIL_RED_COLOR);
				} else {
					textPaint.setColor(getResources().getColor(DEFAULT_CANDLE_DETAIL_DKGREEN_COLOR));
				}
			} catch (Exception e) {
			}
            canvas.drawText(new DecimalFormat(yTitleFormat).format(open),
                    leftText + leftmargin, top + 2*lineHeight, textPaint);

			textPaint.setColor(Color.DKGRAY);
			canvas.drawText(getResources().getString(R.string.high), leftText, top + 3*lineHeight, textPaint);
			double high = data.getHigh();
			if (open < high) {
				textPaint.setColor(DEFAULT_CANDLE_DETAIL_RED_COLOR);
			} else {
				textPaint.setColor(getResources().getColor(DEFAULT_CANDLE_DETAIL_DKGREEN_COLOR));
			}
			canvas.drawText(new DecimalFormat(yTitleFormat).format(high), leftText + leftmargin,
                    top + 3*lineHeight, textPaint);

			textPaint.setColor(Color.DKGRAY);
			canvas.drawText(getResources().getString(R.string.low), leftText, top + 4*lineHeight, textPaint);
			double low = data.getLow();
			try {
				double yesterday = (mOHLCData.get(selectIndext + 1).getLow() + mOHLCData.get(
						selectIndext + 1).getHigh()) / 2.0f;
				if (yesterday <= low) {
					textPaint.setColor(DEFAULT_CANDLE_DETAIL_RED_COLOR);
				} else {
					textPaint.setColor(getResources().getColor(DEFAULT_CANDLE_DETAIL_DKGREEN_COLOR));
				}
			} catch (Exception e) {

			}
			canvas.drawText(new DecimalFormat(yTitleFormat).format(low), leftText + leftmargin,
                    top + 4*lineHeight, textPaint);

			textPaint.setColor(Color.DKGRAY);
			canvas.drawText(getResources().getString(R.string.close), leftText, top + 5*lineHeight, textPaint);
			double close = data.getClose();
			try {
				double yesdopen = (mOHLCData.get(selectIndext + 1).getLow() + mOHLCData.get(
						selectIndext + 1).getHigh()) / 2.0f;
				if (yesdopen <= close) {
					textPaint.setColor(DEFAULT_CANDLE_DETAIL_RED_COLOR);
				} else {
					textPaint.setColor(getResources().getColor(DEFAULT_CANDLE_DETAIL_DKGREEN_COLOR));
				}
			} catch (Exception e) {

			}
			canvas.drawText(new DecimalFormat(yTitleFormat).format(close), leftText + leftmargin,
                    top + 5*lineHeight, textPaint);

			textPaint.setColor(Color.DKGRAY);
			canvas.drawText(getResources().getString(R.string.change), leftText, top + 6*lineHeight, textPaint);
			try {
				double yesdclose = mOHLCData.get(selectIndext + 1).getClose();
				double priceRate = (close - yesdclose) / yesdclose;
				if (priceRate >= 0) {
					textPaint.setColor(DEFAULT_CANDLE_DETAIL_RED_COLOR);
				} else {
					textPaint.setColor(getResources().getColor(DEFAULT_CANDLE_DETAIL_DKGREEN_COLOR));
				}
				canvas.drawText(new DecimalFormat("#.###%").format(priceRate), leftText
						+ leftmargin, top + 6*lineHeight, textPaint);
			} catch (Exception e) {
				canvas.drawText("--", leftText + leftmargin, top + 6*lineHeight, textPaint);
			}
		}
	}

	private void drawTitles(Canvas canvas) {
		Paint textPaint = new Paint();
		textPaint.setColor(DEFAULT_AXIS_Y_TITLE_COLOR);
		textPaint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);

        //count num after "."
        String s = mOHLCData.get(0).closeMid + "";
        int length = s.length() - s.indexOf(".") - 1;
        String yTitleFormat = "#.";
        for (int i=0;i<length;i++)
        {
            yTitleFormat += "#";
        }

		// Titles in y-axis
        double pricePreLatitude = (mMaxPrice - mMinPrice) / DEFAULT_UPER_LATITUDE_NUM;
        for (int i=0;i<=DEFAULT_UPER_LATITUDE_NUM;i++)
        {
            if (i == DEFAULT_UPER_LATITUDE_NUM)
            {
                canvas.drawText(new DecimalFormat(yTitleFormat).format(mMinPrice + pricePreLatitude * i),
                        getCandleWidth() + 10, UPER_CHART_BOTTOM - getLatitudeSpacing() * i - DEFAULT_ONE_LINE_WIDTH + DEFAULT_AXIS_TITLE_SIZE, textPaint);
            }
            else if (i == 0)
            {
                canvas.drawText(new DecimalFormat(yTitleFormat).format(mMinPrice + pricePreLatitude * i),
                        getCandleWidth() + 10, UPER_CHART_BOTTOM - getLatitudeSpacing() * i - DEFAULT_ONE_LINE_WIDTH, textPaint);
            }
            else
            {
                canvas.drawText(new DecimalFormat(yTitleFormat).format(mMinPrice + pricePreLatitude * i),
                        getCandleWidth() + 10, UPER_CHART_BOTTOM - getLatitudeSpacing() * i - DEFAULT_ONE_LINE_WIDTH + DEFAULT_AXIS_TITLE_SIZE / 2, textPaint);
            }
        }

		// Titles in x-axis
		textPaint.setColor(DEFAULT_AXIS_X_TITLE_COLOR);
        for (int i=1;i<DEFAULT_LOGITUDE_NUM;i++)
        {
            float offset;
            int position = mDataStartIndext + mShowDataNum * (DEFAULT_LOGITUDE_NUM-i) / DEFAULT_LOGITUDE_NUM;
            if (i == 0)
            {
                offset = 0;
                position -= 1;
            }
            else if (i == DEFAULT_LOGITUDE_NUM)
            {
                offset = - 2 - 2.5f * DEFAULT_AXIS_TITLE_SIZE;
            }
            else
            {
                offset = - 2 - 1.2f * DEFAULT_AXIS_TITLE_SIZE;
            }
            String date = mOHLCData.get(position).getDate();
            if (date.contains("T") && date.contains("."))
            {
                canvas.drawText(date.substring(date.indexOf("T")+1, date.indexOf(":00.")),
                        DEFAULT_CANDLE_LEFT_MARGIN + (getCandleWidth()-DEFAULT_CANDLE_LEFT_MARGIN)*i/DEFAULT_LOGITUDE_NUM + offset,
                        UPER_CHART_BOTTOM + DEFAULT_AXIS_TITLE_SIZE, textPaint);
            }
            else
            {
                canvas.drawText(date, DEFAULT_CANDLE_LEFT_MARGIN + (getCandleWidth()-DEFAULT_CANDLE_LEFT_MARGIN)*i/DEFAULT_LOGITUDE_NUM + offset,
                        UPER_CHART_BOTTOM + DEFAULT_AXIS_TITLE_SIZE, textPaint);
            }
        }
	}

	private void drawUpperRegion(Canvas canvas) {
		// 绘制蜡烛图
		Paint redPaint = new Paint();
		redPaint.setColor(Color.RED);
		Paint greenPaint = new Paint();
		greenPaint.setColor(Color.GREEN);
		int width = getCandleWidth();
		mCandleWidth = (width - 4 - DEFAULT_CANDLE_LEFT_MARGIN) / 10.0 * 10.0 / mShowDataNum;
        double rate = (getUperChartHeight() - DEFAULT_TWO_LINE_WIDTH) / (mMaxPrice - mMinPrice);
		for (int i = 0; i < mShowDataNum && mDataStartIndext + i < mOHLCData.size(); i++) {
            FXCandleDTO entity = mOHLCData.get(mDataStartIndext + i);
			float open = (float) ((mMaxPrice - entity.getOpen()) * rate + DEFAULT_TWO_LINE_WIDTH);
			float close = (float) ((mMaxPrice - entity.getClose()) * rate + DEFAULT_TWO_LINE_WIDTH);
			float high = (float) ((mMaxPrice - entity.getHigh()) * rate + DEFAULT_TWO_LINE_WIDTH);
			float low = (float) ((mMaxPrice - entity.getLow()) * rate + DEFAULT_TWO_LINE_WIDTH);

            float lineWidth = (float)1.25;
			float left = (float) (width - mCandleWidth * (i + 1) + lineWidth*2);
			float right = (float) (width - mCandleWidth * i - lineWidth*2);
			float startX = (float) (width - mCandleWidth * i - (mCandleWidth - 1) / 2);
			if (open < close) {
				canvas.drawRect(left, open, right, close, redPaint);
				canvas.drawRect(startX - lineWidth, high, startX + lineWidth, low, redPaint);
			} else if (open == close) {
				canvas.drawRect(left, open, right, open + lineWidth*2, greenPaint);
				canvas.drawRect(startX - lineWidth, high, startX + lineWidth, low, greenPaint);
			} else {
				canvas.drawRect(left, close, right, open, greenPaint);
				canvas.drawRect(startX - lineWidth, high, startX + lineWidth, low, greenPaint);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 设置触摸模式
		case MotionEvent.ACTION_DOWN:
			TOUCH_MODE = DOWN;
			mStartX = event.getRawX();
			mStartY = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (TOUCH_MODE == DOWN) {
				TOUCH_MODE = NONE;
				if (!super.onTouchEvent(event)) {
					if (mStartX > 2.0f && mStartX < getCandleWidth() - 2.0f) {
						showDetails = false;//true;
					}
				}
				postInvalidate();
			} else {
				TOUCH_MODE = NONE;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			TOUCH_MODE = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mOHLCData == null || mOHLCData.size() <= 0) {
				return true;
			}
			showDetails = false;
			if (TOUCH_MODE == MOVE) {
				float horizontalSpacing = event.getRawX() - mStartX;
				if (Math.abs(horizontalSpacing) < MIN_MOVE_DISTANCE) {
					return true;
				}
				mStartX = event.getRawX();
				mStartY = event.getRawY();
				if (horizontalSpacing < 0) {
					mDataStartIndext--;
					if (mDataStartIndext < 0) {
						mDataStartIndext = 0;
					}
				} else if (horizontalSpacing > 0) {
					mDataStartIndext++;
				}
				setCurrentData();
				postInvalidate();
			} else if (TOUCH_MODE == ZOOM) {
				float verticalSpacing = event.getRawY() - mStartY;
				if (Math.abs(verticalSpacing) < MIN_MOVE_DISTANCE) {
					return true;
				}
				mStartX = event.getRawX();
				mStartY = event.getRawY();
				if (verticalSpacing < 0) {
					zoomOut();
				} else {
					zoomIn();
				}
				setCurrentData();
				postInvalidate();

			} else if (TOUCH_MODE == DOWN) {
				setTouchMode(event);
			}

			break;
		}
		return true;
	}

	private void setCurrentData() {
		if (mShowDataNum > mOHLCData.size()) {
			mShowDataNum = mOHLCData.size();
		}
		if (MIN_CANDLE_NUM > mOHLCData.size()) {
			mShowDataNum = MIN_CANDLE_NUM;
		}

		if (mShowDataNum > mOHLCData.size()) {
			mDataStartIndext = 0;
		} else if (mShowDataNum + mDataStartIndext > mOHLCData.size()) {
			mDataStartIndext = mOHLCData.size() - mShowDataNum;
		}
		mMinPrice = mOHLCData.get(mDataStartIndext).getLow();
		mMaxPrice = mOHLCData.get(mDataStartIndext).getHigh();
		for (int i = mDataStartIndext + 1; i < mOHLCData.size()
				&& i < mShowDataNum + mDataStartIndext; i++) {
            FXCandleDTO entity = mOHLCData.get(i);
			mMinPrice = mMinPrice < entity.getLow() ? mMinPrice : entity.getLow();
			mMaxPrice = mMaxPrice > entity.getHigh() ? mMaxPrice : entity.getHigh();
		}
	}

	private void zoomIn() {
		mShowDataNum++;
		if (mShowDataNum > mOHLCData.size()) {
			mShowDataNum = MIN_CANDLE_NUM > mOHLCData.size() ? MIN_CANDLE_NUM : mOHLCData.size();
		}

	}

	private void zoomOut() {
		mShowDataNum--;
		if (mShowDataNum < MIN_CANDLE_NUM) {
			mShowDataNum = MIN_CANDLE_NUM;
		}

	}

	private void setTouchMode(MotionEvent event) {
		float daltX = Math.abs(event.getRawX() - mStartX);
		float daltY = Math.abs(event.getRawY() - mStartY);
		if (Math.sqrt(daltX * daltX + daltY * daltY) > MIN_MOVE_DISTANCE) {
			if (daltX < daltY) {
				TOUCH_MODE = ZOOM;
			} else {
				TOUCH_MODE = MOVE;
			}
			mStartX = event.getRawX();
			mStartY = event.getRawY();
		}
	}

	public void setOHLCData(List<FXCandleDTO> OHLCData) {
		if (OHLCData == null || OHLCData.size() <= 0) {
			return;
		}
		this.mOHLCData = OHLCData;
		Collections.reverse(mOHLCData);

		setCurrentData();
		postInvalidate();
	}

}
