package com.github.rlf.bitcloud;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Base-skabelon for Bukkit Plugins
 */
public class BitCloud extends JavaPlugin {
    private int heltal;
    private float decimaltal; // 32 bits
    private double decimaltal2; // 64 bits;
    private char bogstav;
    private String saetning;
    private Object object;

    /**
     * Denne metode SKAL være her (også selvom den er tom), hvis pluginnet skal virke med PlugMan.
     */
    @Override
    public void onEnable() {
        // indlæs konfiguration og initialiser tilstand
        // tjek evt. om softdepend afhængigheder fra plugin.yml er i de korrekte versioner osv.
        BitCloudEvents eventObject = new BitCloudEvents(getConfig(), this);
        getServer().getPluginManager().registerEvents(eventObject, this);
    }

    /**
     * Denne metode SKAL være her (også selvom den er tom), hvis pluginnet skal virke med PlugMan.
     */
    @Override
    public void onDisable() {
        // Ryd op i eventuel tilstand
        // Gem f.eks. ændringer i configuration med saveConfig().
        // Stop med at lytte på div. events
        HandlerList.unregisterAll(this);
    }

    /**
     * Simpel "hook" til Bukkit.
     * Kun kommandoer der er listet i plugin.yml under commands vil lede til denne metode.
     * @param sender    Enten ConsoleCommandSender eller Player
     * @param command   Et data-objekt der repræsenterer indgangen i plugin.yml
     * @param label     Det alias der blev brugt ved kaldet (f.eks. 'mc')
     * @param args      Eventuelle argumenter givet efter selve kommandoen (f.eks. 'god', ved <code>/mc god</code>).
     * @see org.bukkit.command.ConsoleCommandSender
     * @see org.bukkit.entity.Player
     * @return falsk hvis kommandoen enten ikke kunne forstås, eller brugeren ikke har rettigheder, ellers sand.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Du kaldte " + label + " med " + args.length + " argumenter!");
        return true;
    }
}
