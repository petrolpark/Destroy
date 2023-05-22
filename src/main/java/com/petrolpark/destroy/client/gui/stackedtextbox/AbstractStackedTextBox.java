package com.petrolpark.destroy.client.gui.stackedTextBox;

import com.simibubi.create.foundation.gui.widget.ElementWidget;

public abstract class AbstractStackedTextBox extends ElementWidget {

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
    
};
