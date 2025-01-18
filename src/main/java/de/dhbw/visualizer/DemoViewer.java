package de.dhbw.visualizer;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.NewtChartFactory;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.util.ArrayList;
import java.util.List;

public class DemoViewer extends AbstractAnalysis {

    public static DemoViewer initAndOpen() throws Exception {
        var viewer = new DemoViewer();
        AnalysisLauncher.open(viewer);
        viewer.chart.open();
        return viewer;
    }

    private final List<Runnable> runnables = new ArrayList<>();

    public DemoViewer() {
        super(new NewtChartFactory());
    }

    public void add(Drawable drawable) {
        this.chart.add(drawable);

        if (drawable instanceof Runnable runnable) {
            runnables.add(runnable);
        }
    }

    @Override
    public void init() {
        Quality quality = Quality.Nicest();
        chart = new Chart(this.getFactory(), quality);
        var view = chart.getView();
        view.setSquared(false);
    }
}
