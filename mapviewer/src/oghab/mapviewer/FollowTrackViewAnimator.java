package oghab.mapviewer;

import gov.nasa.worldwind.animation.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.PropertyAccessor;
import gov.nasa.worldwind.view.*;
import gov.nasa.worldwind.view.orbit.*;
import gov.nasa.worldwind.tracks.TrackPoint;

public class FollowTrackViewAnimator extends CompoundAnimator
{
    BasicOrbitView orbitView;

    TrackPoint[] track;
    Vec4[] speed;
    int index = 0;

    public FollowTrackViewAnimator(OrbitView orbitView, Interpolator interpolator,
                                   Animator[] animators,
                                   TrackPoint[] track, Vec4[] speed)
    {
        super(interpolator);
        this.animators = animators;
        this.orbitView = (BasicOrbitView) orbitView;
        if (interpolator == null)
        {
            this.interpolator = new ScheduledInterpolator(10000);
        }
        this.track = track;
        this.speed = speed;
    }


    @Override
    protected void setImpl(double interpolant)
    {
        boolean allStopped = true;
        for (Animator a : animators)
        {
            if (a != null)
            {
                if (a.hasNext())
                {
                    allStopped = false;
                    a.set(interpolant);
                }
            }
        }
        if (allStopped && index >= track.length)
        {
            this.stop();
        }
        else if (allStopped)
        {           
            index++;
            if(index < track.length)
            {
                long timeToMove = getTimeToMove(orbitView.getEyePosition(), track[index].getPosition());
                interpolator = new ScheduledInterpolator(timeToMove);
                animators = createAnimators(orbitView, timeToMove, track[index].getPosition(), speed[index]);
            }
        }
    }

    static Position startPos;
    static boolean is_first = true;
    private static Animator[] createAnimators(OrbitView orbitView, long timeToMove,
                                              Position centerPos,
                                              Vec4 speed)
    {
        Vec4 upStart = Vec4.UNIT_Y;
        Vec4 forwardStart = Vec4.UNIT_NEGATIVE_Z;
        if(is_first)
        {
            is_first = false;
            startPos = centerPos;
        }
        
//        startPos = orbitView.getCenterPosition();
//        startPos = orbitView.getCurrentEyePosition();
//        startPos = orbitView.getEyePosition();



        Earth earth = new Earth();
        Vec4 start = earth.computePointFromPosition(startPos);
//        Vec4 end = start.add3(speed);
        Vec4 end = earth.computePointFromPosition(centerPos);
        speed = end.subtract3(start);
//        speed = start.subtract3(end);

//        Angle heading = Position.greatCircleAzimuth(startPos, centerPos);

        Angle heading = Vec4.axisAngle(upStart, speed, new Vec4[1]).addDegrees(0.0);
//        Angle pitch = Vec4.axisAngle(forwardStart, speed, new Vec4[1]).subtractDegrees(0.0);
//        Angle roll = Vec4.axisAngle(forwardStart, speed, new Vec4[1]).subtractDegrees(45.0);
//        Angle heading = Angle.fromDegrees(0);
//        Angle heading = Angle.fromDegrees(0);
        Angle pitch = Angle.fromDegrees(90);
        Angle roll = Angle.fromDegrees(0);
//        centerPos = new Position(centerPos, centerPos.getElevation() + 100);
//        centerPos = new Position(centerPos, 3000);
//        double elevation = 2000;
        
//        String text = "("+heading.toDecimalDegreesString(2)+","+pitch.toDecimalDegreesString(2)+","+roll.toDecimalDegreesString(2)+")";
//        MainFrame.balloon.setText(text);



//        ScheduledInterpolator interpolator = new ScheduledInterpolator(timeToMove);
//        myPositionAnimator centerAnimator = new myPositionAnimator(
//                interpolator,
//                startPos, centerPos,
//                ViewPropertyAccessor.createEyePositionAccessor(orbitView));
//
//        AngleAnimator headingAnimator = new AngleAnimator(
//                interpolator,
//                orbitView.getHeading(), heading,
//                ViewPropertyAccessor.createHeadingAccessor(orbitView));
//
//        AngleAnimator pitchAnimator = new AngleAnimator(
//                interpolator,
//                orbitView.getPitch(), pitch,
//                ViewPropertyAccessor.createPitchAccessor(orbitView));
//
//        AngleAnimator rollAnimator = new AngleAnimator(
//                interpolator,
//                orbitView.getRoll(), roll,
//                ViewPropertyAccessor.createRollAccessor(orbitView));

//        DoubleAnimator elevationAnimator = new DoubleAnimator(
//                interpolator,
//                startPos.getAltitude(), centerPos.getAltitude(),
//                ViewPropertyAccessor.createElevationAccessor(orbitView));



        myPositionAnimator centerAnimator = new myPositionAnimator(
            new ScheduledInterpolator(timeToMove),
                startPos, centerPos,
                ViewPropertyAccessor.createEyePositionAccessor(orbitView));

        AngleAnimator headingAnimator = new AngleAnimator(
                new ScheduledInterpolator(timeToMove),
                orbitView.getHeading(), heading,
//                heading, heading,
                ViewPropertyAccessor.createHeadingAccessor(orbitView));

        AngleAnimator pitchAnimator = new AngleAnimator(
                new ScheduledInterpolator(timeToMove),
                orbitView.getPitch(), pitch,
//                pitch, pitch,
                ViewPropertyAccessor.createPitchAccessor(orbitView));

        AngleAnimator rollAnimator = new AngleAnimator(
                new ScheduledInterpolator(timeToMove),
                orbitView.getRoll(), roll,
//                roll, roll,
                ViewPropertyAccessor.createRollAccessor(orbitView));

        DoubleAnimator elevationAnimator = new DoubleAnimator(
                new ScheduledInterpolator(timeToMove),
                startPos.getElevation(), centerPos.getElevation(),
                ViewPropertyAccessor.createElevationAccessor(orbitView));

        
        
//        MoveToPositionAnimator centerAnimator = new MoveToPositionAnimator(
//                startPos, centerPos, 0.9,
//                ViewPropertyAccessor.createEyePositionAccessor(orbitView));
//
//        RotateToAngleAnimator headingAnimator = new RotateToAngleAnimator(
//                orbitView.getHeading(), heading, 0.9,
//                ViewPropertyAccessor.createHeadingAccessor(orbitView));
//
//        RotateToAngleAnimator pitchAnimator = new RotateToAngleAnimator(
//                orbitView.getPitch(), pitch, 0.9,
//                ViewPropertyAccessor.createPitchAccessor(orbitView));
//
//        RotateToAngleAnimator rollAnimator = new RotateToAngleAnimator(
//                orbitView.getRoll(), roll, 0.9,
//                ViewPropertyAccessor.createRollAccessor(orbitView));
//
//        MoveToDoubleAnimator elevationAnimator = new MoveToDoubleAnimator(
//                centerPos.getAltitude() + 1000, 0.9,
//                ViewPropertyAccessor.createElevationAccessor(orbitView));

        int idx = 0;
        Animator[] animators = new Animator[5];
        animators[idx++] = centerAnimator;
        animators[idx++] = elevationAnimator;
        animators[idx++] = headingAnimator;
        animators[idx++] = pitchAnimator;
        animators[idx++] = rollAnimator;

        startPos = centerPos;

        return animators;
    }

    public static FollowTrackViewAnimator createFollowTrackViewAnimator(
        OrbitView orbitView,
        TrackPoint[] track,
        Vec4[] speed)
    {
        long timeToMove = getTimeToMove(orbitView.getEyePosition(), track[0].getPosition());
        Animator[] animators = createAnimators(orbitView, timeToMove, track[0].getPosition(), speed[0]);

        FollowTrackViewAnimator panAnimator = new FollowTrackViewAnimator(orbitView,
            new ScheduledInterpolator(timeToMove), animators,
            track, speed);

        return(panAnimator);
    }

    private static long getTimeToMove(Position beginCenterPos, Position endCenterPos)
    {
        final long MIN_LENGTH_MILLIS = 4000;
        final long MAX_LENGTH_MILLIS = 16000;
        return AnimationSupport.getScaledTimeMillisecs(
            beginCenterPos, endCenterPos,
            MIN_LENGTH_MILLIS, MAX_LENGTH_MILLIS);

    }

}
