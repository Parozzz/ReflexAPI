/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.NMS.packets;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import me.parozzz.reflex.Debug;
import me.parozzz.reflex.NMS.NMSWrapper;
import me.parozzz.reflex.NMS.ReflectionUtil;
import net.minecraft.server.v1_13_R2.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_13_R2.ParticleParam;
import net.minecraft.server.v1_13_R2.Particles;

import org.bukkit.Color;
import org.bukkit.Location;

/**
 *
 * @author Paros
 */
public class ParticlePacket extends Packet
{
    private final static Class<?> particleEnumClazz;
    private final static Constructor<?> constructor;
    static
    {
        particleEnumClazz = ReflectionUtil.getNMSClass("Particles");

        constructor = ReflectionUtil.getConstructor(ReflectionUtil.getNMSClass("PacketPlayOutWorldParticles"),
        		ReflectionUtil.getNMSClass("ParticleParam"), boolean.class,
                float.class, float.class, float.class, float.class, float.class, float.class, float.class,
                int.class);
    }

    public static enum ParticleEnum implements NMSWrapper
    {
        //From 1.8
        BARRIER,
        EXPLOSION_HUGE, 
        EXPLOSION_LARGE,
        EXPOSION_NORMAL,
        BLOCK_CRACK,  
        BLOCK_DUST, 
        CLOUD,
        CRIT,
        CRIT_MAGIC,
        ENCHANTMENT_TABLE,
        PORTAL,
        FLAME,
        FIREWORKS_SPARK,
        FOOTSTEP,
        VILLAGER_HAPPY,
        VILLAGER_ANGRY,
        HEART,
        ITEM_CRACK,
        ITEM_TAKE,
        SPELL_MOB_AMBIENT, 
        SPELL_MOB,
        SPELL_INSTANT,
        SPELL_WITCH,
        SPELL, 
        SMOKE_LARGE,
        LAVA,
        DRIP_LAVA,
        MOB_APPEARANCE,
        NOTE,
        REDSTONE,
        SLIME,
        SMOKE_NORMAL,
        SNOW_SHOVEL,
        SNOWBALL,
        SUSPENDED,
        TOWN_AURA,
        SUSPENDED_DEPTH, 
        WATER_BUBBLE,
        WATER_SPLASH, 
        DRIP_WATER, 
        WATER_DROP, 
        WATER_WAKE,
        //Da 1.9
        DAMAGE_INDICATOR,
        DRAGON_BREATH,
        END_ROD,
        SWEEP_ATTACK,
        //Da 1.10
        FALLING_DUST,
        //Da 1.11
        SPIT,
        TOTEM;

        private final Object nmsObject;

        private ParticleEnum() 
        {  
            nmsObject = Arrays.stream(particleEnumClazz.getFields())
                    .filter(obj -> obj.toString().equals(name()))
                    .findFirst()
                    .orElse(null);
        }
        
        @Override
        public Object getNMSObject() 
        { 
            return nmsObject; 
        }
    }
    
    private final Object packet;
    public ParticlePacket(final ParticleEnum particle, final float x, final float y, final float z, final float xOffset, final float yOffset, final float zOffset, final float speed, final int amount)
    {
        packet = Debug.validateConstructor(constructor, particle.getNMSObject(), true, x, y, z, xOffset, yOffset, zOffset, speed, amount, null);
    }
        
    public ParticlePacket(final ParticleEnum particle, final Location l, final float xOffset, final float yOffset, final float zOffset, final float speed, final int amount)
    {
        packet = Debug.validateConstructor(constructor, particle.getNMSObject(), true, (float)l.getX(), (float)l.getY(), (float)l.getZ(), xOffset, yOffset, zOffset, speed, amount, null);
    }
    
    public ParticlePacket(final ParticleEnum particle, final Location l, final float speed, final int amount)
    {
        packet = Debug.validateConstructor(constructor, particle.getNMSObject(), true, (float)l.getX(), (float)l.getY(), (float)l.getZ(), 0F, 0F, 0F, speed, amount, null);
    }
    
    public <T extends ColoredParticle> ParticlePacket(final float x, final float y, final float z, final T colored)
    {
        this(colored.getParticle(), x, y, z, colored.getOffsetX(), colored.getOffsetY(), colored.getOffsetZ(), colored.getSpeed(), 0);
    }
    
    public <T extends ColoredParticle> ParticlePacket(final Location l, final T colored)
    {
        this(colored.getParticle(), l, colored.getOffsetX(), colored.getOffsetY(), colored.getOffsetZ(), colored.getSpeed(), 0);
    }
    
    @Override
    public Object getNMSObject() 
    {
        return packet;
    }
    
    private static interface ColoredParticle
    {
        ParticleEnum getParticle();
        float getOffsetX();
        float getOffsetY();
        float getOffsetZ();
        int getSpeed();
    }
    
    public static class ColoredNote implements ColoredParticle
    {
        private final int note;
        public ColoredNote(final int note)
        {
            this.note = note > 24 ? 24 : note;
        }
        
        @Override
        public float getOffsetX() 
        {
            return (float) note / 24F;
        }

        @Override
        public float getOffsetY() 
        {
            return 0;
        }

        @Override
        public float getOffsetZ() 
        {
            return 0;
        }

        @Override
        public int getSpeed() 
        {
            return 1;
        }

        @Override
        public ParticleEnum getParticle() 
        {
            return ParticleEnum.NOTE;
        }
    }
    
    public static class ColoredRedstone implements ColoredParticle
    {
        private final Color color;
        public ColoredRedstone(final Color color)
        {
            this.color = color;
        }
        
        @Override
        public float getOffsetX() 
        {
            return color.getRed() == 0 ? Float.MIN_NORMAL : color.getRed() / 255F;
        }

        @Override
        public float getOffsetY() 
        {
            return color.getGreen() / 255F;
        }

        @Override
        public float getOffsetZ() 
        {
            return color.getBlue() / 255F;
        }

        @Override
        public int getSpeed() 
        {
            return 1;
        }

        @Override
        public ParticleEnum getParticle() 
        {
            return ParticleEnum.REDSTONE; 
        }
    }
    
    public static class ColoredMobSpell implements ColoredParticle
    {
        private final Color color;
        private final boolean ambient;
        public ColoredMobSpell(final Color color, final boolean ambient)
        {
            this.color = color;
            this.ambient = ambient;
        }
        
        @Override
        public float getOffsetX() 
        {
            return color.getRed() / 255F;
        }

        @Override
        public float getOffsetY() 
        {
            return color.getGreen() / 255F;
        }

        @Override
        public float getOffsetZ() 
        {
            return color.getBlue() / 255F;
        }

        @Override
        public int getSpeed() 
        {
            return 0;
        }

        @Override
        public ParticleEnum getParticle() 
        {
            return ambient ? ParticleEnum.SPELL_MOB : ParticleEnum.SPELL_MOB_AMBIENT; 
        }
    }
}
