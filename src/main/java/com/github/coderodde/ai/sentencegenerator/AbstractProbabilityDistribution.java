package com.github.coderodde.ai.sentencegenerator;
 
import java.util.Objects;
import java.util.Random;
 
public abstract class AbstractProbabilityDistribution<E> {
 
    protected double totalWeight;
    protected final Random random;
 
    protected AbstractProbabilityDistribution() {
        this(new Random());
    }
 
    protected AbstractProbabilityDistribution(Random random) {
        this.random =
                Objects.requireNonNull(random,
                                       "The random number generator is null.");
    }
 
    public abstract boolean isEmpty();
    public abstract int size();
    public abstract boolean addElement(E element, double weight);
    public abstract E sampleElement();
    public abstract boolean contains(E element);
    public abstract boolean removeElement(E element);
    public abstract void clear();
 
    protected void checkWeightNotNaNAndIsPositive(double weight) {
        if (Double.isNaN(weight)) {
            throw new IllegalArgumentException("The element weight is NaN.");
        }
 
        if (weight <= 0.0) {
            throw new IllegalArgumentException(
                    "The element weight must be positive. Received " + weight);
        }
 
        if (Double.isInfinite(weight)) {
            // Once here, 'weight' is positive infinity.
            throw new IllegalArgumentException(
                    "The element weight is infinite.");
        }
    }
 
    protected void checkNotEmpty(int size) {
        if (size == 0) {
            throw new IllegalStateException(
                    "This probability distribution is empty.");
        }
    }
}