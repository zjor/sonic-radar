/**
 * References:
 *  - line and circle intersection: http://e-maxx.ru/algo/circle_line_intersection
 *  - circles intersection: http://e-maxx.ru/algo/circles_intersection 
 */
import java.util.List;
import java.util.LinkedList;

float c = 330;

float len = 150;
float dt = 0.3;


void setup() {
  size(800, 600);
  background(0);
  float y = height / 2;
  
  stroke(255);
  line((width - len) / 2, y, (width + len) / 2, y);
  
  float r = c * dt;
  noFill();
  ellipseMode(RADIUS);
  ellipse((width - len) / 2, y, r, r);
  
  stroke(150);
  
  for (int i = int(max(len / 2, r)); i < 2 * len; i += 45) {
    noFill();
    ellipse((width - len) / 2, y, i + r, i + r);
    ellipse((width + len) / 2, y, i, i);
    List<Point> points = intersectCircles((width - len) / 2, y, (width + len) / 2, y, i + r, i);
    fill(50);
    for (Point p: points) {
      ellipse(p.x, p.y, 5, 5);
    }
    
  }  
}

List<Point> intersectCircles(float x1, float y1, float x2, float y2, float r1, float r2) {
  float x3 = x2 - x1;
  float y3 = y2 - y1;
  float a = - 2.0 * x3;
  float b = - 2.0 * y3;
  float c = x3 * x3 + y3 * y3 + r1 * r1 - r2 * r2;
  float x0 = - a * c / (a * a + b * b);
  float y0 = - b * c / (a * a + b * b);
  float d = sqrt(r1 * r1 - c * c / (a * a + b * b));
  float m = sqrt(d * d / (a * a + b * b));
  List<Point> points = new LinkedList<Point>();
  points.add(new Point(x0 + b * m + x1, y0 - a * m + y1));
  points.add(new Point(x0 - b * m + x1, y0 + a * m + y1));
  return points;
}

class Point {
 public final float x;
 public final float y;
 public Point(float x, float y) {
   this.x = x;
   this.y = y;
 }
}
