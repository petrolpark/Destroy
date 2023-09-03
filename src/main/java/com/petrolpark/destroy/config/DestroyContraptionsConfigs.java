package com.petrolpark.destroy.config;

public class DestroyContraptionsConfigs extends DestroyConfigBase {

    public final ConfigGroup dynamo = group(0, "dynamo", Comments.dynamo);
    public final ConfigBool dynamoBulkCharging = b(true, "dynamoBulkCharging", Comments.dynamoBulkCharging);
    
    
    @Override
    public String getName() {
        return "contraptions";
    };

    private static class Comments {

        static String dynamo = "Dynamo";
        static String dynamoBulkCharging = "Whether Dynamos can charge multiple Item Stacks at once.";
    };
}
