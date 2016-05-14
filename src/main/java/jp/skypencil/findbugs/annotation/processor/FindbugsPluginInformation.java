package jp.skypencil.findbugs.annotation.processor;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Strings;

import jp.skypencil.findbugs.annotation.FindbugsPlugin;
import lombok.Value;

@Value
class FindbugsPluginInformation {
    private String website;
    private String provider;
    private String pluginId;

    @ParametersAreNonnullByDefault
    static FindbugsPluginInformation of(FindbugsPlugin annotation, String packageName) {
        String pluginId = annotation.pluginid();
        if (Strings.isNullOrEmpty(pluginId)) {
            pluginId = packageName;
        }
        return new FindbugsPluginInformation(annotation.website(), annotation.provider(), pluginId);
    }
}
