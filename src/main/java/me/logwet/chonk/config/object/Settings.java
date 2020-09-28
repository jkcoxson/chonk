package me.logwet.chonk.config.object;

import java.util.List;

public class Settings {
    private Boolean enabled;
    private Integer life;
    private String pot;
    private String potType;
    private List<World> worlds;

    public Settings() {
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getLife() {
        return life;
    }

    public void setLife(Integer life) {
        this.life = life;
    }

    public String getPot() {
        return pot;
    }

    public void setPot(String pot) {
        this.pot = pot;
    }

    public String getPotType() {
        return potType;
    }

    public void setPotType(String potType) {
        this.potType = potType;
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public void setWorlds(List<World> worlds) {
        this.worlds = worlds;
    }
}
