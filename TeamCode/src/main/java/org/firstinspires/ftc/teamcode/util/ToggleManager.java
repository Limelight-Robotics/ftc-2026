package org.firstinspires.ftc.teamcode.util;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple registry for named Toggle instances. Allows writing all toggle
 * states to telemetry in a consistent place.
 */
public class ToggleManager {
    private final Map<String, Toggle> toggles = new LinkedHashMap<>();

    public void register(String name, Toggle toggle) {
        if (name == null || toggle == null)
            return;
        toggles.put(name, toggle);
    }

    public Toggle get(String name) {
        return toggles.get(name);
    }

    public void writeToTelemetry(Telemetry telemetry) {
        if (telemetry == null)
            return;
        for (Map.Entry<String, Toggle> e : toggles.entrySet()) {
            String name = e.getKey();
            Toggle t = e.getValue();
            telemetry.addData(name, t.getState() ? "ON" : "OFF");
        }
    }
}
