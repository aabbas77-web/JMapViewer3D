package oghab.mapviewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class myDrawLine{
    public static void main(String args[]) throws Exception {
        JFrame f = new JFrame("Draw a Red Line");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(300, 300);
        f.setLocation(300, 300);
        f.setResizable(false);
        JPanel p = new JPanel() {
            Point pointStart = null;
            Point pointEnd   = null;
            private ArrayList<Integer> xPoints = new ArrayList<>();
            private ArrayList<Integer> yPoints = new ArrayList<>();
            {
                addMouseListener(new MouseAdapter() {
//                    public void mouseClicked(MouseEvent e) {
//                        pointStart = e.getPoint();
//                        System.out.println("mouseClicked: ("+e.getPoint().x+" , "+e.getPoint().y+")");
//                    }
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1)
                        {
                            pointStart = e.getPoint();
                            xPoints.add(e.getPoint().x);
                            yPoints.add(e.getPoint().y);
                            repaint();
                            System.out.println("mousePressed: ("+e.getPoint().x+" , "+e.getPoint().y+")");
                        }
                        else
                        if (e.getButton() == MouseEvent.BUTTON3)
                        {
                            if(xPoints.size() > 0)
                            {
                                xPoints.remove(xPoints.size()-1);
                                yPoints.remove(yPoints.size()-1);
                                if(xPoints.size() > 0)  
                                    pointStart = new Point(xPoints.get(xPoints.size()-1),yPoints.get(yPoints.size()-1));
                                else
                                    pointStart = null;
                                repaint();
                                System.out.println("mousePressed: ("+e.getPoint().x+" , "+e.getPoint().y+")");
                            }
                        }
                    }
//                    public void mouseMoved(MouseEvent e) {
//                        pointEnd = e.getPoint();
//                        repaint();
//                        System.out.println("mouseMoved: ("+e.getPoint().x+" , "+e.getPoint().y+")");
//                    }
//                    public void mouseReleased(MouseEvent e) {
//                        pointStart = null;
//                        System.out.println("mouseReleased: ("+e.getPoint().x+" , "+e.getPoint().y+")");
//                    }
                });
                addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseMoved(MouseEvent e) {
                        pointEnd = e.getPoint();
                        repaint();
//                        System.out.println("mouseMoved: ("+e.getPoint().x+" , "+e.getPoint().y+")");
                    }

//                    public void mouseDragged(MouseEvent e) {
//                        pointEnd = e.getPoint();
//                        repaint();
//                    }
                });
            }
            public void paint(Graphics g) {
                super.paint(g);
                if (pointStart != null)
                {
                    g.setColor(Color.RED);
                    g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
                    int[] Xs = xPoints.stream().mapToInt(Integer::intValue).toArray();                    
                    int[] Ys = yPoints.stream().mapToInt(Integer::intValue).toArray();                    
                    g.drawPolyline(Xs, Ys, xPoints.size());
                }
            }
        };
        f.add(p);
        f.setVisible(true); 
    }
}
