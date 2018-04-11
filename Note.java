/*
 * Copyright (C) 2012 Florian Thalmann *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 * */

package melo;

import org.rubato.base.*;
import org.rubato.math.yoneda.*;
import org.rubato.composer.RunInfo;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.math.module.QElement;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.RElement;
import org.rubato.math.arith.Rational;
//import org.rubato.rubettes.util.NoteGenerator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.module.DomainException;
import org.rubato.base.RubatoException;


/**
 * @author Mijia Jiang 
 */

public class Note {
    private LimitDenotator noteDeno;

    static public Note copy(Denotator noteDeno) {
        try {
            LimitDenotator limitNoteDeno = (LimitDenotator)noteDeno;
            return new Note(limitNoteDeno.copy());
        } catch (Exception e) {
        }
        return null;
    }

    public Note copy() {
        return new Note(noteDeno.copy());
    }

    public Note(Denotator noteDeno) {
        this.noteDeno = (LimitDenotator)noteDeno;
    }
    public double getOnset() {
        SimpleDenotator onsetDeno = (SimpleDenotator)noteDeno.getFactor(0);
        return onsetDeno.getReal();
    }
    public double getPitch() { 
        SimpleDenotator pitchDeno = (SimpleDenotator)noteDeno.getFactor(1);
        return pitchDeno.getRational().doubleValue();
    }
    public int getLoudness() {
        SimpleDenotator loudnessDeno = (SimpleDenotator)noteDeno.getFactor(2);
        return loudnessDeno.getInteger();
    }
    public double getDuration() { 
        SimpleDenotator durationDeno = (SimpleDenotator)noteDeno.getFactor(3);
        return durationDeno.getReal();
    }
    public int getVoice() {
        SimpleDenotator voiceDeno = (SimpleDenotator)noteDeno.getFactor(4);
        return voiceDeno.getInteger();
    }
    public void setOnset(double onset) {
        try {
            SimpleDenotator d = new SimpleDenotator(NameDenotator.make(""), (SimpleForm)Repository.systemRepository().getForm("Onset"), new RElement(onset)); 
            setOnset(d);
        } catch (Exception e) {
        }
    }
    public void setPitch(double pitch) {
        try {
            SimpleDenotator d = new SimpleDenotator(NameDenotator.make(""), (SimpleForm)Repository.systemRepository().getForm("Pitch"), new QElement(new Rational(pitch))); 
            setPitch(d);
        } catch (Exception e) {
        }
    }
    public void setLoudness(int loudness) {
        try {
            SimpleDenotator d = new SimpleDenotator(NameDenotator.make(""), (SimpleForm)Repository.systemRepository().getForm("Loudness"), new ZElement(loudness)); 
            setLoudness(d);
        } catch (Exception e) {
        }
    }
    public void setDuration(double duration) {
        try {
            SimpleDenotator d = new SimpleDenotator(NameDenotator.make(""), (SimpleForm)Repository.systemRepository().getForm("Duration"), new RElement(duration)); 
            setDuration(d);
        } catch (Exception e) {
        }
    }
    public void setVoice(int voice) {
        try {
            SimpleDenotator d = new SimpleDenotator(NameDenotator.make(""), (SimpleForm)Repository.systemRepository().getForm("Voice"), new ZElement(voice)); 
            setVoice(d);
        } catch (Exception e) {
        }
    }
    public void setOnset(SimpleDenotator onset) {
        try {
            noteDeno.setFactor(0, onset.copy());
        } catch (Exception e) {
        }
    }
    public void setPitch(SimpleDenotator pitch) {
        try {
            noteDeno.setFactor(1, pitch.copy());
        } catch (Exception e) {
        }
    }
    public void setLoudness(SimpleDenotator loudness) {
        try {
            noteDeno.setFactor(2, loudness.copy());
        } catch (Exception e) {
        }
    }
    public void setDuration(SimpleDenotator duration) {
        try {
            noteDeno.setFactor(3, duration.copy());
        } catch (Exception e) {
        }
    }
    public void setVoice(SimpleDenotator voice) {
        try {
            noteDeno.setFactor(4, voice.copy());
        } catch (Exception e) {
        }
    }
}
