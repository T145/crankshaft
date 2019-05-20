package T145.crankshaft.api.constants;

import net.minecraft.util.Identifier;

public class RegistryCS {

	private RegistryCS() {}

	public static final String ID = "crankshaft";
	public static final String NAME = "CrankShaft";

	public static Identifier getIdentifier(String name) {
		return new Identifier(ID, name);
	}
}
