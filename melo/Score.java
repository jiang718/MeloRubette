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

import java.util.*;

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

public class Score {
    private PowerDenotator scoreDeno;
    private PowerForm scoreForm;

    static public Score copy(Denotator scoreDeno) {
        try {
            PowerDenotator powerScoreDeno = (PowerDenotator)scoreDeno;
            return new Score(powerScoreDeno.copy());
        } catch (Exception e) {
        }
        return null;
    }

    public int size() {
        return scoreDeno.getFactorCount();
    }

    public Score copy() {
        return new Score(scoreDeno.copy());
    }

    //initialize a score with select notes -> melody
    //don't copy unless necessary
    //mode == 0->don't copy
    //mod == 1->copy
    public Score(Score score, ArrayList<Integer> selects, Status.CopyMode mode) {
        this(score.toDenotator(), selects, mode);
    }
    public Score(Score score, ArrayList<Integer> selects) {
        this(score.toDenotator(), selects, Status.CopyMode.NOCOPY);
    }
    public Score(Denotator scoreD, ArrayList<Integer> selects, Status.CopyMode mode) {
        try {
            scoreForm = (PowerForm)Repository.systemRepository().getForm("Score");
            PowerDenotator powerScoreDeno = (PowerDenotator)scoreD;
            List<Denotator> notesList = new ArrayList<Denotator>();
            //System.out.println("selects' length: " + selects.size());
            for (int i = 0; i < selects.size(); i++) {
                if (mode == Status.CopyMode.NOCOPY) {
                    notesList.add(powerScoreDeno.getFactor(selects.get(i)));
                } else {
                    notesList.add(new Note(powerScoreDeno.getFactor(selects.get(i)), Status.CopyMode.COPY).toDenotator());
                }
            }
            this.scoreDeno = new PowerDenotator(NameDenotator.make(""), scoreForm, notesList);
            //System.out.println("Get score length in constructor:" + this.scoreDeno.getFactorCount());
            //System.out.println("score deno success");
        } catch (RubatoException e) {
            e.printStackTrace();
        }
    }
    public Score(Denotator scoreDeno, ArrayList<Integer> selects) {
        this(scoreDeno, selects, Status.CopyMode.NOCOPY);
    }

    public Score(Denotator scoreDeno, Status.CopyMode mode) {
        if (mode == Status.CopyMode.NOCOPY) {
            this.scoreDeno = (PowerDenotator)scoreDeno;
        } else {
            this.scoreDeno = (PowerDenotator)Score.copy(scoreDeno).toDenotator();
        }
    }
    public Score(Denotator scoreDeno) {
        this(scoreDeno, Status.CopyMode.NOCOPY);
    }

    public double getOnset(int index) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        return note.getOnset();
    }

    public double getPitch(int index) { 
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        return note.getPitch();
    }
    public int getLoudness(int index) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        return note.getLoudness();
    }
    public double getDuration(int index) { 
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        return note.getDuration();
    }
    public int getVoice(int index) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        return note.getVoice();
    }
    public void setOnset(int index, double onset) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setOnset(onset);
    }
    public void setPitch(int index, double pitch) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setPitch(pitch);
    }
    public void setLoudness(int index, int loudness) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setLoudness(loudness);
    }
    public void setDuration(int index, double duration) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setDuration(duration);
    }
    public void setVoice(int index, int voice) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setVoice(voice);
    }
    public void setOnset(int index, SimpleDenotator onset) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setOnset(onset);
    }
    public void setPitch(int index, SimpleDenotator pitch) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setPitch(pitch);
    }
    public void setLoudness(int index, SimpleDenotator loudness) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setLoudness(loudness);
    }
    public void setDuration(int index, SimpleDenotator duration) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setDuration(duration);
    }
    public void setVoice(int index, SimpleDenotator voice) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setVoice(voice);
    }

    public Denotator toDenotator() {
        return scoreDeno;        
    }

    public void printLong() {
        System.out.println("Score/Motif Length : " + size());
        for (int i = 0; i < scoreDeno.getFactorCount(); i++) {
            Note note = new Note(scoreDeno.getFactor(i));
            note.printLong();
        }
        System.out.println();
    }

    public void print() {
        System.out.println("Score/Motif Length : " + size());
        for (int i = 0; i < scoreDeno.getFactorCount(); i++) {
            Note note = new Note(scoreDeno.getFactor(i));
            note.print();
        }
        System.out.println();
    }

}
