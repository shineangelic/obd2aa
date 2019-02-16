package uk.co.boconi.emil.obd2aa.ui.gauge;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ArcAnimation extends Animation {

    private ArcProgress circle;

    private float oldAngle;
    private float newAngle;

    public ArcAnimation(ArcProgress circle, float newAngle) {
        this.oldAngle = circle.getProgress();
        this.newAngle = newAngle;
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);

        circle.setProgress(angle);
    }

}
