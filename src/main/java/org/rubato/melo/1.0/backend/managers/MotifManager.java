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
 * You should have received a copy of the GNU General Public * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 * */

package org.rubato.melo;
import java.util.*;
import java.lang.Double;
import java.lang.Math;
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

public class MotifManager {
    //Format
    protected SimpleForm weightForm;
    protected LimitForm noteForm;
    protected LimitForm weightedNoteForm;
    protected PowerForm weightedScoreForm;

    protected PowerForm motifForm;
    protected LimitForm weightedMotifForm; 
    protected PowerForm weightedMotifListForm;

    Score score;
    int noteLimit;
    double span;

    boolean ifInv, ifRetro, ifRetroInv;
    int shapeSelec;
    double neighbour;

    List<List<Score>> motifLib;
    List<Integer> motifLenLib;

    List<List<List<Score>>> variMotifLib;
    boolean[] variSelec;
    protected List<ShapeManager> shapeManagerList;


    /**************************BASIC START****************************/
    public MotifManager(PowerDenotator scoreDeno, int noteLimitT, double spanT) {
        this(scoreDeno, noteLimitT, spanT, Status.CopyMode.NOCOPY);
    }
    public MotifManager(PowerDenotator scoreDeno, int noteLimitT, double spanT, Status.CopyMode mode) {
        System.out.println("Before genForm");
        //scoreOrMotifForm is needed for genForm
        score = new Score(scoreDeno, mode);
        genForm();
        System.out.println("After genForm");

        noteLimit = Math.min(noteLimitT, scoreDeno.getFactorCount());
        System.out.println("After noteLimit");
        span = spanT;
        System.out.println("After span");
        motifLib = new ArrayList<List<Score>>();
        System.out.println("After motifLib");
        motifLenLib = new ArrayList<Integer>();
        System.out.println("After motifLenLib");
        System.out.println("Before calMotifLib");
        calMotifLib(score, noteLimit, span);
        System.out.println("After calMotifLib");
    }
    /**************************BASIC START****************************/

    /********************* Format START ***********************************/
    public void genForm() {
        //for weighted motif 
        motifForm = (PowerForm)Repository.systemRepository().getForm("Score");
        weightForm = (SimpleForm)Repository.systemRepository().getForm("Real");
        List<Form> weightedMotifFormList = new LinkedList<Form>();
        List<String> weightedMotifFormLabelList = new LinkedList<String>();
        weightedMotifFormList.add(motifForm);
        weightedMotifFormList.add(weightForm);
        weightedMotifFormLabelList.add("motif");
        weightedMotifFormLabelList.add("weight");
        weightedMotifForm = FormFactory.makeLimitForm("weightedMotif", weightedMotifFormList); 
        weightedMotifForm.setLabels(weightedMotifFormLabelList);
        weightedMotifListForm = FormFactory.makePowerForm("weightedMotifList", weightedMotifForm);

        //for weighted score 
        noteForm = (LimitForm)Repository.systemRepository().getForm("Note");
        List<Form> weightedNoteFormList = new LinkedList<Form>();
        List<String> weightedNoteFormLabelList = new LinkedList<String>();
        weightedNoteFormList.add(noteForm);
        weightedNoteFormList.add(weightForm);
        weightedNoteFormLabelList.add("Note");
        weightedNoteFormLabelList.add("Weight");
        weightedNoteForm = FormFactory.makeLimitForm("weightedNoteForm", weightedNoteFormList);
        weightedNoteForm.setLabels(weightedNoteFormLabelList);
        weightedScoreForm = FormFactory.makePowerForm("weightedScoreForm", weightedNoteForm);
    }

    public Denotator toWeightedOnsetListDeno() {
        if (shapeManagerList == null || shapeManagerList.get(shapeSelec) == null) {
            return null;
        }
        return shapeManagerList.get(shapeSelec).toWeightedOnsetListDeno();
    }

    public Denotator toWeightedMotifListDeno() {
        try {
            List<Denotator> weightedMotifListDenoList = new ArrayList<Denotator>();
            for (int shelfId = 0; shelfId < motifLib.size(); shelfId++) {
                List<Score> motifShelf = motifLib.get(shelfId);
                for (int motifId = 0; motifId < motifShelf.size(); motifId++) {
                    List<Denotator> weightedMotifDenoList = new ArrayList<Denotator>();
                    Score motif = motifShelf.get(motifId);
                    Denotator motifDeno = motif.toScoreDeno();
                    double weight = motif.getTotalWeight();
                    Denotator weightDeno = new SimpleDenotator(NameDenotator.make("weightDeno"), weightForm, new RElement(weight));
                    weightedMotifDenoList.add(motifDeno);
                    weightedMotifDenoList.add(weightDeno);
                    LimitDenotator weightedMotifDeno = new LimitDenotator(NameDenotator.make("weightedMotifDeno"), weightedMotifForm, weightedMotifDenoList);
                    weightedMotifListDenoList.add(weightedMotifDeno);
                }
            }
            PowerDenotator weightedMotifListDeno = new PowerDenotator(NameDenotator.make("weightedMotifListDeno"), weightedMotifListForm, weightedMotifListDenoList);
            return weightedMotifListDeno;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Denotator toWeightedScoreDeno() {
        try {
            List<Denotator> weightedScoreDenoList = new ArrayList<Denotator>();
            for (int noteId = 0; noteId < score.size(); noteId++) {
                Denotator noteDeno = score.getNoteDeno(noteId);
                double weight = score.getWeight(noteId);
                Denotator weightDeno = new SimpleDenotator(NameDenotator.make("weightDeno"), weightForm, new RElement(weight));
                List<Denotator> weightedNoteDenoList = new ArrayList<Denotator>();
                weightedNoteDenoList.add(noteDeno);
                weightedNoteDenoList.add(weightDeno);
                LimitDenotator weightedNoteDeno = new LimitDenotator(NameDenotator.make("weightedNote"), weightedNoteForm, weightedNoteDenoList);
                weightedScoreDenoList.add(weightedNoteDeno);
            }
            PowerDenotator weightedScoreDeno = new PowerDenotator(NameDenotator.make("weighedScoreDeno"), weightedScoreForm, weightedScoreDenoList);
            return weightedScoreDeno;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }
    /********************* Format END ***********************************/

    /********************* Internal Data Output BEGIN *****************************/
    public List<List<Score>> getMotifLib() {
        return motifLib;
    }
    public Score getScore() { 
        return score;
    }
    public List<WeightedOnset> getWeightedOnsetList() {
        if (shapeManagerList == null || shapeManagerList.get(shapeSelec) == null) {
            return null;
        }
        return shapeManagerList.get(shapeSelec).getWeightedOnsetList();
    }
    /********************* Internal Data Output END *******************************/
    
    /**************************CalMotifLib START**********************/
    void searchMotif(List<Integer> rankList, int currentIndex, int nextAvaliableId, int noteLimit, int shelfIndex, double span) {
        if (currentIndex == noteLimit) {
            Score motif = new Score(score, new ArrayList<Integer>(rankList)); 
            motifLib.get(shelfIndex).add(motif);
            return;
        }
        for (int i = nextAvaliableId; i<=score.size()-(noteLimit-currentIndex); i++) {  
            if (currentIndex!=0) {
                //skip if two notes has same onset
                double currentOnset = score.getOnset(i);
                double previousOnset = score.getOnset(rankList.get(currentIndex-1));
                if (currentOnset-previousOnset <=1e-8) {
                    continue;
                }

                //check span
                int smallestFinalIndex=i+noteLimit-currentIndex-1;
                double finalOnset=score.getOnset(smallestFinalIndex);
                int firstIndex = rankList.get(0);
                double firstOnset = score.getOnset(firstIndex); 
                if (finalOnset-firstOnset>span) break;
            }
            rankList.add(i);
            searchMotif(rankList, currentIndex+1, i+1, noteLimit, shelfIndex, span);
            rankList.remove(currentIndex);
        }
    }
    void calMotifLib(Score score, int noteLimit, double span) {
        System.out.println("into generate motives:");
        List<Integer> rankList = new ArrayList<Integer>();
        System.out.println("notes limit: " + noteLimit);
        System.out.println("span: " + span);
        for (int i = 1; i <= noteLimit; i++) {
            motifLib.add(new ArrayList<Score>());
            motifLenLib.add(i);
            System.out.println("before search:" + i);
            int currentIndex = 0;
            int nextAvaliableId = 0;
            int limit = i;
            int shelfIndex = i-1;
            searchMotif(rankList, currentIndex, nextAvaliableId, limit, shelfIndex, span); 
            System.out.println("after search:" + i);
            if (motifLib.get(motifLib.size()-1).size() == 0) {
                motifLib.remove(motifLib.size()-1);
                noteLimit = motifLib.size()-1;
                break;
            }
        }
        System.out.println("after search");
    }
    /**************************CalMotifLib END************************/

    /****************************CalWeight START*************************/
    //call by user UI Action  -> calWeight
    public void calVariSelec(boolean ifInv, boolean ifRetro, boolean ifRetroInv){ 
        System.out.println("calculating vari selec");
        System.out.println("ifInv: " + ifInv);
        System.out.println("ifRetro: " + ifRetro);
        System.out.println("ifRetroInv: " + ifRetroInv);
        variSelec = new boolean[4];
        for (int i = 0; i < 4; i++) {
            variSelec[i] = false;
        }
        variSelec[0] = true;
        int total = 0;
        if (ifInv) total++;
        if (ifRetro) total++;
        if (ifRetroInv) total++;
        if (total >= 2) {
            for (int i = 0; i < 4; i++) {
                variSelec[i] = true;
            }
        } else if (total == 1) {
            if (ifInv) {
                variSelec[1] = true;
            } else if (ifRetro) {
                variSelec[2] = true;
            } else if (ifRetroInv) {
                variSelec[3] = true;
            }
        }
        System.out.println("after calculating vari selec: " + total);
    }
    public void calVariMotifLib(List<List<Score>> motifLib, boolean[] variSelec) {
        System.out.println("calculating vari motif lib");
        List<List<Score>> invMotifLib = null, retroMotifLib = null, retroInvMotifLib = null;
        if (variSelec[1]) {
            invMotifLib = new ArrayList<List<Score>>();
        }
        if (variSelec[2]) {
            retroMotifLib = new ArrayList<List<Score>>();
        }
        if (variSelec[3]) {
            retroInvMotifLib = new ArrayList<List<Score>>();
        }
        for (int i = 0; i < motifLib.size(); i++) {
            List<Score> shelf = motifLib.get(i);
            List<Score> invShelf = new ArrayList<Score>();
            List<Score> retroShelf = new ArrayList<Score>();
            List<Score> retroInvShelf = new ArrayList<Score>();
            for (int j = 0; j < motifLib.get(i).size(); j++) {
                Score motif = shelf.get(j);
                if (variSelec[1]) {
                    Score invMotif = motif.getInv();
                    invShelf.add(invMotif);
                }
                if (variSelec[2]) {
                    Score retroMotif = motif.getRetro();
                    retroShelf.add(retroMotif);
                }
                if (variSelec[3]) {
                    Score retroInvMotif = motif.getRetroInv();
                    retroInvShelf.add(retroInvMotif);
                }
            }
            if (variSelec[1]) invMotifLib.add(invShelf);
            if (variSelec[2]) retroMotifLib.add(retroShelf);
            if (variSelec[3]) retroInvMotifLib.add(retroInvShelf);
        }
        variMotifLib = new ArrayList<List<List<Score>>>();
        variMotifLib.add(motifLib);
        variMotifLib.add(invMotifLib);
        variMotifLib.add(retroMotifLib);
        variMotifLib.add(retroInvMotifLib);
        System.out.println("after calculating variMotifLib");
    }
    public void genShapeManager(Score score, List<List<List<Score>>> variMotifLib, boolean[] variSelec, int shapeSelec, double neighbour) {
        System.out.println("gen shapeManager");
        if (shapeManagerList == null || shapeManagerList.isEmpty()) {
            shapeManagerList = new ArrayList<ShapeManager>();
            for (int i = 0; i < 3; i++) {
                shapeManagerList.add(null);
            }
        }
        //TODO
        switch (shapeSelec) {
            case 0:
                shapeManagerList.set(shapeSelec, new RigidShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour));
                break;
            case 1:
                shapeManagerList.set(shapeSelec, new DiaShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour));
                break;
            case 2:
                shapeManagerList.set(shapeSelec, new ElasShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour));
                break;
        }
        System.out.println("after gen shapeManager");
    }

    public void calWeight(boolean ifInvT, boolean ifRetroT, boolean ifRetroInvT, int shapeSelecT, double neighbourT) {
        ifInv = ifInvT;
        ifRetro = ifRetroT;
        ifRetroInv = ifRetroInvT;
        shapeSelec = shapeSelecT;
        neighbour = neighbourT;

        calVariSelec(ifInv, ifRetro, ifRetroInv);
        calVariMotifLib(motifLib, variSelec);
        genShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour);
        //test();
        //debugging
    }
    /****************************CalWeight END*************************/
    /****************************Reset Weight START***********************/

    void resetVariAndShape(boolean ifInv, boolean ifRetro, boolean ifRetroInv, int shapeSelec) {
        calWeight(ifInv, ifRetro, ifRetroInv, shapeSelec, neighbour);
    }

    void resetNeighbour(double neighbourT) {
        neighbourT = neighbour;
        genShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour);
    }

    void test() {
        //getIfContain Test
        System.out.println("test getIfContain");
        double[][] pointList1 = {{1, 0}, {2, 0}, {3, 0}};
        int[] rankList1 =  {0, 3, 4};
        boolean ifRev1 = false;
        int diffMax1= 3;
        double[][] pointList2 = {{2, 0}, {1, 0}};
        int[] rankList2 = {4, 3};
        boolean ifRev2 = true;
        int diffMax2 = 3;
        Shape s1 = new Shape(pointList1, 2, rankList1, ifRev1, diffMax1, true);
        Shape s2 = new Shape(pointList2, 2, rankList2, ifRev2, diffMax2, true);
        boolean test1 = s1.getIfContain(s2);
        System.out.println("if contain: " + test1);

        System.out.println("test inv/retro/retroInv");
        System.out.println("original score");
        int shelfId = 1;
        int motifId = 0;
        motifLib.get(shelfId).get(motifId).print();
        System.out.println("inverted score");
        Score invScore = motifLib.get(shelfId).get(motifId).getInv(); 
        invScore.print();
        System.out.println("retrograded score");
        Score retroScore = motifLib.get(shelfId).get(motifId).getRetro(); 
        retroScore.print();
        System.out.println("retroinverted score");
        Score retroInvScore = motifLib.get(shelfId).get(motifId).getRetroInv(); 
        retroInvScore.print();


        //System.out.println("test rigid/dia/elas shape");
        //System.out.println("test getDis rigid");
        //System.out.println("test getDis dia/elas");
    }

    /****************************Reset Weight END***********************/



    /********************* Debugging START ***********************************/
    public void print() {
        System.out.println("score: ");
        score.print();
        System.out.println("\nnotes' limit: " + noteLimit);
        System.out.println("motif library's' size: " + motifLib.size());
        for (int i = 0; i < motifLib.size(); i++) {
            System.out.println("Shelf " + (i+1) + " (Length = " + motifLenLib.get(i) + ")");
            for (int j = 0; j < motifLib.get(i).size(); j++) {
                motifLib.get(i).get(j).print();
            }
        }
    }
    /********************* Debugging END ***********************************/
}
