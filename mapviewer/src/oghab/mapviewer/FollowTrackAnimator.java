package oghab.mapviewer;

import gov.nasa.worldwind.animation.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.PropertyAccessor;
import gov.nasa.worldwind.view.*;
import gov.nasa.worldwind.view.orbit.*;
import gov.nasa.worldwind.tracks.TrackPoint;
import gov.nasa.worldwind.view.firstperson.BasicFlyView;

public class FollowTrackAnimator extends CompoundAnimator
{
    BasicFlyView flyView;

    TrackPoint[] track;
    Vec4[] speed;
    int index = 0;

    public FollowTrackAnimator(BasicFlyView flyView, Interpolator interpolator,
                                   Animator[] animators,
                                   TrackPoint[] track, Vec4[] speed)
    {
        super(interpolator);
        this.animators = animators;
        this.flyView = (BasicFlyView) flyView;
        if (interpolator == null)
        {
            this.interpolator = new ScheduledInterpolator(10000);
        }
        this.track = track;
        this.speed = speed;
    }


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
            
            long timeToMove = getTimeToMove(flyView.getEyePosition(), track[index].getPosition());

            interpolator = new ScheduledInterpolator(timeToMove);
            animators = createAnimators(flyView, timeToMove, track[index].getPosition(), speed[index]);
        }
    }

    private static Animator[] createAnimators(BasicFlyView flyView, long timeToMove,
                                              Position centerPos,
                                              Vec4 speed)
    {
//        Vec4 upStart = Vec4.UNIT_Y;
//        Vec4 forwardStart = Vec4.UNIT_NEGATIVE_Z;
//
//        Earth earth = new Earth();
//        Vec4 start = earth.computePointFromPosition(centerPos);
//        Vec4 end = start.add3(speed);
//        Position viewingAt =  earth.computePositionFromPoint(end);

//        Angle heading = Vec4.axisAngle(upStart, speed, new Vec4[1]).addDegrees(45.0);
//        Angle pitch = Vec4.axisAngle(forwardStart, speed, new Vec4[1]).subtractDegrees(45.0);
//        Angle roll = Vec4.axisAngle(forwardStart, speed, new Vec4[1]).subtractDegrees(45.0);
        Angle heading = Angle.fromDegrees(0);
        Angle pitch = Angle.fromDegrees(45);
        Angle roll = Angle.fromDegrees(0);
//        centerPos = new Position(centerPos, centerPos.getElevation() + 100);
//        centerPos = new Position(centerPos, 3000);
        double elevation = 2000;

        PositionAnimator centerAnimator = new PositionAnimator(
            new ScheduledInterpolator(timeToMove),
//                flyView.getEyePosition(), centerPos,
flyView.getCenterPosition(), centerPos,
                ViewPropertyAccessor.createEyePositionAccessor(flyView));

        AngleAnimator headingAnimator = new AngleAnimator(
                new ScheduledInterpolator(timeToMove),
                flyView.getHeading(), heading,
                ViewPropertyAccessor.createHeadingAccessor(flyView));

        AngleAnimator pitchAnimator = new AngleAnimator(
                new ScheduledInterpolator(timeToMove),
                flyView.getPitch(), pitch,
                ViewPropertyAccessor.createPitchAccessor(flyView));

        AngleAnimator rollAnimator = new AngleAnimator(
                new ScheduledInterpolator(timeToMove),
                flyView.getRoll(), roll,
                ViewPropertyAccessor.createRollAccessor(flyView));

        DoubleAnimator elevationAnimator = new DoubleAnimator(
                new ScheduledInterpolator(timeToMove),
                flyView.getEyePosition().getElevation(), centerPos.getElevation(),
                ViewPropertyAccessor.createElevationAccessor(flyView));

        Animator[] animators = new Animator[5];
        animators[0] = centerAnimator;
        animators[1] = headingAnimator;
        animators[2] = pitchAnimator;
        animators[3] = rollAnimator;
        animators[4] = elevationAnimator;

        return animators;
    }

    public static FollowTrackAnimator createFollowTrackAnimator(
        BasicFlyView flyView,
        TrackPoint[] track,
        Vec4[] speed)
    {
        long timeToMove = getTimeToMove(flyView.getEyePosition(), track[0].getPosition());
        Animator[] animators = createAnimators(flyView, timeToMove, track[0].getPosition(), speed[0]);

        FollowTrackAnimator panAnimator = new FollowTrackAnimator(flyView,
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
