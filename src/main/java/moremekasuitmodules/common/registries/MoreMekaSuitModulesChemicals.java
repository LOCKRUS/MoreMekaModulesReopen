package moremekasuitmodules.common.registries;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import mekanism.common.registration.impl.ChemicalDeferredRegister;
import mekanism.common.registration.impl.DeferredChemical;
import moremekasuitmodules.common.MoreMekaModulesReopen;

public final class MoreMekaSuitModulesChemicals {
    public static final ChemicalDeferredRegister CHEMICALS =
            new ChemicalDeferredRegister(MoreMekaModulesReopen.MODID);

    /**
     * Solipsismium infusion used by the Metallurgic Infuser's yellow input.
     * The custom texture is supplied by the user and keeps the material
     * visually distinct from Mekanism's standard infusion types.
     */
    public static final DeferredChemical<Chemical> SOLIPSISMIUM = CHEMICALS.register(
            "solipsismium", () -> new Chemical(ChemicalBuilder
                    .builder(MoreMekaModulesReopen.rl("infuse_type/solipsismium"))
                    .tint(0xFFFFFF)));

    /** Gas produced as the precursor for Solipsismium infusion. */
    public static final DeferredChemical<Chemical> STRANGE_MATTER = CHEMICALS.register(
            "strange_matter", () -> new Chemical(ChemicalBuilder
                    .builder(MoreMekaModulesReopen.rl("gas/strange_matter"))
                    .gaseous()
                    .tint(0xD58BE7)));

    /** Intermediate chemical crystallized into the Encased Strappel Ingot. */
    public static final DeferredChemical<Chemical> MOLTEN_STRAPPEL_MIXTURE = CHEMICALS.register(
            "molten_strappel_mixture", () -> new Chemical(ChemicalBuilder
                    .builder(MoreMekaModulesReopen.rl("gas/strange_matter"))
                    .gaseous()
                    .tint(0xF06BC0)));

    private MoreMekaSuitModulesChemicals() {
    }
}
