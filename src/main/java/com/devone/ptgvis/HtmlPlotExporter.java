package com.devone.ptgvis;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlPlotExporter {

    public static void export(String ptrnName, List<Location3D> allPoints, List<Location3D> removedPoints, List<Location3D> keptPoints,
                              Location3D observer, Location3D center) throws IOException {

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
                    <title>3D Pattern Visualization</title>
                </head>
                <body>
                    <div id="plot" style="width: 100vw; height: 100vh;"></div>
                    <script>
                        function toTrace(points, color, name, opacity) {
                            return {
                                x: points.map(p => p[0]),
                                y: points.map(p => p[2]),
                                z: points.map(p => p[1]),
                                mode: 'markers',
                                type: 'scatter3d',
                                marker: {
                                    size: 2,
                                    color: color,
                                    opacity: opacity
                                },
                                name: name
                            };
                        }

                        const allPoints = %s;
                        const removedPoints = %s;
                        const keptPoints = %s;

                        const observerTrace = {
                            x: [%d],
                            y: [%d],
                            z: [%d],
                            mode: 'markers',
                            type: 'scatter3d',
                            marker: {
                                size: 6,
                                color: 'red'
                            },
                            name: 'Observer'
                        };

                        const centerTrace = {
                            x: [%d],
                            y: [%d],
                            z: [%d],
                            mode: 'markers',
                            type: 'scatter3d',
                            marker: {
                                size: 6,
                                color: 'blue'
                            },
                            name: 'Center'
                        };

                        const data = [
                            toTrace(allPoints, 'lightgray', 'All (volume)', 0.05),
                            toTrace(removedPoints, 'gray', 'Removed', 0.2),
                            toTrace(keptPoints, 'green', 'Kept (Shape)', 1.0),
                            observerTrace,
                            centerTrace
                        ];

                        Plotly.newPlot('plot', data, {
                            scene: {
                                xaxis: { title: 'X (mc-x)' },
                                yaxis: { title: 'Z (mc-z)' },
                                zaxis: { title: 'Y (mc-y)' }
                            },
                            margin: { l: 0, r: 0, b: 0, t: 0 }
                        });
                    </script>
                </body>
                </html>
                """;

        String all = toJSArray(allPoints);
        String removed = toJSArray(removedPoints);
        String kept = toJSArray(keptPoints);

        String result = String.format(html,
                all, removed, kept,
                observer.x, observer.z, observer.y,  // red marker
                center.x, center.z, center.y         // blue marker
        );

        Path output = Path.of(ptrnName+"_vis.html");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(output))) {
            writer.print(result);
        }

        System.out.println("Visualization saved to: " + ptrnName+"_vis.html");
    }

    private static String toJSArray(List<Location3D> points) {
        return points.stream()
                .map(p -> "[" + p.x + "," + p.y + "," + p.z + "]")
                .collect(Collectors.toList())
                .toString();
    }
}
