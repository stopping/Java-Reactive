package org.auvua.model.motion;

/*************************************************************************
 *  Compilation:  javac Quaternion.java
 *  Execution:    java Quaternion
 *
 *  Data type for quaternions.
 *
 *  http://mathworld.wolfram.com/Quaternion.html
 *
 *  % java Quaternion
 *
 *************************************************************************/

public class Quaternion {
    private double real, i, j, k; 

    // create a new object with the given components
    public Quaternion(double real, double i, double j, double k) {
        this.real = real;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    // return a string representation of the invoking object
    public String toString() {
        return real + " + " + i + "i + " + j + "j + " + k + "k";
    }

    // return the quaternion norm
    public double norm() {
        return Math.sqrt(real*real + i*i +j*j + k*k);
    }

    // return the quaternion conjugate
    public Quaternion conjugate() {
        return new Quaternion(real, -i, -j, -k);
    }

    // return a new Quaternion whose value is (this + b)
    public Quaternion plus(Quaternion b) {
        Quaternion a = this;
        return new Quaternion(a.real+b.real, a.i+b.i, a.j+b.j, a.k+b.k);
    }


    // return a new Quaternion whose value is (this * b)
    public Quaternion times(Quaternion b) {
        Quaternion a = this;
        double y0 = a.real*b.real - a.i*b.i - a.j*b.j - a.k*b.k;
        double y1 = a.real*b.i + a.i*b.real + a.j*b.k - a.k*b.j;
        double y2 = a.real*b.j - a.i*b.k + a.j*b.real + a.k*b.i;
        double y3 = a.real*b.k + a.i*b.j - a.j*b.i + a.k*b.real;
        return new Quaternion(y0, y1, y2, y3);
    }

    // return a new Quaternion whose value is the inverse of this
    public Quaternion inverse() {
        double d = real*real + i*i + j*j + k*k;
        return new Quaternion(real/d, -i/d, -j/d, -k/d);
    }


    // return a / b
    // we use the definition a * b^-1 (as opposed to b^-1 a)
    public Quaternion divides(Quaternion b) {
         Quaternion a = this;
        return a.times(b.inverse());
    }
    
    public void setI(double i) {
    	this.i = i;
    }
    
    public void setJ(double j) {
    	this.j = j;
    }
    
    public void setK(double k) {
    	this.k = k;
    }
    
    public double getI() {
    	return i;
    }
    
    public double getJ() {
    	return j;
    }
    
    public double getK() {
    	return k;
    }

}

