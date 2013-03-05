package n3phele.factory.test;

import n3phele.factory.test.units.SecurityFilterTest;
import n3phele.factory.test.units.VirtualServerResourceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SecurityFilterTest.class, VirtualServerResourceTest.class })
public class AllUnitTests {

}
