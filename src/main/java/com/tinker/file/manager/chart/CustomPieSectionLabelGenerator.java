package com.tinker.file.manager.chart;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

import java.text.AttributedString;

/**
 * CustomPieSectionLabelGenerator
 *
 * 重写区域标签显示，实现隐藏标签
 *
 * @author Tinker Chen
 * @date 2019/2/25
 */
public class CustomPieSectionLabelGenerator implements PieSectionLabelGenerator {

    @Override
    public String generateSectionLabel(PieDataset dataset, Comparable key) {
        return null;
    }

    @Override
    public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
        return null;
    }
}
