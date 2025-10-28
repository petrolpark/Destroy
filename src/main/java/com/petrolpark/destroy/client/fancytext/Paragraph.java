package com.petrolpark.destroy.client.fancytext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.ibm.icu.text.BreakIterator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.fancytext.component.FancyComponent;
import com.petrolpark.destroy.client.fancytext.component.FancyComponent.Maker;
import com.petrolpark.destroy.client.fancytext.component.FancyComponent.MakerMaker;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class Paragraph {

    protected final List<Line> lines;
    public final int width;
    public final int height;

    protected Paragraph(List<Line> lines) {
        this.lines = lines;
        int width = 0;
        int height = 0;
        for (Line line : lines) {
            height += line.height;
            if (line.width > width) width = line.width;
        };
        this.width = width;
        this.height = height;
    };

    public void render(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        for (Line line : lines) {
            for (Line.Entry entry : line) {
                entry.component.render(graphics, font, mouseX, mouseY);
                ms.translate(entry.width, 0f, 0f);
            };
            ms.translate(0f, line.height, 0f);
        };
        ms.popPose();   
    };

    @SuppressWarnings("unchecked")
    public <C extends FancyComponent<C>> Map<Rect2i, C> getAllComponentsOfType(FancyComponent.Type<C> type) {
        Map<Rect2i, C> map = new Object2ObjectArrayMap<>();
        int height = 0;
        for (Line line : lines) {
            int width = 0;
            for (Line.Entry entry : line) {
                if (entry.component.getType() == type) map.put(new Rect2i(width, height, entry.width, entry.component.getHeight()), (C)entry.component);
                width += entry.width;
            };
            height += line.height;
        };
        return map;
    };

    public static Paragraph of(String string, Font font, int maxTextWidth, MakerMaker defaultMakerMaker, char[] delimiters, List<MakerMaker> makerMakers) {
        if (delimiters.length != makerMakers.size()) throw new IllegalArgumentException("Numbers of delimiters and Makers do not match");
        
        // Get an ordered list of Makers
        List<Maker> makers = new ArrayList<>();

        int currentDelimiterIndex = -1;
        boolean delimiting = false;
        String currentSubstring = "";

        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            boolean delimiterFound = false;
            checkDelimiters: for (int delimeterIndex = 0; delimeterIndex < delimiters.length; delimeterIndex++) {
                char delimiter = delimiters[delimeterIndex];
                if (character == delimiter) { // Starting a new segment or finishing an existing one
                    if (!delimiting ) {
                        delimiterFound = true;
                        currentDelimiterIndex = delimeterIndex;
                        break checkDelimiters;
                    } else if (delimiter == delimiters[currentDelimiterIndex]) { // Finishing an existing segment
                        delimiterFound = true;
                        break checkDelimiters;
                    } else { // Out of place delimiter
                        continue checkDelimiters; 
                    }
                };
            };
            if (delimiterFound) {
                if (delimiting) { // If we have just finished delimiting
                    delimiting = false;
                    makers.add(makerMakers.get(currentDelimiterIndex).make(currentSubstring));
                    currentSubstring = "";
                    currentDelimiterIndex = -1;
                } else { // If we are going to start delimiting now
                    delimiting = true;
                    makers.add(defaultMakerMaker.make(currentSubstring));
                    currentSubstring = "";
                };
            } else { // If the delimiter sitch hasn't changed
                currentSubstring += character;
            };
        };
        makers.add(defaultMakerMaker.make(currentSubstring));

        // Split into lines based on future width
        List<List<Pair<Maker, String>>> lines = new ArrayList<>();

        int currentLineWidth = 0;
        List<Pair<Maker, String>> currentLine = new ArrayList<>();

        for (Maker maker : makers) {
            String plainText = maker.getPlainText();
            BreakIterator iterator = maker.getLineBreaker();
            iterator.setText(plainText);

            int substringStart = iterator.first();
            int substringEnd = iterator.next();
            while (substringEnd != BreakIterator.DONE) {
                String substring = plainText.substring(substringStart, substringEnd); // Change the substring to the new size
                int substringWidth = maker.getWidth(substring, font);
                if (currentLineWidth + substringWidth > maxTextWidth) { // Move to a new line if needed
                    lines.add(currentLine);
                    currentLine = new ArrayList<>();
                    currentLineWidth = 0;
                    currentLine.add(Pair.of(maker, substring));
                    currentLineWidth += substringWidth;
                    substringStart = substringEnd; // Only if we needed to start a new line do we move the start of the substring and begin a new one
                };
                substringEnd = iterator.next();
                if (substringEnd == BreakIterator.DONE) { // If there is no more text from this Maker to add, add whatever substring we currently have
                    currentLine.add(Pair.of(maker, substring));
                };
            };
        };
        lines.add(currentLine);

        // Actually make the Fancy Components
        return new Paragraph(lines
            .stream()
            .map(line -> {
                return new Line(line.stream().map(pair -> {
                    Maker maker = pair.getFirst();
                    String text = pair.getSecond();
                    return new Line.Entry(maker.getWidth(text, font), maker.make(text));
                })
                .toList());
            }).toList()
        );

    };
    
    protected static class Line extends ArrayList<Line.Entry> {

        public final int width;
        public final int height;

        public Line(Collection<? extends Line.Entry> collection) {
            super(collection);
            int maxHeight = 0;
            int width = 0;
            for (Line.Entry entry : collection) {
                width += entry.width;
                int componentHeight = entry.component.getHeight();
                if (componentHeight > maxHeight) maxHeight = componentHeight;
            };
            height = maxHeight;
            this.width = width;
        };

        public record Entry(int width, FancyComponent<?> component) {};
    };

    public static class Builder {

        private Font font;
        private int maxTextWith = 200;
        private UnaryOperator<Component> baseComponentTransform;
        private boolean shadow;
        private final Char2ObjectMap<MakerMaker> delimitersAndMakers;

        public Builder() {
            Minecraft minecraft = Minecraft.getInstance();
            font = minecraft.font;
            baseComponentTransform = component -> component;
            shadow = false;
            delimitersAndMakers = new Char2ObjectOpenHashMap<>();
        };

        public Builder palette(FontHelper.Palette palette) {
            delimitersAndMakers.put('_', FancyComponent.Simple.with(c -> c.copy().withStyle(palette.highlight()), shadow));
            baseComponentTransform = c -> c.copy().withStyle(palette.primary());
            return this;
        };

        public Builder with(char delimiter, MakerMaker fancyComponentMakerSupplier) {
            delimitersAndMakers.put(delimiter, fancyComponentMakerSupplier);
            return this;
        };

        public Builder font(Font font) {
            this.font = font;
            return this;
        };

        public Builder maxTextWidth(int maxTextWith) {
            this.maxTextWith = maxTextWith;
            return this;
        };

        public Builder shadow() {
            return shadow(true);
        };

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        };

        public Paragraph build(String text) {
            return Paragraph.of(text, font, maxTextWith, FancyComponent.Simple.with(baseComponentTransform, shadow), delimitersAndMakers.keySet().toCharArray(), new ArrayList<MakerMaker>(delimitersAndMakers.values()));
        };
    };
};
