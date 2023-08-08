package com.example.demo.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.ml.clustering.Clusterable;

@Getter
@Setter
@NoArgsConstructor
public class DoublePoint implements Clusterable {
    private double value;

    public DoublePoint(double value) {
        this.value = value;
    }

    @Override
    public double[] getPoint() {
        return new double[]{value};
    }
}
