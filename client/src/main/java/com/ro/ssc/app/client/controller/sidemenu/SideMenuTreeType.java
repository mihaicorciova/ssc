package com.ro.ssc.app.client.controller.sidemenu;

/**
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public enum SideMenuTreeType {

    NODE_PROJECT("Node Project"),
    // system performance phase
    SYSTEM_PERFORMANCE("System Performance"),
    // metrology phase
    METROLOGY_SETUP("Metrology Setup"),
    TARGET_DESIGN("Target design"),
    YIELDSTAR_RECIPE_PREPARATION("YieldStar recipe preparation"),
    YIELDSTAR_DATA_ANALYSIS("YieldStar data analysis"),
    STACK_VERIFICATION("Stack verification"),
    ACCURACY_QUALIFICATION("Accuracy qualification"),
    MULTI_YIELDSTAR_QUALIFICATION("Multi-YieldStar qualification"),
    // static control setup
    STATIC_CONTROL_SETUP("Static Control Setup"),
    MODEL_AND_SAMPLING_ADVISOR("Model and sampling advisor"),
    SAMPLE_SCHEME_OPTIMIZER("Sample scheme optimizer"),
    BUDGET_BREAKDOWN("Budget breakdown"),
    // dynamic control setup
    DYNAMIC_CONTROL_SETUP("Dynamic Control Setup"),
    SHADOW_MODE_ANALYSIS("Shadow mode analysis"),
    KPI_LIMIT_ADVISOR("KPI limit advisor"),
    // hvm monitoring and control
    HVM_MONITORING_AND_CONTROL("HVM Monitoring and Control");

    private final String title;

    private SideMenuTreeType(final String title) {
        this.title = title;
    }

    public static SideMenuTreeType parse(final String pValue) {

        for (SideMenuTreeType type : SideMenuTreeType.values()) {
            if (type.getTitle().equals(pValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("unknown tree value");
    }

    public String getTitle() {
        return title;
    }

}
