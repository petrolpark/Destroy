package com.petrolpark.destroy.client.gui.stackedTextBox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.simibubi.create.foundation.gui.widget.ElementWidget;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public abstract class AbstractStackedTextBox extends ElementWidget {

    protected Minecraft minecraft;

    protected AbstractStackedTextBox parent;
    protected AbstractStackedTextBox child;

    protected AbstractStackedTextBox(int x, int y, AbstractStackedTextBox parent, AbstractStackedTextBox child) {
        super(x, y);
        this.parent = parent;
        this.child = child;
    };

    public abstract void close();

    public static AbstractStackedTextBox NOTHING = new AbstractStackedTextBox(0, 0, null, null) {

        @Override
        public void close() {};

        @Override
        public boolean isActive() {
            return false;
        };

    };

    public static LinesAndActivationAreas getTextAndActivationAreas(String text, int startX, int startY, int maxWidthPerLine, Font font) {

        // Break the String into sections of plain text, Molecule names, and hoverable definitions
        List<StackedTextBoxComponent> components = new ArrayList<>();
        String currentSection = "";
        boolean inMoleculeName = false;
        boolean inDefinition = false;
        boolean failed = false;
        addAllSections: for (char character : text.toCharArray()) {
            switch (character) {
                case '[': {
                    if (inDefinition) {
                        failed = true;
                        break addAllSections;
                    };
                    inMoleculeName = true;
                    components.add(new StackedTextBoxComponent.Plain(String.valueOf(currentSection)));
                    currentSection = "";
                    break;
                }
                case ']': {
                    if (!inMoleculeName) {
                        failed = true;
                        break addAllSections;
                    };
                    inMoleculeName = false;
                    components.add(new StackedTextBoxComponent.Molecule(String.valueOf(currentSection)));
                    currentSection = "";
                    break;
                }
                case '{': {
                    if (inMoleculeName) {
                        failed = true;
                        break addAllSections;
                    };
                    inDefinition = true;
                    components.add(new StackedTextBoxComponent.Plain(String.valueOf(currentSection)));
                    currentSection = "";
                    break;
                }
                case '}': {
                    if (!inDefinition) {
                        failed = true;
                        break addAllSections;
                    };
                    inDefinition = false;
                    components.add(new StackedTextBoxComponent.Definition(String.valueOf(currentSection)));
                    currentSection = "";
                    break;
                }
                default: {
                    currentSection += character;
                };
            };
        };

        // Add the final section
        if (currentSection != "") {
            components.add(new StackedTextBoxComponent.Plain(String.valueOf(currentSection)));
        };

        if (failed) {
            components = List.of(new StackedTextBoxComponent.Plain("Badly formatted definition: "+text));
        };

        List<Pair<Area, String>> areasAndTextBoxes = new ArrayList<>();
        Area currentActivationArea = new Area(0, 0, 0, 0);

        int maxLineWidth = 0;

        List<String> lines = new LinkedList<>();
		StringBuilder currentLine = new StringBuilder();
		int currentLineWidth = 0;
        for (StackedTextBoxComponent component : components) {
            addAllWordsInComponent: for (String word : component.getWords()) {
                int wordWidth = font.width(word);
                if (currentLineWidth + wordWidth > maxWidthPerLine) {
                    if (currentLineWidth > 0) {
                        String line = currentLine.toString();
                        lines.add(line);
                        currentLine = new StringBuilder();
                        currentLineWidth = 0;
                    } else { // If the word on its own is bigger than one line
                        lines.add(word);
                        maxLineWidth = Math.max(maxLineWidth, wordWidth);

                        // Set the current activation area to the size of the word
                        currentActivationArea.minX = startX;
                        currentActivationArea.maxX = startX + wordWidth;

                        // Don't add the line again below
                        continue addAllWordsInComponent;
                    };
                };
                currentLine.append(word);

                // Set the current activation area on the offchance there is one
                currentActivationArea.minX = startX + currentLineWidth + 5;
                currentActivationArea.minY = startY + (lines.size()) * font.lineHeight;
                currentActivationArea.maxY = startY + (lines.size() + 1) * font.lineHeight;

                currentLineWidth += wordWidth;
                maxLineWidth = Math.max(maxLineWidth, currentLineWidth);

                // Set the current activation area on the offchance there is one
                currentActivationArea.maxX = startX + currentLineWidth + 5;
            };

            if (component instanceof StackedTextBoxComponent.Definition definition) {
                areasAndTextBoxes.add(Pair.of(currentActivationArea, definition.definitionTranslationKey));
            };

            currentActivationArea = new Area(0, 0, 0, 0);
        };
        // Add the final line
        if (currentLineWidth > 0) {
			lines.add(currentLine.toString());
		};

        List<Component> formattedLines = new ArrayList<>(lines.size());
        for (String line : lines) {
            formattedLines.add(Component.literal(line));
        };

        return new LinesAndActivationAreas(formattedLines, areasAndTextBoxes, maxLineWidth + 5, 11 + (lines.size() + 1) * font.lineHeight);
    };

    public static class Area {
        int minX;
        int maxX;
        int minY;
        int maxY;

        public Area(int x, int y, int width, int height) {
            minX = x;
            minY = y;
            maxX = x + width;
            maxY = y + height;
        };

        public boolean isIn(int x, int y) {
            return (x >= minX && x <= maxX && y >= minY && y <= maxY);
        };
    };

    /**
     * @param lines An ordered list of the lines for this tooltip
     * @param areas Pair of Activation Areas and the IDs of the Stacked Text Boxes they would open
     * @param height The height that this text requires
     * @param width The width that this text requires
     */
    public static record LinesAndActivationAreas(List<Component> lines, List<Pair<Area, String>> areas, int width, int height) {};
    
};
