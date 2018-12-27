package com.github.zjor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Point {
    private float x;
    private float y;

    public float d(Point p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
