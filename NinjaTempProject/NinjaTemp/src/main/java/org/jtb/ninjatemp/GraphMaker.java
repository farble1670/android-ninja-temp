package org.jtb.ninjatemp;

import android.content.Context;
import android.util.Log;

import java.util.List;

import uk.ac.cam.cl.dtg.snowdon.GraphView;

class GraphMaker {
  private static final String TAG = GraphMaker.class.getSimpleName();

  private final Context context;
  private final Units units;

  GraphMaker(Context context, Units units) {
    this.context = context;
    this.units = units;
  }

  void create(GraphView graph, List<DataPoint> points) {
    if (points.size() < 2) {
      float[][] data = {new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}};
      graph.setData(new float[][][]{data}, 0.0f, 0.0f, 0.0f, 0.0f);
      graph.setXLabels(new String[]{});
      graph.setXLabelPositions(new float[]{});
      graph.setXTickPositions(new float[]{});
      graph.setYLabels(new String[]{});
      graph.setYLabelPositions(new float[]{});
      graph.setYTickPositions(new float[]{});
    } else {
      graph.setXLabels(new String[]{});
      graph.setXLabelPositions(new float[]{});
      graph.setXTickPositions(new float[]{});

      float[] xs = new float[points.size()];
      float[] ys = new float[points.size()];

      float ymin = 0.0f;
      float ymax = 0.0f;

      Interval interval = Interval.obtain(Period.getPeriod(context));

      long xMin = interval.start;
      long xMax = interval.end;

      //Log.d(TAG, String.format("xMin=%d, xMax=%d", xMin, xMax));

      DataPoint.sort(points);

      for (int i = 0; i < points.size(); i++) {
        //Log.d(TAG, String.format("processing time: %s (%d)", points.get(i).getTimeString(), points.get(i).getTimeMillis()));

        long x = points.get(i).getTimeMillis();
        if (x == -1) {
          continue;
        }

        xs[i] = x;

        if (x < xMin) {
          xMin = x;
        } else if (x > xMax) {
          xMax = x;
        }

        float y = units.getValue(points.get(i).getValue());
        ys[i] = y;

        if (ymin == 0.0f) {
          ymin = y;
        } else if (y < ymin) {
          ymin = y;
        }

        if (ymax == 0.0f) {
          ymax = y;
        } else if (y > ymax) {
          ymax = y;
        }
      }

      int yLabelCount = Math.min(points.size(), 7);
      float yinc = (ymax - ymin) / yLabelCount;
      if (ymax == ymin) {
        yinc = 1.0f;
      }

      String[] yLabels = new String[yLabelCount];
      yLabels[0] = getTempString(ymin - yinc);
      for (int p = 1; p < yLabelCount - 1; p++) {
        yLabels[p] = getTempString(ymin + yinc * p);
      }
      yLabels[yLabelCount - 1] = getTempString(ymin + yinc * yLabelCount);

      graph.setYLabels(yLabels);


      float[] yLabelPositions = new float[yLabelCount];
      yLabelPositions[0] = 0.0f;
      for (int p = 1; p < yLabelCount - 1; p++) {
        yLabelPositions[p] = p / (float) (yLabelCount - 1);
      }
      yLabelPositions[yLabelCount - 1] = 1.0f;

      graph.setYLabelPositions(yLabelPositions);
      graph.setYTickPositions(yLabelPositions);

      float[][] data = {xs, ys};


      graph.setData(new float[][][]{data}, xMin, xMax, ymin - 1.0f, ymax + 1.0f);
    }
  }

  static String getTempString(float val) {
    return String.format("%.1f%c", val, (char) 0x00B0);
  }
}
