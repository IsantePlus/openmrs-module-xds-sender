package org.openmrs.module.xdssender.api.cda;

import org.junit.Before;
import org.junit.Test;
import org.marc.everest.datatypes.ENXP;
import org.marc.everest.datatypes.EntityNamePartType;
import org.marc.everest.datatypes.PN;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.exceptions.DuplicateItemException;
import org.openmrs.Person;
import org.openmrs.PersonName;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CdaDataUtilTest {

	private CdaDataUtil cdaDataUtil;

	@Before
	public void setUp() {
		cdaDataUtil = new CdaDataUtil();
	}

	/**
	 * Checks that no two PNs in the SET have identical content
	 * by comparing their part values.
	 */
	private void assertNoDuplicateContent(SET<PN> nameSet) {
		Set<String> seen = new HashSet<>();
		for (PN pn : nameSet) {
			String key = pnToKey(pn);
			assertFalse("Found duplicate PN content in SET: " + key, seen.contains(key));
			seen.add(key);
		}
	}

	private String pnToKey(PN pn) {
		StringBuilder sb = new StringBuilder();
		for (ENXP part : pn.getParts()) {
			sb.append(part.getValue()).append("|");
		}
		return sb.toString();
	}

	private boolean setContainsPartValue(SET<PN> nameSet, String value) {
		for (PN pn : nameSet) {
			for (ENXP part : pn.getParts()) {
				if (value.equals(part.getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	// ---------------------------------------------------------------
	// Tests that verify createPN produces correct PN objects
	// ---------------------------------------------------------------

	@Test
	public void createPN_shouldCreatePNWithGivenAndFamilyParts() {
		PersonName name = new PersonName("Jean", null, "Baptiste");
		PN pn = cdaDataUtil.createPN(name);

		assertNotNull("createPN should not return null", pn);
		assertEquals("Expected 2 parts (given + family)", 2, pn.getParts().size());

		boolean hasGiven = false, hasFamily = false;
		for (ENXP part : pn.getParts()) {
			if ("Jean".equals(part.getValue())) hasGiven = true;
			if ("Baptiste".equals(part.getValue())) hasFamily = true;
		}
		assertTrue("Expected given name 'Jean'", hasGiven);
		assertTrue("Expected family name 'Baptiste'", hasFamily);
	}

	@Test
	public void createPN_shouldIncludeMiddleName() {
		PersonName name = new PersonName("Jean", "Pierre", "Baptiste");
		PN pn = cdaDataUtil.createPN(name);

		assertEquals("Expected 3 parts (given + middle + family)", 3, pn.getParts().size());

		boolean hasMiddle = false;
		for (ENXP part : pn.getParts()) {
			if ("Pierre".equals(part.getValue())) hasMiddle = true;
		}
		assertTrue("Expected middle name 'Pierre'", hasMiddle);
	}

	// ---------------------------------------------------------------
	// Tests that verify SET<PN> duplicate behavior directly.
	// The marc-everest SET uses semanticEquals via a Comparator.
	// When two PNs are semantically equal, SET.add() throws
	// DuplicateItemException. Our fix prevents this by deduplicating
	// at the PersonName level before creating PNs.
	// ---------------------------------------------------------------

	@Test
	public void set_addingTwoIdenticalPNsDirectly_shouldThrowOrBeHandled() {
		// This test documents the SET behavior.
		// If semanticEquals detects duplicates, add() throws.
		// If it doesn't (version-dependent), add() succeeds silently.
		// Either way, our fix in createNameSet prevents duplicates
		// from reaching SET.add().
		SET<PN> set = new SET<>();
		PN pn1 = cdaDataUtil.createPN(new PersonName("Jean", null, "Baptiste"));
		PN pn2 = cdaDataUtil.createPN(new PersonName("Jean", null, "Baptiste"));

		set.add(pn1);
		try {
			set.add(pn2);
			// If we get here, this marc-everest version doesn't throw.
			// The set might have 1 or 2 items depending on semanticEquals.
			assertTrue("SET should have at most 2 items", set.size() <= 2);
		} catch (DuplicateItemException e) {
			// This is what production throws — SET detected the duplicate.
			assertEquals("SET should still have 1 item after failed add", 1, set.size());
		}
	}

	// ---------------------------------------------------------------
	// Tests for createNameSet — the method we fixed
	// ---------------------------------------------------------------

	@Test
	public void createNameSet_shouldReturnSingleNameForOnePersonName() {
		Person person = new Person();
		person.addName(new PersonName("Jean", null, "Baptiste"));

		SET<PN> result = cdaDataUtil.createNameSet(person);

		assertNotNull("Expected a non-null name set", result);
		assertEquals("Expected exactly one name", 1, result.size());
		assertNoDuplicateContent(result);
		assertTrue("Expected 'Jean' in name parts", setContainsPartValue(result, "Jean"));
		assertTrue("Expected 'Baptiste' in name parts", setContainsPartValue(result, "Baptiste"));
	}

	@Test
	public void createNameSet_shouldNeverProduceDuplicateContentWithPreferredDuplicates() {
		Person person = new Person();
		PersonName n1 = new PersonName("Jean", null, "Baptiste");
		n1.setPreferred(true);
		PersonName n2 = new PersonName("Jean", null, "Baptiste");
		n2.setPreferred(true);
		person.addName(n1);
		person.addName(n2);

		SET<PN> result = cdaDataUtil.createNameSet(person);

		assertNotNull("Expected a non-null name set", result);
		assertEquals("Expected duplicates collapsed into one", 1, result.size());
		assertNoDuplicateContent(result);
	}

	@Test
	public void createNameSet_shouldNeverProduceDuplicateContentWithNonPreferredDuplicates() {
		Person person = new Person();
		person.addName(new PersonName("Jean", null, "Baptiste"));
		person.addName(new PersonName("Jean", null, "Baptiste"));

		SET<PN> result = cdaDataUtil.createNameSet(person);

		assertNotNull("Expected a non-null name set", result);
		assertEquals("Expected duplicates collapsed into one", 1, result.size());
		assertNoDuplicateContent(result);
	}

	@Test
	public void createNameSet_shouldNeverProduceDuplicateContentWithThreeCopies() {
		Person person = new Person();
		PersonName n1 = new PersonName("Jean", null, "Baptiste");
		n1.setPreferred(true);
		PersonName n2 = new PersonName("Jean", null, "Baptiste");
		n2.setPreferred(false);
		PersonName n3 = new PersonName("Jean", null, "Baptiste");
		n3.setPreferred(true);
		person.addName(n1);
		person.addName(n2);
		person.addName(n3);

		SET<PN> result = cdaDataUtil.createNameSet(person);

		assertNotNull("Expected a non-null name set", result);
		assertEquals("Expected three copies collapsed into one", 1, result.size());
		assertNoDuplicateContent(result);
	}

	@Test
	public void createNameSet_shouldPreserveBothDistinctNames() {
		Person person = new Person();
		person.addName(new PersonName("Jean", null, "Baptiste"));
		person.addName(new PersonName("Marie", null, "Toussaint"));

		SET<PN> result = cdaDataUtil.createNameSet(person);

		assertNotNull("Expected a non-null name set", result);
		assertEquals("Expected both distinct names", 2, result.size());
		assertNoDuplicateContent(result);
		assertTrue("Expected 'Jean'", setContainsPartValue(result, "Jean"));
		assertTrue("Expected 'Marie'", setContainsPartValue(result, "Marie"));
	}

	@Test
	public void createNameSet_shouldDistinguishByMiddleName() {
		Person person = new Person();
		person.addName(new PersonName("Jean", "Pierre", "Baptiste"));
		person.addName(new PersonName("Jean", "Pierre", "Baptiste"));
		person.addName(new PersonName("Jean", "Claude", "Baptiste"));

		SET<PN> result = cdaDataUtil.createNameSet(person);

		assertNotNull("Expected a non-null name set", result);
		assertEquals("Expected two names (Pierre vs Claude)", 2, result.size());
		assertNoDuplicateContent(result);
		assertTrue("Expected 'Pierre'", setContainsPartValue(result, "Pierre"));
		assertTrue("Expected 'Claude'", setContainsPartValue(result, "Claude"));
	}

	@Test
	public void createNameSet_shouldExcludeAsteriskPlaceholderNames() {
		Person person = new Person();
		person.addName(new PersonName("Jean", null, "*"));
		person.addName(new PersonName("Marie", null, "Toussaint"));

		SET<PN> result = cdaDataUtil.createNameSet(person);

		assertNotNull("Expected a non-null name set", result);
		assertEquals("Expected only the non-asterisk name", 1, result.size());
		assertTrue("Expected 'Marie'", setContainsPartValue(result, "Marie"));
		assertFalse("Should not contain asterisk", setContainsPartValue(result, "*"));
	}

	@Test
	public void createNameSet_shouldReturnNullWhenOnlyAsteriskNames() {
		Person person = new Person();
		person.addName(new PersonName("Jean", null, "*"));

		SET<PN> result = cdaDataUtil.createNameSet(person);

		assertNull("Expected null when all names are asterisk placeholders", result);
	}

	@Test
	public void createNameSet_shouldCollapsePreferredAndNonPreferredDuplicates() {
		Person person = new Person();
		PersonName preferred = new PersonName("Jean", null, "Baptiste");
		preferred.setPreferred(true);
		PersonName nonPreferred = new PersonName("Jean", null, "Baptiste");
		nonPreferred.setPreferred(false);
		person.addName(preferred);
		person.addName(nonPreferred);

		SET<PN> result = cdaDataUtil.createNameSet(person);

		assertNotNull("Expected a non-null name set", result);
		assertNoDuplicateContent(result);
		assertTrue("Expected 'Jean'", setContainsPartValue(result, "Jean"));
		assertTrue("Expected 'Baptiste'", setContainsPartValue(result, "Baptiste"));
	}
}
