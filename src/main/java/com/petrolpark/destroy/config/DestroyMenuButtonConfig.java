package com.petrolpark.destroy.config;

public class DestroyMenuButtonConfig extends DestroyConfigBase {

    public final ConfigGroup mainMenuButtonPosition = group(0, Comments.mainMenu);
    public final ConfigInt mainMenuConfigButtonRow = i(2, 0, 4, "mainMenuConfigButtonRow", Comments.mainMenuConfigButtonRow);
	public final ConfigInt mainMenuConfigButtonOffsetX = i(-28, Integer.MIN_VALUE, Integer.MAX_VALUE, "mainMenuConfigButtonOffsetX", Comments.mainMenuConfigButtonOffsetX);
    
    public final ConfigGroup pauseMenuButtonPosition = group(0, Comments.pauseMenu);
	public final ConfigInt pauseMenuConfigButtonRow = i(3, 0, 5, "ingameMenuConfigButtonRow", Comments.pauseMenuConfigButtonRow);
	public final ConfigInt pauseMenuConfigButtonOffsetX = i(-28, Integer.MIN_VALUE, Integer.MAX_VALUE, "ingameMenuConfigButtonOffsetX", Comments.pauseMenuConfigButtonOffsetX);

    @Override
	public String getName() {
		return "buttonPositions";
	};

    private static class Comments {
        static String

        mainMenu = "Main Menu Button",
        pauseMenu = "Pause Menu Button";

        static String[]
        mainMenuConfigButtonRow = new String[]{
                "Choose the menu row that the Destroy config button appears on in the main menu",
                "Set to 0 to disable the button altogether"
        },
        mainMenuConfigButtonOffsetX = new String[]{
                "Offset the Destroy config button in the main menu by this many pixels on the X axis",
                "The sign (-/+) of this value determines what side of the row the button appears on (left/right)"
        },
        pauseMenuConfigButtonRow = new String[]{
                "Choose the menu row that the Destroy config button appears on in the pause menu",
                "Set to 0 to disable the button altogether"
        },
        pauseMenuConfigButtonOffsetX = new String[]{
                "Offset the Destroy config button in the pause menu by this many pixels on the X axis",
                "The sign (-/+) of this value determines what side of the row the button appears on (left/right)"
        };
    };
};
