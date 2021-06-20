package ru.timeconqueror.spongemixins;

import cpw.mods.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SpongeMixins.MODID, version = "1.3.0", name = SpongeMixins.NAME)
public class SpongeMixins {
    public static final String NAME = "SpongeMixins Loader";
    public static final String MODID = "spongemixins";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
}
