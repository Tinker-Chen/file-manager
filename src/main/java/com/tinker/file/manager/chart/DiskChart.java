package com.tinker.file.manager.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;

/**
 * DiskChart
 *
 * @author Tinker Chen
 * @date 2019/2/25
 */
public class DiskChart {

    private String diskName;
    private long totalSpace;
    private long freeSpace;
    private long usedSpace;

    public DiskChart(String diskName, long totalSpace, long freeSpace, long usedSpace) {
        this.diskName = diskName;
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
        this.usedSpace = usedSpace;
    }

    public ChartPanel generateChartPanel() {
        //设置显示字体，解决乱码问题
        Font font = new Font("Dialog", Font.PLAIN, 12);

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("可用空间", 1.0 * freeSpace / totalSpace);
        dataset.setValue("已用空间", 1.0 * usedSpace / totalSpace );
        JFreeChart chart = ChartFactory.createPieChart("驱动器 " + diskName, dataset);
        chart.getTitle().setFont(font);
        //图例
        LegendTitle legendTitle = chart.getLegend();
        if (legendTitle != null) {
            legendTitle.setItemFont(font);
        }
        //图表区域
        PiePlot piePlot= (PiePlot) chart.getPlot();
        piePlot.setLabelFont(font);
        //隐藏区域标签
        piePlot.setLabelGenerator(new CustomPieSectionLabelGenerator());
        //指定区域颜色
        piePlot.setSectionPaint("可用空间", new Color(84, 84, 255));
        piePlot.setSectionPaint("已用空间", new Color(255, 84, 84));

       return new ChartPanel(chart);
    }
}
