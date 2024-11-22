package de.dhbw.visualizer;

import de.orat.math.view.euclidview3d.GeometryView3d;
import de.orat.math.xml.urdf.Visualizer;
import de.orat.math.xml.urdf.api.Chain;
import de.orat.math.xml.urdf.api.Link;
import de.orat.math.xml.urdf.api.Urdf;
import org.jzy3d.maths.Pair;
import org.jzy3d.plot3d.primitives.RobotType;

import java.util.ArrayList;

public class Main extends GeometryView3d {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        DemoViewer demoViewer = DemoViewer.initAndOpen();
        Urdf jurdf = new Urdf(Visualizer.class.getResourceAsStream("ur5eA.urdf"));
        Chain chain = jurdf.createChain();

        var links = chain.getLinks().keySet();
        var dh = chain.toDH();
        MyRobotMesh robot = new MyRobotMesh(demoViewer.getChart(), RobotType.notype);
        var list = new ArrayList<Pair<Link, Chain.DH>>();

        for (String link : links) {
            list.add(new Pair<>(chain.getLink(link), dh.get(link)));
        }

        robot.add(list);
        robot.addToChartParts();
    }
}
