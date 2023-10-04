package com.petrolpark.destroy.config;

public class DestroyContraptionsConfigs extends DestroyConfigBase {

    public final ConfigGroup dynamo = group(0, "dynamo", Comments.dynamo);
    public final ConfigBool dynamoBulkCharging = b(true, "dynamoBulkCharging", Comments.dynamoBulkCharging);

    public final ConfigGroup vat = group(0, "vat", Comments.vat);
    public final ConfigBool vatExplodesAtHighPressure = b(true, "vatExplodesAtHighPressure", Comments.vatExplodesAtHighPressure);
    
    
    @Override
    public String getName() {
        return "contraptions";
    };

    private static class Comments {

        static String dynamo = "Dynamo";
        static String dynamoBulkCharging = "Whether Dynamos can charge multiple Item Stacks at once.";
        static String vat = "Vat";
        static String vatExplodesAtHighPressure = "Whether Vats explode if the pressure exceeds the maximum of the weakest block.";
    };
}
