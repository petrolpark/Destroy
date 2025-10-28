package com.petrolpark.destroy.client.stackedtextbox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.widget.ElementWidget;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

@MoveToPetrolparkLibrary
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

    public static LinesAndActivationAreas getTextAndActivationAreas(String text, int startX, int startY, int maxWidthPerLine, Screen screen, Font font, FontHelper.Palette palette, boolean isTextBox) {

        if (screen == null) return new LinesAndActivationAreas(List.of(), List.of(), startX, startY, 0, 0);

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
        if (!currentSection.isEmpty()) {
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
                int wordWidth = font.width(word.replaceAll("_", ""));
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
                currentActivationArea.minX = startX + currentLineWidth;
                currentActivationArea.minY = startY + (isTextBox ? -3 : 0) +(lines.size()) * font.lineHeight;
                currentActivationArea.maxY = startY + (isTextBox ? -3 : 0) + (lines.size() + 1) * font.lineHeight;

                currentLineWidth += wordWidth;
                maxLineWidth = Math.max(maxLineWidth, currentLineWidth);

                // Set the current activation area on the offchance there is one
                currentActivationArea.maxX = startX + currentLineWidth;
            };

            if (component instanceof StackedTextBoxComponent.Definition definition) {
                areasAndTextBoxes.add(Pair.of(currentActivationArea, Component.translatable(definition.definitionTranslationKey).getString()));
            };

            currentActivationArea = new Area(0, 0, 0, 0);
        };
        // Add the final line
        if (currentLineWidth > 0) {
			lines.add(currentLine.toString());
		};

        // Make the lines look pretty
        Couple<Style> styles = Couple.create(palette.highlight(), palette.primary());
        MutableComponent lineStart = Component.literal("");
        lineStart.withStyle(palette.primary());
        boolean currentlyHighlighted = false;
        List<Component> formattedLines = new ArrayList<>(lines.size());
        for (String line : lines) {
            MutableComponent currentComponent = lineStart.plainCopy();
            String[] split = line.split("_");
			for (String part : split) {
				currentComponent.append(Component.literal(part).withStyle(styles.get(currentlyHighlighted)));
				currentlyHighlighted = !currentlyHighlighted;
			}
            formattedLines.add(currentComponent);
			currentlyHighlighted = !currentlyHighlighted;
        };

        // Reposition the tooltip if it's in the bottom or right-hand side of the screen
        int width = maxLineWidth + 5;
        int height = 11 + (lines.size() + 1) * font.lineHeight;
        if (startX + (width / 2) > screen.width / 2) {
            startX -= width;
            for (Pair<Area, String> pair : areasAndTextBoxes) { // Reposition the Activation Areas to match the new position of the text box
                pair.getFirst().minX -= width;
                pair.getFirst().maxX -= width;
            };
        };
        if (startY + (height / 2) > screen.height / 2) {
            startY -= (height + font.lineHeight);
            for (Pair<Area, String> pair : areasAndTextBoxes) { // Reposition the Activation Areas to match the new position of the text box
                pair.getFirst().minY -= (height + font.lineHeight);
                pair.getFirst().maxY -= (height + font.lineHeight);
            };
        };

        return new LinesAndActivationAreas(formattedLines, areasAndTextBoxes, startX, startY, width, height);
    };

    public static class Area {
        public int minX;
        public int maxX;
        public int minY;
        public int maxY;

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
     * @param startX
     * @param startY
     * @param height The height that this text requires
     * @param width The width that this text requires
     */
    public static record LinesAndActivationAreas(List<Component> lines, List<Pair<Area, String>> areas, int startX, int startY, int width, int height) {};
    
};
