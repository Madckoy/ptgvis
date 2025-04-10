package com.devone.bot.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlPlotExporter {

    public static void export(String ptrnName, List<BotCoordinate3D> innerPts, List<BotCoordinate3D> outerPts, 
                            List<BotCoordinate3D> removedPts, BotCoordinate3D observerPos, BotCoordinate3D figureCenter) throws IOException {

        StringBuilder html = new StringBuilder();
        html.append("<html><head><script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>");
        html.append("<div id='plot' style='width:100%;height:100vh;'></div><script>\n");

        // Преобразуем точки в блоки (для каждой группы: innerPts, outerPts, removedPts)
        addMesh3dSection(html, innerPts, "innerPattern", "green");
        addMesh3dSection(html, outerPts, "outerPattern", "blue");
        addMesh3dSection(html, removedPts, "removedPattern", "red");

        // Добавление наблюдателя и центра как блоков с красным и синим цветами через addMesh3dSection
        List<BotCoordinate3D> observerList = new ArrayList<>();
        observerList.add(observerPos); // Добавляем точку для наблюдателя
        addMesh3dSection(html, observerList, "observer", "red"); // Наблюдатель красный

        List<BotCoordinate3D> centerList = new ArrayList<>();
        centerList.add(figureCenter); // Добавляем точку для центра
        addMesh3dSection(html, centerList, "figureCenter", "blue"); // Центр синий

        // Визуализация 3D
        html.append("Plotly.newPlot('plot', [innerPattern, outerPattern, removedPattern, observer, figureCenter], {")
            .append("margin:{l:0,r:0,b:0,t:30},")
            .append("scene:{xaxis:{title:'X'}, yaxis:{title:'Y'}, zaxis:{title:'Z'}},")
            .append("title:'3D Pattern Visualization'});\n");

        html.append("</script></body></html>");

        // Сохранение результата
        try (FileWriter writer = new FileWriter(ptrnName + "_visualization.html")) {
            writer.write(html.toString());
        }

        System.out.println("Visualization saved to: " + ptrnName + "_visualization.html");
    }

    // Метод для добавления 3D-сетки
    private static void addMesh3dSection(StringBuilder html, List<BotCoordinate3D> points, String varName, String color) {
        if (points == null || points.isEmpty()) {
            System.err.println("Warning: No points to plot for " + varName);
            return;
        }

        html.append("var ").append(varName).append(" = {type:'mesh3d', x:[], y:[], z:[], i:[], j:[], k:[], ")
            .append("facecolor:[], text:[], opacity:0.5, name:'").append(varName)
            .append("', showlegend:true, hoverinfo:'text'};\n");

        html.append("var x = ").append(varName).append(".x, y = ").append(varName).append(".y, z = ").append(varName).append(".z;\n");
        html.append("var i = ").append(varName).append(".i, j = ").append(varName).append(".j, k = ").append(varName).append(".k;\n");
        html.append("var facecolor = ").append(varName).append(".facecolor;\n");
        html.append("var text = ").append(varName).append(".text;\n");

        int vertexOffset = 0;
        for (BotCoordinate3D point : points) {
            // Добавление куба для каждой точки
            double x0 = point.x - 0.5, x1 = point.x + 0.5;
            double y0 = point.y - 0.5, y1 = point.y + 0.5;
            double z0 = point.z - 0.5, z1 = point.z + 0.5;

            // Вершины куба
            html.append("x.push(").append(x0).append("); x.push(").append(x1).append("); x.push(").append(x1).append("); x.push(").append(x0).append(");");
            html.append("x.push(").append(x0).append("); x.push(").append(x1).append("); x.push(").append(x1).append("); x.push(").append(x0).append(");");

            html.append("y.push(").append(y0).append("); y.push(").append(y0).append("); y.push(").append(y1).append("); y.push(").append(y1).append(");");
            html.append("y.push(").append(y0).append("); y.push(").append(y0).append("); y.push(").append(y1).append("); y.push(").append(y1).append(");");

            html.append("z.push(").append(z0).append("); z.push(").append(z0).append("); z.push(").append(z0).append("); z.push(").append(z0).append(");");
            html.append("z.push(").append(z1).append("); z.push(").append(z1).append("); z.push(").append(z1).append("); z.push(").append(z1).append(");");

            // Треугольники
            int[][] faces = {
                {0, 1, 2}, {0, 2, 3},
                {4, 5, 6}, {4, 6, 7},
                {0, 1, 5}, {0, 5, 4},
                {2, 3, 7}, {2, 7, 6},
                {1, 2, 6}, {1, 6, 5},
                {3, 0, 4}, {3, 4, 7}
            };

            for (int[] face : faces) {
                html.append("i.push(").append(vertexOffset + face[0]).append(");");
                html.append("j.push(").append(vertexOffset + face[1]).append(");");
                html.append("k.push(").append(vertexOffset + face[2]).append(");");
            }

            // Цвет и текст
            for (int f = 0; f < faces.length; f++) {
                html.append("facecolor.push('").append(color).append("');");
                html.append("text.push('").append("X: ").append(point.x).append("<br>Y: ").append(point.y).append("<br>Z: ").append(point.z).append("');");
            }

            vertexOffset += 8;
        }
    }
}
