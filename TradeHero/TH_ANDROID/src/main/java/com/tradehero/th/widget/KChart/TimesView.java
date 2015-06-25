package com.tradehero.th.widget.KChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import com.tradehero.chinabuild.data.QuoteTick;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import java.text.DecimalFormat;
import java.util.List;

public class TimesView extends TimesBase
{
	private final int DATA_MAX_COUNT = 4 * 60 + 2;
	private List<QuoteTick> timesList;

	private float uperBottom;
	private float uperHeight;
	private float lowerBottom;
	private float lowerHeight;
	private float dataSpacing;

	private double initialWeightedIndex;
	private float uperHalfHigh;
	private float lowerHigh;
	private float uperRate;
	private float lowerRate;

	private boolean showDetails;
	private float touchX;

	public TimesView(Context context) {
		super(context);
		init();
	}

	public TimesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TimesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		super.setShowLowerChartTabs(false);
		super.setShowTopTitles(false);

		timesList = null;
		uperBottom = 0;
		uperHeight = 0;
		lowerBottom = 0;
		lowerHeight = 0;
		dataSpacing = 0;

		initialWeightedIndex = 0;
		uperHalfHigh = 0;
		lowerHigh = 0;
		uperRate = 0;
		lowerRate = 0;
		showDetails = false;
		touchX = 0;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (timesList == null || timesList.size() <= 0) {
			return;
		}
		uperBottom = UPER_CHART_BOTTOM - 2;
		uperHeight = getUperChartHeight() - 4;
		lowerBottom = getHeight() - 3 - DEFAULT_AXIS_TITLE_SIZE;
		lowerHeight = getLowerChartHeight() - 2;
		dataSpacing = (getWidth() - 4 - mLeftMargin) * 10.0f / 10.0f / DATA_MAX_COUNT;

		if (uperHalfHigh > 0) {
			uperRate = uperHeight / uperHalfHigh / 2.0f;
		}
		if (lowerHigh > 0) {
            lowerRate = lowerHeight / lowerHigh;
        //Timber.d("lyl lowerRate="+lowerRate+" lowerHeight="+lowerHeight+" lowerHigh="+lowerHigh);
		}

		// 绘制上部曲线及下部线条
		drawLines(canvas);

		// 绘制坐标标题
		drawTitles(canvas);

		// 绘制点击时的详细信息
		//drawDetails(canvas);
	}

	//private void drawDetails(Canvas canvas) {
	//	if (showDetails) {
	//		float width = getWidth();
	//		float left = 5.0f;
	//		float top = 4.0f;
	//		float right = 3.0f + 6.5f * DEFAULT_AXIS_TITLE_SIZE;
	//		float bottom = 7.0f + 4 * DEFAULT_AXIS_TITLE_SIZE;
	//		if (touchX < width / 2.0f) {
	//			right = width - 4.0f;
	//			left = width - 4.0f - 6.5f * DEFAULT_AXIS_TITLE_SIZE;
	//		}
    //
	//		// 绘制点击线条及详情区域
	//		Paint paint = new Paint();
	//		paint.setColor(Color.LTGRAY);
	//		paint.setAlpha(150);
	//		canvas.drawLine(touchX, 2.0f, touchX, UPER_CHART_BOTTOM, paint);
	//		canvas.drawLine(touchX, lowerBottom - lowerHeight, touchX, lowerBottom, paint);
	//		canvas.drawRect(left, top, right, bottom, paint);
    //
	//		Paint borderPaint = new Paint();
	//		borderPaint.setColor(Color.WHITE);
	//		borderPaint.setStrokeWidth(2);
	//		canvas.drawLine(left, top, left, bottom, borderPaint);
	//		canvas.drawLine(left, top, right, top, borderPaint);
	//		canvas.drawLine(right, bottom, right, top, borderPaint);
	//		canvas.drawLine(right, bottom, left, bottom, borderPaint);
    //
	//		//// 绘制详情文字
	//		//Paint textPaint = new Paint();
	//		//textPaint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
	//		//textPaint.setColor(Color.WHITE);
	//		//textPaint.setFakeBoldText(true);
	//		//try {
	//		//	QuoteTick fenshiData = timesList.get((int) ((touchX - 2) / dataSpacing));
	//		//	//TimesEntity fenshiData = timesList.get((int) ((touchX - 2) / dataSpacing));
	//		//	canvas.drawText("时间: " + fenshiData.getTime(), left + 1, top
	//		//			+ DEFAULT_AXIS_TITLE_SIZE, textPaint);
     //       //
	//		//	canvas.drawText("价格:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f, textPaint);
	//		//	double p = fenshiData.getWeightedIndex();
	//		//	if (p >= initialWeightedIndex) {
	//		//		textPaint.setColor(Color.RED);
	//		//	} else {
	//		//		textPaint.setColor(Color.GREEN);
	//		//	}
	//		//	canvas.drawText(new DecimalFormat("#.##").format(p), left + 1
	//		//			+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f,
	//		//			textPaint);
     //       //
	//		//	textPaint.setColor(Color.WHITE);
	//		//	canvas.drawText("涨跌:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f, textPaint);
	//		//	double change = (fenshiData.getWeightedIndex() - initialWeightedIndex)
	//		//			/ initialWeightedIndex;
	//		//	if (change >= 0) {
	//		//		textPaint.setColor(Color.RED);
	//		//	} else {
	//		//		textPaint.setColor(Color.GREEN);
	//		//	}
	//		//	canvas.drawText(new DecimalFormat("#.##%").format(change), left + 1
	//		//			+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f,
	//		//			textPaint);
     //       //
	//		//	textPaint.setColor(Color.WHITE);
	//		//	canvas.drawText("成交:", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f, textPaint);
	//		//	textPaint.setColor(Color.YELLOW);
	//		//	canvas.drawText(String.valueOf(fenshiData.getVolume()), left + 1
	//		//			+ DEFAULT_AXIS_TITLE_SIZE * 2.5f, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f,
	//		//			textPaint);
     //       //
	//		//} catch (Exception e) {
	//		//	canvas.drawText("时间: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE, textPaint);
	//		//	canvas.drawText("价格: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 2.0f, textPaint);
	//		//	canvas.drawText("涨跌: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 3.0f, textPaint);
	//		//	canvas.drawText("成交: --", left + 1, top + DEFAULT_AXIS_TITLE_SIZE * 4.0f, textPaint);
	//		//}
	//	}
    //
	//}

	private void drawTitles(Canvas canvas) {
		// 绘制Y轴titles
		float viewWidth = getWidth();
		Paint paint = new Paint();
		paint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

		paint.setColor(COLOR_GREEN);
		canvas.drawText(new DecimalFormat("#.##").format(initialWeightedIndex - uperHalfHigh),
                mLeftMargin - 50 - DEFAULT_AXIS_TITLE_SIZE, uperBottom, paint);
		canvas.drawText(new DecimalFormat("#.##").format(initialWeightedIndex - uperHalfHigh * 2 / 3),
                mLeftMargin - 50 - DEFAULT_AXIS_TITLE_SIZE, uperBottom - getLatitudeSpacing() + 10, paint);
		canvas.drawText(new DecimalFormat("#.##").format(initialWeightedIndex - uperHalfHigh / 3),
                mLeftMargin - 50 - DEFAULT_AXIS_TITLE_SIZE, uperBottom - getLatitudeSpacing() * 2 + 10, paint);

		paint.setColor(Color.BLACK);
		canvas.drawText(new DecimalFormat("#.##").format(initialWeightedIndex), mLeftMargin - 50 - DEFAULT_AXIS_TITLE_SIZE,
                uperBottom - getLatitudeSpacing() * 3 + 10, paint);

		paint.setColor(COLOR_RED);
        canvas.drawText(new DecimalFormat("#.##").format(uperHalfHigh / 3 + initialWeightedIndex), mLeftMargin - 50 - DEFAULT_AXIS_TITLE_SIZE,
                uperBottom - getLatitudeSpacing() * 4 + 10, paint);
        canvas.drawText(new DecimalFormat("#.##").format(uperHalfHigh * 2 / 3 + initialWeightedIndex), mLeftMargin - 50 - DEFAULT_AXIS_TITLE_SIZE,
                uperBottom - getLatitudeSpacing() * 5 + 10, paint);
		canvas.drawText(new DecimalFormat("#.##").format(uperHalfHigh + initialWeightedIndex), mLeftMargin - 50 - DEFAULT_AXIS_TITLE_SIZE,
				DEFAULT_AXIS_TITLE_SIZE, paint);

		// 绘制X轴Titles
        paint.setColor(Color.BLACK);
		canvas.drawText("09:30", 2 + mLeftMargin, lowerBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
		canvas.drawText("10:30", (viewWidth - mLeftMargin) / 4.0f - DEFAULT_AXIS_TITLE_SIZE * 1.3f + mLeftMargin, lowerBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
		canvas.drawText("13:00", (viewWidth - mLeftMargin) / 2.0f - DEFAULT_AXIS_TITLE_SIZE * 1.3f + mLeftMargin, lowerBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
		canvas.drawText("14:00", (viewWidth - mLeftMargin) * 3 / 4.0f - DEFAULT_AXIS_TITLE_SIZE * 1.3f + mLeftMargin, lowerBottom + DEFAULT_AXIS_TITLE_SIZE, paint);
		canvas.drawText("15:00", viewWidth - 2 - DEFAULT_AXIS_TITLE_SIZE * 2.5f, lowerBottom + DEFAULT_AXIS_TITLE_SIZE, paint);

        //Y轴下Titles
        paint.setTextSize(DEFAULT_AXIS_TITLE_SIZE);
        paint.setColor(Color.BLACK);
        float leftMargin = mLeftMargin - 50 - DEFAULT_AXIS_TITLE_SIZE;
        if (lowerHigh > 100000000) {
            canvas.drawText(new DecimalFormat("####.#").format(lowerHigh / 100000000).concat("亿"), 1 + leftMargin - 30, LOWER_CHART_TOP + 1 + DEFAULT_AXIS_TITLE_SIZE, paint);
        } else {
            canvas.drawText(new DecimalFormat("####.#").format(lowerHigh / 10000).concat("万"), 1 + leftMargin - 50, LOWER_CHART_TOP + 1 + DEFAULT_AXIS_TITLE_SIZE, paint);
        }
        if (lowerHigh / 2 > 100000000) {
            canvas.drawText(new DecimalFormat("####.#").format(lowerHigh / 2 / 100000000).concat("亿"), 1 + leftMargin - 30, LOWER_CHART_TOP + 1 + (DEFAULT_AXIS_TITLE_SIZE + getLowerChartHeight()) / 2, paint);
        } else {
            canvas.drawText(new DecimalFormat("####.#").format(lowerHigh / 2 / 10000).concat("万"), 1 + leftMargin - 50, LOWER_CHART_TOP + 1 + (DEFAULT_AXIS_TITLE_SIZE + getLowerChartHeight()) / 2, paint);
        }
        //if (lowerHigh / 3 > 100000000) {
        //    canvas.drawText(new DecimalFormat("####.#").format(lowerHigh / 3 / 100000000).concat("亿"), 1 + leftMargin - 30, LOWER_CHART_TOP + 1 + DEFAULT_AXIS_TITLE_SIZE + getLowerChartHeight() * 2 / 3, paint);
        //} else {
        //    canvas.drawText(new DecimalFormat("####.#").format(lowerHigh / 3 / 10000).concat("万"), 1 + leftMargin - 50, LOWER_CHART_TOP + 1 + DEFAULT_AXIS_TITLE_SIZE + getLowerChartHeight() * 2 / 3, paint);
        //}
	}

	private void drawLines(Canvas canvas) {
		float x = mLeftMargin;
		float uperWhiteY = 0;
		float uperYellowY = 0;
		Paint paint = new Paint();
		for (int i = 0; i < timesList.size() && i < DATA_MAX_COUNT; i++) {
        QuoteTick fenshiData = timesList.get(i);
        if (fenshiData.price == null || fenshiData.avgPrice == null) {
            continue;
        }

        // 绘制上部表中曲线
        paint.setStrokeWidth(2);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        float endWhiteY = (float) (uperBottom - (fenshiData.avgPrice
                + uperHalfHigh - initialWeightedIndex)
                * uperRate);
        float endYelloY = (float) (uperBottom - (fenshiData.price + uperHalfHigh - initialWeightedIndex)
                * uperRate);
        if (i != 0 && timesList.get(i - 1).price == null) {
            x = 3 + mLeftMargin + dataSpacing * i;
            uperWhiteY = endWhiteY;
            uperYellowY = endYelloY;
            continue;
        }
        if (i != 0) {
            paint.setColor(COLOR_YEllOW);
            canvas.drawLine(x, uperWhiteY, 3 + mLeftMargin + dataSpacing * i, endWhiteY, paint);
            paint.setColor(COLOR_BLUE);
            canvas.drawLine(x, uperYellowY, 3 + mLeftMargin + dataSpacing * i, endYelloY, paint);
        }

        x = 3 + mLeftMargin + dataSpacing * i;
        uperWhiteY = endWhiteY;
        uperYellowY = endYelloY;

        // 绘制下部表内数据线
        if (fenshiData.volume == null || (i != 0 && timesList.get(i - 1).volume == null)) {
            continue;
        }
        Long buy = i == 0 ? fenshiData.volume : fenshiData.volume - timesList.get(i-1).volume;
        paint.setStrokeWidth(3);
        if (i <= 0) {
            paint.setColor(COLOR_RED);
        } else if (fenshiData.price >= timesList.get(i - 1).price) {
            paint.setColor(COLOR_RED);
        } else {
            paint.setColor(COLOR_GREEN);
        }
        canvas.drawLine(x, lowerBottom, x, lowerBottom - buy * lowerRate, paint);
		}
  }

	//@Override
	//public boolean onTouchEvent(MotionEvent event) {
	//	switch (event.getAction()) {
	//	case MotionEvent.ACTION_DOWN:
	//	case MotionEvent.ACTION_MOVE:
	//		touchX = event.getRawX();
	//		if (touchX < 2 || touchX > getWidth() - 2) {
	//			return false;
	//		}
	//		showDetails = true;
	//		postInvalidate();
	//		break;
    //
	//	case MotionEvent.ACTION_UP:
	//	case MotionEvent.ACTION_CANCEL:
	//	case MotionEvent.ACTION_OUTSIDE:
	//		showDetails = false;
	//		break;
    //
	//	default:
	//		break;
	//	}
    //
	//	return true;
	//}

	public void setTimesList(List<QuoteTick> timesList) {
		if (timesList == null || timesList.size() <= 0) {
			return;
		}
		this.timesList = timesList;
        //Timber.d("lyl size="+this.timesList.size());

        QuoteTick fenshiData = timesList.get(0);
		Double price = fenshiData.price;
        Double avgPrice = fenshiData.avgPrice;
		Long volume = fenshiData.volume;
        if (SecurityDetailFragment.mPreClose != null) {
    		    initialWeightedIndex = SecurityDetailFragment.mPreClose;
        }
        if (volume != null) {
    		lowerHigh = volume;
        }
        for (int i = 0; i < timesList.size(); i++) {
            fenshiData = this.timesList.get(i);
            //Timber.d("lyl i="+i+" "+fenshiData.toString());
            if (fenshiData.price == null || fenshiData.avgPrice == null || fenshiData.volume == null) {
                //Timber.d("lyl null i="+i);
                //fix null point
                if (i == 0 || i == timesList.size() - 1 || timesList.get(i+1).price == null || timesList.get(i+1).avgPrice == null || timesList.get(i+1).volume == null) {
                    continue;
                } else if (timesList.get(i-1).price != null && timesList.get(i+1).price != null) {
                    this.timesList.get(i).price = (timesList.get(i-1).price + timesList.get(i+1).price) / 2;
                    this.timesList.get(i).avgPrice = (timesList.get(i-1).avgPrice + timesList.get(i+1).avgPrice) / 2;
                    this.timesList.get(i).volume = (timesList.get(i-1).volume + timesList.get(i+1).volume) / 2;
                }
            }

            if (initialWeightedIndex == 0 && fenshiData.price != null) {
                initialWeightedIndex = fenshiData.price;
            }
            if (lowerHigh == 0 && fenshiData.volume != null) {
                lowerHigh = fenshiData.volume;
            }
            price = fenshiData.price;
            avgPrice = fenshiData.avgPrice;
            if (i == 0) {
                volume = fenshiData.volume;
            } else if (timesList.get(i - 1).volume != null) {
                volume = fenshiData.volume - timesList.get(i - 1).volume;
            } else {
                continue;
            }

            if (initialWeightedIndex == 0 || volume == null) {
                continue;
            }
            uperHalfHigh = (float) (uperHalfHigh > Math
                    .abs(avgPrice - initialWeightedIndex) ? uperHalfHigh : Math
                    .abs(avgPrice - initialWeightedIndex));
            uperHalfHigh = (float) (uperHalfHigh > Math.abs(price - initialWeightedIndex) ? uperHalfHigh
                    : Math.abs(price - initialWeightedIndex));
            lowerHigh = lowerHigh > volume ? lowerHigh : volume;
        }
        postInvalidate();
    }
}
