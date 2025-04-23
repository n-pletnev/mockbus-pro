package ru.altacloud.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.within;
import static ru.altacloud.model.Flowmeter.RegisterName.FLOW_RATE;

public class FlowmeterTest {

    private final Flowmeter flowmeter = new Flowmeter(1);

    @Test
    public void testGetFlowRate() {
        float epsilon = 0.000001f;
        Register<Float> register = flowmeter.readRegister(FLOW_RATE.ordinal());
        float value = register.getValue();
        Assertions.assertThat(value)
                .isCloseTo(Math.round(value * 10) / 10f, within(epsilon));
    }
}
