package com.devone.ptgvis;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlPlotExporter {

    public static void export(List<Location3D> allPoints, List<Location3D> removedPoints, List<Location3D> keptPoints) throws IOException {
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
                                // Оставляем X как есть:
                                x: points.map(p => p[0]),

                                // Вместо Minecraft Z выводим на ось Y Plotly:
                                y: points.map(p => p[2]),

                                // Вместо Minecraft Y выводим на ось Z Plotly:
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

                        const data = [
                            toTrace(allPoints, 'lightgray', 'All (volume)', 0.05),
                            toTrace(removedPoints, 'gray', 'Removed', 0.2),
                            toTrace(keptPoints, 'green', 'Kept (Shape)', 1.0)
                        ];

                        Plotly.newPlot('plot', data, {
                            scene: {
                                xaxis: { title: 'X (mc-x)' },
                                yaxis: { title: 'Z (mc-y)' }, // здесь теперь Z (MC) идёт по оси Y
                                zaxis: { title: 'Y (mc=z)' }  // а Y (MC) идёт по оси Z
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

        String result = String.format(html, all, removed, kept);

        Path output = Path.of("pattern_visualization.html");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(output))) {
            writer.print(result);
        }

        System.out.println("Visualization saved to pattern_visualization.html");
    }

    private static String toJSArray(List<Location3D> points) {
        return points.stream()
                .map(p -> "[" + p.x + "," + p.y + "," + p.z + "]")
                .collect(Collectors.toList())
                .toString();
    }
}
