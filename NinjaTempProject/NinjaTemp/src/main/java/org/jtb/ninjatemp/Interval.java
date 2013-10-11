package org.jtb.ninjatemp;

public class Interval {
  public final long start;
  public final long end;

  private Interval(long start, long end) {
    this.start = start;
    this.end = end;
  }

  boolean contains(long time) {
    return time >= start && time <= end;
  }

  static Interval obtain(Period period) {
    long end = System.currentTimeMillis();
    long start = end - period.timeMillis;

    return new Interval(start, end);
  }

}
