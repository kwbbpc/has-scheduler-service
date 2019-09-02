package com.broadway.has.core.commander;

import java.io.IOException;

public interface Commander {

    void sendCommand(WateringRequest watering) throws IOException;
}
