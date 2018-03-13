package modfix;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

//-Dfml.coreMods.load=modfix.ModFixLC
public class ModFixLC implements IFMLLoadingPlugin {

    //True when using SRG names
    public static boolean runtimeDeobfuscationEnabled = true;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"modfix.ModFixCT"};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        runtimeDeobfuscationEnabled = (boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
