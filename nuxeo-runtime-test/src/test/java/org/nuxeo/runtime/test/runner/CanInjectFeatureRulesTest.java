package org.nuxeo.runtime.test.runner;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.inject.Binder;

@RunWith(FeaturesRunner.class)
@Features(CanInjectFeatureRulesTest.ThisFeature.class)
public class CanInjectFeatureRulesTest {

    public static SuiteRule classRule;

    public static class SuiteRule implements TestRule {

        @Inject
        RunNotifier notifier;

        @Override
        public Statement apply(Statement base, Description description) {
            classRule = this;
            return base;
        }

        protected void assertSelf() {
            assertThat(notifier).isNotNull();
        }

    }

    public static ThisRule testRule;

    public static ThisRule methodRule;

    public static class ThisRule implements TestRule, MethodRule {

        @Inject
        Bean bean;

        @Override
        public Statement apply(Statement base, Description description) {
            testRule = this;
            assertSelf();
            return base;
        }

        protected void assertSelf() {
            assertThat(bean).isNotNull();
        }

        @Override
        public Statement apply(Statement base, FrameworkMethod method,
                Object target) {
            methodRule = this;
            return base;
        }
    }

    public static class ThisFeature extends SimpleFeature {

        @ClassRule
        public static TestRule classRule() {
            return new SuiteRule();
        }

        @Rule
        public TestRule testRule() {
            return new ThisRule();
        }

        @Rule
        public MethodRule methodRule() {
            return new ThisRule();
        }

        @Override
        public void configure(FeaturesRunner runner, Binder binder) {
            binder.bind(Bean.class).toInstance(new Bean());
            ;
        }
    }

    public static class Bean {

    }

    @Inject
    protected Bean bean = null;

    @Test
    public void testIsInjectedByFeatures() {
        assertThat(bean).isNotNull();
    }

    @Test
    public void classRulesAreLoaded() {
        assertThat(classRule).isNotNull();
        classRule.assertSelf();
    }

    @Test
    public void testRulesAreLoaded() {
        assertThat(testRule).isNotNull();
        testRule.assertSelf();
    }

    @Test
    public void methodRulesAreLoaded() {
        assertThat(methodRule).isNotNull();
        methodRule.assertSelf();
    }
}
