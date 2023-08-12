package com.example.demo.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.ml.clustering.Clusterable;

@Getter
@Setter
@NoArgsConstructor
public class DoublePoint implements Clusterable {
    private double[] point;

    public DoublePoint(double[] point) {
        this.point = point;
    }

    @Override
    public double[] getPoint() {
        return point;
    }
}
