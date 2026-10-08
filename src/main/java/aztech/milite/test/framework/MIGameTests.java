package aztech.milite.test.framework;

import aztech.milite.MI;
import aztech.milite.test.AssemblerTests;
import aztech.milite.test.BlastFurnaceTests;
import aztech.milite.test.CreativeOrderDump;
import aztech.milite.test.GeneratorTests;
import aztech.milite.test.MachineSpeedTests;
import aztech.milite.test.MultiblockTests;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MIGameTests {
    private MIGameTests() {}

    private static final List<Class<?>> TEST_CLASSES = List.of(
            AssemblerTests.class,
            BlastFurnaceTests.class,
            CreativeOrderDump.class,
            GeneratorTests.class,
            MachineSpeedTests.class,
            MultiblockTests.class);

    @GameTestGenerator
    public static List<TestFunction> generateTests() {
        var result = new ArrayList<TestFunction>();

        for (var testClass : TEST_CLASSES) {
            for (var testMethod : testClass.getMethods()) {
                var gametest = testMethod.getAnnotation(MIGameTest.class);
                if (gametest == null) {
                    continue;
                }

                result.add(new TestFunction(
                        gametest.batch(),
                        MI.ID + "." + testMethod.getName().toLowerCase(Locale.ROOT),
                        MI.id("empty").toString(),
                        StructureUtils.getRotationForRotationSteps(gametest.rotationSteps()),
                        gametest.timeoutTicks(),
                        gametest.setupTicks(),
                        gametest.required(),
                        gametest.manualOnly(),
                        gametest.attempts(),
                        gametest.requiredSuccesses(),
                        gametest.skyAccess(),
                        gameTestHelper -> {
                            try {
                                var testObject = testClass.getConstructor().newInstance();
                                testMethod.invoke(testObject, new MIGameTestHelper(gameTestHelper.testInfo));
                            } catch (ReflectiveOperationException e) {
                                throw new RuntimeException(e);
                            }
                        }));
            }
        }

        return result;
    }
}
