package org.rubato.melo;
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
import org.rubato.logeo.FormFactory;


/**
 * @author Mijia Jiang
 */

public class Score {
    protected PowerDenotator scoreDeno;

    protected List<Integer> rankList;
    public boolean ifMotif;
    public boolean ifRev;
    protected List<Double> weightList;
    protected double totalWeight = 0;

    protected PowerForm scoreForm;
    protected SimpleForm rankForm;
    protected PowerForm rankListForm;
    protected SimpleForm weightForm;
    protected PowerForm weightListForm;
    protected LimitForm scoreOrMotifForm;

    /**********************BASIC START***************************/
    public Score(Score score, List<Integer> rankList, Status.CopyMode mode) {
        this(score.toScoreDeno(), rankList, score.getIfRev(), mode);
    }
    public Score(Score score, List<Integer> rankList) {
        this(score.toScoreDeno(),rankList, score.getIfRev(), Status.CopyMode.NOCOPY);
    }
    public Score(Denotator scorerDenoGen, List<Integer> rankListT, boolean ifRevT, Status.CopyMode mode) {
        genForm();
        try {
            ifRev = ifRevT;
            scoreForm = (PowerForm)Repository.systemRepository().getForm("Score");
            PowerDenotator scorerDeno = (PowerDenotator)scorerDenoGen;
            rankList = new ArrayList<Integer>();
            weightList = new ArrayList<Double>();
            if (rankListT == null || rankListT.isEmpty() || rankListT.get(0) == -1) {
                System.out.println("this is not motif");
                ifMotif = false;
                for (int rankId = 0; rankId < scorerDeno.getFactorCount(); rankId++) {
                    rankList.add(-1);
                }
                for (int weightId = 0; weightId < scorerDeno.getFactorCount(); weightId++) {
                    weightList.add(0.0);
                }
            } else {
                System.out.println("this is motif");
                ifMotif = true;
                for (int rankId = 0; rankId < rankListT.size(); rankId++) {
                    rankList.add(rankListT.get(rankId));
                }
                for (int weightId = 0; weightId < rankListT.size(); weightId++) {
                    weightList.add(0.0);
                }
            }
            List<Denotator> scoreDenoList = new ArrayList<Denotator>();
            if (rankListT != null && !rankListT.isEmpty()) {
                for (int i = 0; i < rankListT.size(); i++) {
                    if (mode == Status.CopyMode.NOCOPY) {
                        scoreDenoList.add(scorerDeno.getFactor(rankListT.get(i)));
                    } else {
                        scoreDenoList.add(new Note(scorerDeno.getFactor(rankListT.get(i)), Status.CopyMode.COPY).toDeno());
                    }
                }
                scoreDeno = new PowerDenotator(NameDenotator.make("scoreDeno"), scoreForm, scoreDenoList);
            } else {
                if (mode == Status.CopyMode.NOCOPY) {
                    scoreDeno = scorerDeno;
                } else {
                    scoreDeno = scorerDeno.copy();
                }
            }
        } catch (RubatoException e) {
            e.printStackTrace();
        }
    }
    public Score(Denotator scorerDenoGen,List<Integer> rankList, boolean ifRev) {
        this(scorerDenoGen, rankList, ifRev, Status.CopyMode.NOCOPY);
    }
    public Score(Denotator scoreDenoGen, boolean ifRev, Status.CopyMode mode) {
        this(scoreDenoGen, new ArrayList<Integer>(), ifRev, mode);
    }
    public Score(Denotator scoreDenoGen, boolean ifRev) {
        this(scoreDenoGen, new ArrayList<Integer>(), ifRev, Status.CopyMode.NOCOPY);
    }
    public Score(Denotator scoreDenoGen, Status.CopyMode mode) {
        this(scoreDenoGen, false, mode);
    }
    public Score(Denotator scoreDenoGen) {
        this(scoreDenoGen, false, Status.CopyMode.NOCOPY);
    }
    public Score(List<Note> noteList, List<Integer> rankListT, boolean ifRevT) {
        genForm();
        rankList = new ArrayList<Integer>();
        if (rankListT == null || rankListT.isEmpty() || rankListT.get(0) == -1) {
            ifMotif = false;
            for (int rankId = 0; rankId < rankListT.size(); rankId++) {
                rankList.add(-1);
            }
        } else {
            ifMotif = true;
            for (int rankId = 0; rankId < rankListT.size(); rankId++) {
                rankList.add(rankListT.get(rankId));
            }
        }
        ifRev = ifRevT;
        if (noteList.size() == 0) return;
        weightList = new ArrayList<Double>();
        for (int weightId = 0; weightId < noteList.size(); weightId++) {
            weightList.add(0.0);
        }
        List<Denotator> scoreDenoList = new ArrayList<Denotator>();
        for (int i = 0; i < noteList.size(); i++) {
            scoreDenoList.add(noteList.get(i).toDeno());
        }
        try {
            scoreDeno = new PowerDenotator(NameDenotator.make("scoreDeno"), scoreForm, scoreDenoList);
        } catch (RubatoException e) {
            e.printStackTrace();
        }
    }
    static public Score copy(Denotator scoreDenoGen) {
        try {
            PowerDenotator scoreDeno = (PowerDenotator)scoreDenoGen;
            return new Score(scoreDeno.copy());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Score copy() {
        return new Score(scoreDeno.copy(), ifRev);
    }
    /**********************BASIC END***************************/

    /*********************Format START**********************/
    public void genForm() {
        scoreForm = (PowerForm)Repository.systemRepository().getForm("Score");
        weightForm = (SimpleForm)Repository.systemRepository().getForm("Real");
        weightListForm = FormFactory.makePowerForm("weightListForm", weightForm);
        rankForm = (SimpleForm)Repository.systemRepository().getForm("Integer");
        rankListForm = FormFactory.makePowerForm("rankListForm", rankForm);
        List<Form> scoreOrMotifFormList = new LinkedList<Form>();
        List<String> scoreOrMotifFormLabelList = new LinkedList<String>();
        scoreOrMotifFormList.add(scoreForm);
        scoreOrMotifFormList.add(rankListForm);
        scoreOrMotifFormList.add(weightListForm);
        scoreOrMotifFormLabelList.add("score");
        scoreOrMotifFormLabelList.add("rankList");
        scoreOrMotifFormLabelList.add("weightList");
        scoreOrMotifForm = FormFactory.makeLimitForm("scoreOrMotifForm", scoreOrMotifFormList);
        scoreOrMotifForm.setLabels(scoreOrMotifFormLabelList);
    }
    //if it's not motif, just ignore idList (set to -1)
    public Denotator toDeno() {
        try {
            System.out.println("Before weightListDenoList:" );
            List<Denotator> weightListDenoList = new ArrayList<Denotator>();
            System.out.println("weightList size: " + weightList.size());
            for (int i = 0; i < weightList.size(); i++) {
                System.out.println("After weightDeno0:" + i);
                System.out.println("weighForm == null" + weightForm == null);
                SimpleDenotator weightDeno = new SimpleDenotator(NameDenotator.make("weightDeno"), weightForm, new RElement(weightList.get(i)));
                System.out.println("After weightDeno1:" + i);
                weightListDenoList.add(weightDeno);
                System.out.println("After weightDeno2:" + i);
            }
            System.out.println("After weightListDenoList");
            PowerDenotator weightListDeno = new PowerDenotator(NameDenotator.make("weightListDeno"), weightListForm, weightListDenoList);
            System.out.println("After weightListDeno");
            List<Denotator> rankListDenoList = new ArrayList<Denotator>();
            for (int i = 0; i < rankList.size(); i++) {
                SimpleDenotator rankDeno = new SimpleDenotator(NameDenotator.make("rankDeno"), rankForm, new ZElement(rankList.get(i)));
                System.out.println("After rankDeno");
                rankListDenoList.add(rankDeno);
            }
            PowerDenotator rankListDeno = new PowerDenotator(NameDenotator.make("rankListDeno"), rankListForm, rankListDenoList);
            System.out.println("After rankListDeno");
            List<Denotator> scoreOrMotifDenoList = new ArrayList<Denotator>();
            scoreOrMotifDenoList.add(scoreDeno);
            scoreOrMotifDenoList.add(rankListDeno);
            scoreOrMotifDenoList.add(weightListDeno);
            System.out.println("After scoreOrMotifDenoList");
            LimitDenotator scoreOrMotifDeno = new LimitDenotator(NameDenotator.make("scoreOrMotifDeno"), scoreOrMotifForm, scoreOrMotifDenoList);
            System.out.println("After scoreOrMotifDeno");
            return scoreOrMotifDeno;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Denotator toScoreDeno() {
        return scoreDeno;
    }
    public Form getForm() {
        return scoreOrMotifForm;
    }
    /*********************Format END**********************/



    /*********************Property START**********************/
    public int size() {
        return scoreDeno.getFactorCount();
    }
    public boolean getIfMotif() {
        return ifMotif;
    }
    public static Score getEmptyScore() {
        return new Score(new ArrayList<Note>(), null, false);
    }
    public int getRank(int index) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rankList.get(index);
    }
    public boolean getIfRev() {
        return ifRev;
    }
    /*********************Property END**********************/

    /************Property(note related) START********************/
    public Denotator getNoteDeno(int index) {
        return scoreDeno.getFactor(index);

    }
    public double getOnset(int index) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setVoice(voice);
    }
    public void setOnset(int index, SimpleDenotator onsetDeno) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setOnset(onsetDeno);
    }
    public void setPitch(int index, SimpleDenotator pitchDeno) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setPitch(pitchDeno);
    }
    public void setLoudness(int index, SimpleDenotator loudnessDeno) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setLoudness(loudnessDeno);
    }
    public void setDuration(int index, SimpleDenotator durationDeno) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setDuration(durationDeno);
    }
    public void setVoice(int index, SimpleDenotator voiceDeno) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Note note = new Note(scoreDeno.getFactor(index));
        note.setVoice(voiceDeno);
    }
    /************Property(note related) END********************/



    /**********************Vari START*****************/
    private double getPitchForInv() {
        return 63.5;
    }
    //inv at the middle
    public void inv() {
        inv(getPitchForInv());
    }
    public void inv(double pitch) {
        int len = scoreDeno.getFactorCount();
        if (len == 0) return;
        for (int i = 0; i < scoreDeno.getFactorCount(); i++) {
            Note now = new Note(scoreDeno.getFactor(i));
            now.setPitch(pitch*2-now.getPitch());
        }
    }
    public void inv(Denotator pitchDenoGen) {
        SimpleDenotator pitchDeno= (SimpleDenotator)pitchDenoGen;
        inv(pitchDeno.getRational().doubleValue());
    }
    public void retro() {
        ifRev = !ifRev;
        int len = scoreDeno.getFactorCount();
        if (len == 0) return;
        Note lastNote = new Note(scoreDeno.getFactor(len-1));
        double timeTotal = lastNote.getOnset() + lastNote.getDuration();
        for (int i = 0; i < len; i++) {
            Note now = new Note(scoreDeno.getFactor(i));
            double timeEnding = now.getOnset() + now.getDuration();
            double timeNew = timeTotal-timeEnding;
            now.setOnset(timeNew);
        }
        //swap note to remain order
        for (int i = 0; i < len / 2; i++) {
            Denotator noteDeno = scoreDeno.getFactor(i);
            try {
                scoreDeno.setFactor(i, scoreDeno.getFactor(len-1-i));
                scoreDeno.setFactor(len-1-i, noteDeno);
            } catch (RubatoException e) {
                e.printStackTrace();
            }
        }
    }
    public void retroInv(double pitch) {
        int len = scoreDeno.getFactorCount();
        if (len == 0) return;
        retro();
        inv(pitch);
    }
    public Score getRetro() {
        int len = scoreDeno.getFactorCount();
        if (len == 0) return Score.getEmptyScore();
        List<Note> noteList = new ArrayList<Note>();
        Note lastNote = new Note(scoreDeno.getFactor(len-1));
        double timeTotal = lastNote.getOnset()+lastNote.getDuration();
        for (int i = 0; i < len; i++) {
            Note now = new Note(scoreDeno.getFactor(i), Status.CopyMode.COPY);
            double timeEnding = now.getOnset() + now.getDuration();
            double timeNew = timeTotal-timeEnding;
            now.setOnset(timeNew);
            noteList.add(now);
        }
        //swap note to remain order
        boolean newIfRev = !ifRev;
        for (int i = 0; i < len / 2; i++) {
            Note temp = noteList.get(i);
            noteList.set(i, noteList.get(len-1-i));
            noteList.set(len-1-i, temp);
        }
        return new Score(noteList, rankList, newIfRev);
    }
    public Score getInv() {
        return getInv(getPitchForInv());
    }
    public Score getInv(double pitch) {
        int len = scoreDeno.getFactorCount();
        if (len == 0) return Score.getEmptyScore();
        List<Note> noteList = new ArrayList<Note>();
        for (int i = 0; i < scoreDeno.getFactorCount(); i++) {
            Note now = new Note(scoreDeno.getFactor(i),Status.CopyMode.COPY);
            now.setPitch(pitch*2-now.getPitch());
            noteList.add(now);
        }
        return new Score(noteList, rankList, ifRev);
    }
    public Score getInv(Denotator pitchDenoGen) {
        SimpleDenotator pitchDeno = (SimpleDenotator)pitchDenoGen;
        return getInv(pitchDeno.getRational().doubleValue());
    }
    public Score getRetroInv() {
        double pitch = getPitchForInv();
        return getRetroInv(pitch);
    }
    public Score getRetroInv(double pitch) {
        int len = scoreDeno.getFactorCount();
        if (len == 0) return Score.getEmptyScore();
        Score scoreRetro = getRetro();
        return scoreRetro.getInv();
    }
    public Score getRetroInv(Denotator pitchDenoGen) {
        SimpleDenotator pitchDeno = (SimpleDenotator)pitchDenoGen;
        return getRetroInv(pitchDeno.getRational().doubleValue());
    }
    /**********************Vari END*****************/

    /*********************Target START*************/
    public void setWeight(int index, double weight) {
        weightList.set(index, weight);
    }
    public double getWeight(int index) {
        try {
            if (index < 0 || index > size()) {
                throw new Exception("wrong index");
            }
        } catch (Exception e) {
        }
        return weightList.get(index);
    }
    public double getTotalWeight() {
        return totalWeight;
    }
    public void setTotalWeight(double totalWeightT) {
        totalWeight = totalWeightT;
    }
    /*********************Target END*************/

    /*********************Debugging START********/
    public void printLong() {
        System.out.println("Score/Motif Length : " + size());
        for (int i = 0; i < scoreDeno.getFactorCount(); i++) {
            Note note = new Note(scoreDeno.getFactor(i));
            note.printLong();
        }
        System.out.println();
    }
    public void print() {
        if (ifMotif) {
            System.out.println("Motif Length : " + size());
        } else {
            System.out.println("Score Length : " + size());
        }
        for (int noteId = 0; noteId < scoreDeno.getFactorCount(); noteId++) {
            Note note = new Note(scoreDeno.getFactor(noteId));
            note.print();
            if (!ifMotif) {
                System.out.println("note weight: " + getWeight(noteId));
            }
            if (ifMotif) {
                System.out.println("note rank: " + rankList.get(noteId));
            }
            if (ifRev) {
                System.out.println("reverted score");
            }
        }
        if (ifMotif) {
            System.out.println("Motif Weight: " + getTotalWeight());
        }
        System.out.println();
    }

    @Override
    public String toString() {
        return "I'm a motif.";
    }
    /*********************Debugging END**********/
}
