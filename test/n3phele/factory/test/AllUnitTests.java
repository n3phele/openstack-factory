package n3phele.factory.test;

import n3phele.factory.test.units.CloudManagerTest;
import n3phele.factory.test.units.DebugStrategyTest;
import n3phele.factory.test.units.EncryptedHPCredentialsTest;
import n3phele.factory.test.units.SecurityFilterTest;
import n3phele.factory.test.units.ServerOptionsFactoryTest;
import n3phele.factory.test.units.VirtualServerManagerTest;
import n3phele.factory.test.units.VirtualServerResourceTest;
import n3phele.factory.test.units.ZombieStrategiesTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SecurityFilterTest.class, VirtualServerResourceTest.class, ZombieStrategiesTest.class, DebugStrategyTest.class, EncryptedHPCredentialsTest.class
	, CloudManagerTest.class, ServerOptionsFactoryTest.class, VirtualServerManagerTest.class})
public class AllUnitTests {

}
