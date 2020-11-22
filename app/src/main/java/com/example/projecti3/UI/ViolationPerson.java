package com.example.projecti3.UI;

/**
 *
 * This class will accept data from the display activity and create an object that will later go into the adapter
 */
public class ViolationPerson {
    int Image;
    int Image2;
    String violation;

    public ViolationPerson(int image, int image2, String violation) {
        Image = image;
        Image2 = image2;
        this.violation = violation;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public int getImage2() {
        return Image2;
    }

    public void setImage2(int image2) {
        Image2 = image2;
    }

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }
}
