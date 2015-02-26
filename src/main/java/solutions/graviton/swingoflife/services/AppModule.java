package solutions.graviton.swingoflife.services;

import java.util.HashSet;
import java.util.Set;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;

import solutions.graviton.swingoflife.enums.CellState;
import solutions.graviton.swingoflife.services.impl.OverPopulationRule;
import solutions.graviton.swingoflife.services.impl.PreservationRule;
import solutions.graviton.swingoflife.services.impl.PropertiesImpl;
import solutions.graviton.swingoflife.services.impl.ReproductionRule;
import solutions.graviton.swingoflife.services.impl.RuleExecutorImpl;
import solutions.graviton.swingoflife.services.impl.UnderPopulationRule;

/**
 * Convenience class for Tapestry IoC configuration.
 * 
 * @author Sean (nenad.natoshevic@gmail.com)
 * 
 */
public class AppModule
{
	/**
	 * Provides interface-to-implementation bindings for Tapestry IoC
	 * 
	 * @param binder
	 */
	public static void bind(ServiceBinder binder)
	{
		binder.bind(RuleExecutor.class, RuleExecutorImpl.class);
		/*
		 * Not necessary to bind rule classes, as can be simply instantiated,
		 * but having them bound by the IoC container, allows them to be
		 * injected in any of the other bound services, which might come in
		 * handy.
		 */
		binder.bind(ConwayRule.class, OverPopulationRule.class).withId(
			"OverPopulationRule");
		binder.bind(ConwayRule.class, UnderPopulationRule.class).withId(
			"UnderPopulationRule");
		binder.bind(ConwayRule.class, PreservationRule.class).withId(
			"PreservationRule");
		binder.bind(ConwayRule.class, ReproductionRule.class).withId(
			"ReproductionRule");

		binder.bind(Properties.class, PropertiesImpl.class);

		// binder.bind(InternalRegistry.class, RegistryImpl.class);
	}

	/**
	 * Provides configuration of sets of rules to be applied for each
	 * {@link CellState}
	 * 
	 * @param configuration
	 */
	@Contribute(RuleExecutor.class)
	public static void provideConwayRules(
		MappedConfiguration<CellState,Set<ConwayRule>> configuration,
		@Inject ObjectLocator locator)
	{
		configuration.add(CellState.ACTIVE, getActiveRuleSet(locator));
		configuration.add(CellState.INACTIVE, getInactiveRules(locator));
	}

	/**
	 * Convenience method for creation of the active rule set
	 * 
	 * @param locator (Tapestry IoC ObjectLocater)
	 * @return set of {@link ConwayRule}
	 */
	private static Set<ConwayRule> getActiveRuleSet(ObjectLocator locator)
	{
		Set<ConwayRule> rules = new HashSet<ConwayRule>();

		rules.add(locator.getService("OverPopulationRule", ConwayRule.class));
		rules.add(locator.getService("UnderPopulationRule", ConwayRule.class));
		rules.add(locator.getService("PreservationRule", ConwayRule.class));

		return rules;
	}

	/**
	 * Convenience method for creation of the inactive rule set.
	 * 
	 * @param locator (Tapestry IoC ObjectLocater)
	 * @return set of {@link ConwayRule}
	 */
	private static Set<ConwayRule> getInactiveRules(ObjectLocator locator)
	{
		Set<ConwayRule> rules = new HashSet<ConwayRule>();

		rules.add(locator.getService("ReproductionRule", ConwayRule.class));

		return rules;
	}
}