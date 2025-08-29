/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package samples;

//import com.sun.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.GL;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.BasicWWTexture;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.WWTexture;
import gov.nasa.worldwind.util.*;

//import javax.media.opengl.GL;

/**
 * @author Patrick Murris
 * @version $Id$
 */
public class TexturedWall implements Renderable
{
    private LatLon[] locations;
    private double[] elevations;
    private WWTexture texture;
    private double opacity = 1;

    private OGLStackHandler ogsh = new OGLStackHandler();

    public TexturedWall(Object imageSource, LatLon location1, LatLon location2,
        double bottomElevation, double topElevation)
    {
        if (imageSource == null)
        {
            String message = Logging.getMessage("nullValue.ImageSource");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (location1 == null || location2 == null)
        {
            String message = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.locations = new LatLon[] {location1, location2};
        this.elevations = new double[] {bottomElevation, topElevation};
        this.texture = new BasicWWTexture(imageSource, true);
    }

    public double getOpacity()
    {
        return this.opacity;
    }

    public void setOpacity(double opacity)
    {
        this.opacity = opacity;
    }

    public void render(DrawContext dc)
    {
/*AliSoft        
        GL gl = dc.getGL();
        try
        {
            if (!dc.isPickingMode())
            {
                this.ogsh.pushClientAttrib(gl, GL.GL_COLOR_BUFFER_BIT // for alpha func
                    | GL.GL_ENABLE_BIT
                    | GL.GL_CURRENT_BIT
                    | GL.GL_DEPTH_BUFFER_BIT // for depth func
                    | GL.GL_TEXTURE_BIT // for texture env
                    | GL.GL_TRANSFORM_BIT
                    | GL.GL_POLYGON_BIT);

                // Enable blending using white premultiplied by the current opacity.
                double opacity = dc.getCurrentLayer() != null
                    ? this.getOpacity() * dc.getCurrentLayer().getOpacity() : this.getOpacity();
                gl.glColor4d(opacity, opacity, opacity, opacity);
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

                // Bind texture
                gl.glEnable(GL.GL_TEXTURE_2D);
                this.texture.bind(dc);
                //this.texture.applyInternalTransform(dc);

            }
            else
            {
                this.ogsh.pushClientAttrib(gl, GL.GL_POLYGON_BIT);
            }

            gl.glDisable(GL.GL_CULL_FACE);
            this.draw(dc);
        }
        finally
        {
            this.ogsh.pop(gl);
        }
*/
    }

    protected void draw(DrawContext dc)
    {
/*AliSoft        
        GL gl = dc.getGL();
        gl.glBegin(GL.GL_TRIANGLE_STRIP);

        Vec4 p;
        double ve = dc.getVerticalExaggeration();
        TextureCoords coords = this.texture.getTexCoords();

        p = dc.getGlobe().computePointFromPosition(this.locations[0], this.elevations[0] * ve);
        gl.glTexCoord2d(coords.left(), coords.bottom());
        gl.glVertex3d(p.x, p.y, p.z);

        p = dc.getGlobe().computePointFromPosition(this.locations[0], this.elevations[1] * ve);
        gl.glTexCoord2d(coords.left(), coords.top());
        gl.glVertex3d(p.x, p.y, p.z);

        p = dc.getGlobe().computePointFromPosition(this.locations[1], this.elevations[0] * ve);
        gl.glTexCoord2d(coords.right(), coords.bottom());
        gl.glVertex3d(p.x, p.y, p.z);

        p = dc.getGlobe().computePointFromPosition(this.locations[1], this.elevations[1] * ve);
        gl.glTexCoord2d(coords.right(), coords.top());
        gl.glVertex3d(p.x, p.y, p.z);

        gl.glEnd();
*/
    }

}
