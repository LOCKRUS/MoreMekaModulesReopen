package moremekasuitmodules.common.integration.jade;

import moremekasuitmodules.common.block.RadiationChamberBlock;
import moremekasuitmodules.common.block.entity.RadiationChamberBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin("moremekasuitmodules")
public final class RadiationChamberJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(RadiationChamberJadeProvider.INSTANCE, RadiationChamberBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(RadiationChamberJadeProvider.INSTANCE, RadiationChamberBlock.class);
    }
}
